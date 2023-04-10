package com.birthdaywisher.server.service;

import com.birthdaywisher.server.model.Board;
import com.birthdaywisher.server.model.Message;
import com.birthdaywisher.server.model.User;
import com.birthdaywisher.server.repository.BoardRepository;
import com.birthdaywisher.server.repository.UserRepository;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.concurrent.Future;

/**
 * A service for the server to communicate with proxies and other servers.
 */
@Service
public class CommService {

    private final RestTemplate restTemplate;

    private final ServerProperties serverProperties;

    private final BoardRepository boardRepository;

    private final UserRepository userRepository;

    // server group
    private final List<String> serverGroup = new ArrayList<>();

    @Value("${systemId}")
    private String systemId;

    public CommService(
            ServerProperties serverProperties, RestTemplate restTemplate, BoardRepository boardRepository,
            UserRepository userRepository
    ) {
        this.serverProperties = serverProperties;
        this.restTemplate = restTemplate;
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public synchronized void addServerToGroup(String serverId) {
        serverGroup.add(serverId);
    }

    public synchronized void removeServerFromGroup(String serverId) {
        serverGroup.remove(serverId);
    }

    public synchronized List<String> getServerGroup() {
        return serverGroup;
    }

    public void forwardUserReqToBackups(User user, String typeOfRequest) {
        int response = 0;

        if ("signUp".equals(typeOfRequest)) {
            addUserPOSTRequest(user, response);
        }
    }

    public void forwardUserReqToBackups(ObjectId id, String typeOfRequest) {
        int response = 0;

        if ("delete".equals(typeOfRequest)) {
            deleteUserRequest(id, response);
        }
    }

    public void forwardUserReqToBackups(String userEmail, String friendEmail, String typeOfRequest) {
        int response = 0;

        if ("friendRequest".equals(typeOfRequest)) {
            sendFriendRequest(userEmail, friendEmail, response);
        }
    }

    public void forwardUserReqToBackups(ObjectId userId, String friendEmail, String typeOfRequest) {
        int response = 0;

        String urlTemplate = "";
        if ("acceptFriendRequest".equals(typeOfRequest)) {
            urlTemplate = "https://%s-ey7sfy2hcq-wl.a.run.app/users/forwarded/pendingFriendRequests/accept";
        }
        if ("declineFriendRequest".equals(typeOfRequest)) {
            urlTemplate = "https://%s-ey7sfy2hcq-wl.a.run.app/users/forwarded/pendingFriendRequests/decline";
        }

        List<URI> replicaURIs = buildURIForEachReplica(urlTemplate);
        HttpEntity<JSONObject> request = new HttpEntity<>(null, null);

        List<Future<String>> futures = new ArrayList<>();
        for (URI replicaURI : replicaURIs) {
            String url = UriComponentsBuilder.fromUri(replicaURI)
                    .queryParam("userId", userId)
                    .queryParam("friendEmail", friendEmail)
                    .encode()
                    .toUriString();

            try {
                futures.add(asyncPatchForObject(url, request));
            } catch (Exception e) {
                // handle?
            }
        }

        for (Future<String> future : futures) {
            try {
                future.get();
                response++;
            } catch (Exception e) {
                // handle the exception??
            }
        }

        // if response == number of replicas (for now), then we get all acks
        if (response == 1) {
            System.out.println("I have received 1 ACK from the replicas");
        }
    }

    // TODO: this reads serverGroup, so should it be synchronized too?
    private List<URI> buildURIForEachReplica(String urlTemplate) {
        List<URI> replicaURIs = new ArrayList<>();
        for (String serverId : serverGroup) {
            if (!serverId.equals(systemId)) {
                URI uri = URI.create(String.format(urlTemplate, serverId));
                replicaURIs.add(uri);
            }
        }
        return replicaURIs;
    }

    private void addUserPOSTRequest(User user, int response) {
        List<URI> replicaURIs = buildURIForEachReplica("https://%s-ey7sfy2hcq-wl.a.run.app/users/forwarded/signUp");

        // call post endpoint of other replicas
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject obj = new JSONObject();
        obj.put("id", user.getId().toString());
        obj.put("firstName", user.getFirstName());
        obj.put("lastName", user.getLastName());
        obj.put("email", user.getEmail());
        obj.put("password", user.getPassword());
        obj.put("birthdate", user.getBirthdate().toString());

        HttpEntity<JSONObject> request = new HttpEntity<>(obj, headers);

        List<Future<String>> futures = new ArrayList<>();
        for (URI replicaURI : replicaURIs) {
            try {
                futures.add(asyncPostForObject(replicaURI, request));
            } catch (Exception e) {
                // handle?
            }
        }

        for (Future<String> future : futures) {
            try {
                String result = future.get();
                System.out.println("future result: " + result);
                response++;
            } catch (Exception e) {
                // handle the exception??
            }
        }

        // if response == number of replicas (for now), then we get all acks
        if (response == 1) {
            System.out.println("I have received 1 ACKS from the replicas");
        }
    }

    private void deleteUserRequest(ObjectId userId, int response) {
        List<URI> replicaURIs = buildURIForEachReplica("https://%s-ey7sfy2hcq-wl.a.run.app/users/forwarded/");

        List<Future<String>> futures = new ArrayList<>();
        for (URI replicaURI : replicaURIs) {
            try {
                futures.add(asyncDelete(replicaURI + String.valueOf(userId)));
            } catch (Exception e) {
                // handle?
            }
        }

        for (Future<String> future : futures) {
            try {
                future.get();
                response++;
            } catch (Exception e) {
                // handle the exception??
            }
        }

        // if response == number of replicas (for now), then we get all acks
        if (response == 1) {
            System.out.println("I have received 1 ACK from the replicas");
        }
    }

    private void sendFriendRequest(String userEmail, String friendEmail, int response) {
        List<URI> replicaURIs = buildURIForEachReplica("https://%s-ey7sfy2hcq-wl.a.run.app/users/forwarded/friendRequest");

        HttpEntity<JSONObject> request = new HttpEntity<>(null, null);

        List<Future<String>> futures = new ArrayList<>();
        for (URI replicaURI : replicaURIs) {
            String url = UriComponentsBuilder.fromUri(replicaURI)
                    .queryParam("userEmail", userEmail)
                    .queryParam("friendEmail", friendEmail)
                    .encode()
                    .toUriString();
            try {
                futures.add(asyncPatchForObject(url, request));
            } catch (Exception e) {
                // handle?
            }
        }

        for (Future<String> future : futures) {
            try {
                future.get();
                response++;
            } catch (Exception e) {
                // handle the exception??
            }
        }

        // if response == number of replicas (for now), then we get all acks
        if (response == 1) {
            System.out.println("I have received 1 ACK from the replicas");
        }
    }

    public void forwardCreateBoard(Board board) {
        int response = 0;

        List<URI> replicaURIs = buildURIForEachReplica("https://%s-ey7sfy2hcq-wl.a.run.app/boards/forwarded");

        // call post endpoint of other replicas
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject obj = new JSONObject();
        obj.put("id", board.getId().toString());
        obj.put("isPublic", String.valueOf(board.isPublic()));
        obj.put("isOpen", String.valueOf(board.isOpen()));
        obj.put("year", board.getYear());
        obj.put("userId", board.getUserId().toString());

        HttpEntity<JSONObject> request = new HttpEntity<>(obj, headers);

        List<Future<String>> futures = new ArrayList<>();
        for (URI replicaURI : replicaURIs) {
            try {
                futures.add(asyncPostForObject(replicaURI, request));
            } catch (Exception e) {
                // handle?
            }
        }

        for (Future<String> future : futures) {
            try {
                future.get();
                response++;
            } catch (Exception e) {
                // handle the exception??
            }
        }

        // if response == number of replicas (for now), then we get all acks
        if (response == 1) {
            System.out.println(" I have received 1 ACKS from the replicas");
        }
    }

    public void forwardCreateMessage(ObjectId boardId, Message msg) {
        int response = 0;

        List<URI> replicaURIs = buildURIForEachReplica("https://%s-ey7sfy2hcq-wl.a.run.app/boards/forwarded/" + boardId + "/messages");

        // call post endpoint of other replicas
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject obj = new JSONObject();
        obj.put("id", msg.getId().toString());
        obj.put("fromUserId", msg.getFromUserId().toString());
        obj.put("toUserId", msg.getToUserId().toString());
        obj.put("lastUpdatedDate", msg.getLastUpdatedDate().toString());
        obj.put("msgText", msg.getMsgText());

        HttpEntity<JSONObject> request = new HttpEntity<>(obj, headers);

        List<Future<String>> futures = new ArrayList<>();
        for (URI replicaURI : replicaURIs) {
            try {
                futures.add(asyncPostForObject(replicaURI, request));
            } catch (Exception e) {
                // handle?
            }
        }

        for (Future<String> future : futures) {
            try {
                future.get();
                response++;
            } catch (Exception e) {
                // handle the exception??
            }
        }

        // if response == number of replicas (for now), then we get all acks
        if (response == 1) {
            System.out.println(" I have received 1 ACKS from the replicas");
        }
    }

    public void forwardBoardPatch(String url) {
        int response = 0;

        List<URI> replicaURIs = buildURIForEachReplica(url);

        HttpEntity<String> request = new HttpEntity<>(null, null);

        List<Future<String>> futures = new ArrayList<>();
        for (URI replicaURI : replicaURIs) {
            try {
                futures.add(asyncPatchForObject(replicaURI, request));
            } catch (Exception e) {
                // handle?
            }
        }

        for (Future<String> future : futures) {
            try {
                future.get();
                response++;
            } catch (Exception e) {
                // handle the exception??
            }
        }

        // if response == number of replicas (for now), then we get all acks
        if (response == 1) {
            System.out.println(" I have received 1 ACKS from the replicas");
        }
    }

    public void forwardUpdateMessage(ObjectId boardId, ObjectId msgId, Map<String, String> payload) {
        int response = 0;

        List<URI> replicaURIs = buildURIForEachReplica("https://%s-ey7sfy2hcq-wl.a.run.app/boards/forwarded/" + boardId + "/messages/" + msgId);

        HttpEntity<?> request = new HttpEntity<>(payload, null);

        List<Future<String>> futures = new ArrayList<>();
        for (URI replicaURI : replicaURIs) {
            try {
                futures.add(asyncPatchForObject(replicaURI, request));
            } catch (Exception e) {
                // handle?
            }
        }

        for (Future<String> future : futures) {
            try {
                future.get();
                response++;
            } catch (Exception e) {
                // handle the exception??
            }
        }

        // if response == number of replicas (for now), then we get all acks
        if (response == 1) {
            System.out.println(" I have received 1 ACKS from the replicas");
        }
    }

    public void forwardDeleteReq(String url) {
        int response = 0;

        List<URI> replicaURIs = buildURIForEachReplica(url);

        List<Future<String>> futures = new ArrayList<>();
        for (URI replicaURI : replicaURIs) {
            try {
                futures.add(asyncDelete(replicaURI));
            } catch (Exception e) {
                // handle?
            }
        }

        for (Future<String> future : futures) {
            try {
                future.get();
                response++;
            } catch (Exception e) {
                // handle the exception??
            }
        }

        // if response == number of replicas (for now), then we get all acks
        if (response == 1) {
            System.out.println(" I have received 1 ACKS from the replicas");
        }
    }

    @Async
    public Future<String> asyncPatchForObject(URI uri, Object request) {
        return CompletableFuture.completedFuture(restTemplate.patchForObject(uri, request, String.class));
    }

    @Async
    public Future<String> asyncPatchForObject(String url, Object request) {
        return CompletableFuture.completedFuture(restTemplate.patchForObject(url, request, String.class));
    }

    @Async
    public Future<String> asyncPostForObject(URI uri, Object request) {
        return CompletableFuture.completedFuture(restTemplate.postForObject(uri, request, String.class));
    }

    @Async
    public Future<String> asyncDelete(URI uri) {
        restTemplate.delete(uri);
        return CompletableFuture.completedFuture("Done!");
    }

    @Async
    public Future<String> asyncDelete(String url) {
        restTemplate.delete(url);
        return CompletableFuture.completedFuture("Done!");
    }

    public List<Iterable<?>> dataDump() {
        List<Iterable<?>> dataList = new ArrayList<>();
        dataList.add(userRepository.findAll());
        dataList.add(boardRepository.findAll());
        return dataList;
    }

    public void dataReset(List<Iterable<?>> data) {
        // Wipe all collections & repopulate them with given data
        userRepository.deleteAll();
        boardRepository.deleteAll();

        List<User> userList = new ArrayList<>();
        for (Object userObject : data.get(0)) {
            LinkedHashMap object = (LinkedHashMap) userObject;
            User user = new User(new ObjectId((String) object.get("id")),
                    (String) object.get("firstName"),
                    (String) object.get("lastName"),
                    (String) object.get("email"),
                    (String) object.get("password"),
                    LocalDate.parse((String) object.get("birthdate")),
                    (ArrayList<ObjectId>) object.get("friends"),
                    (HashMap<ObjectId, Boolean>) object.get("pendingFriends"),
                    (String) object.get("profilePic"));
            userList.add(user);
        }

        List<Board> boardList = new ArrayList<>();
        for (Object boardObject : data.get(1)) {
            LinkedHashMap object = (LinkedHashMap) boardObject;
            Board board = new Board(
                    new ObjectId((String) object.get("id")),
                    (boolean) object.get("public"),
                    (boolean) object.get("open"),
                    (String) object.get("year"),
                    new ObjectId((String) object.get("userId")),
                    (Map<ObjectId, Message>) object.get("messages"));
            boardList.add(board);
        }

        userRepository.saveAll(userList);
        boardRepository.saveAll(boardList);
    }
}
