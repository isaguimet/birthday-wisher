package com.birthdaywisher.server.service;

import com.birthdaywisher.server.model.Board;
import com.birthdaywisher.server.model.Message;
import com.birthdaywisher.server.model.User;
import com.birthdaywisher.server.repository.BoardRepository;
import com.birthdaywisher.server.repository.UserRepository;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

    private final BoardRepository boardRepository;

    private final UserRepository userRepository;

    // server group
    private List<String> serverGroup = new ArrayList<>();
    private List<String> proxies = Arrays.asList("proxy1", "proxy2");

    @Value("${systemId}")
    private String systemId;

    public CommService(RestTemplate restTemplate, BoardRepository boardRepository, UserRepository userRepository) {
        this.restTemplate = restTemplate;
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public synchronized void removeServerFromGroup(String serverId) {
        serverGroup.remove(serverId);
        System.out.println("Server group: " + serverGroup);
    }

    public synchronized List<String> getServerGroup() {
        return serverGroup;
    }

    public synchronized void setServerGroup(String serverGroup) {
        List<String> servers = new ArrayList<>(Arrays.asList(serverGroup.split(",")));

        this.serverGroup = servers;
        System.out.println("Server group: " + this.serverGroup);
    }

    public void forwardUserReqToBackups(User user, String typeOfRequest) {
        if ("signUp".equals(typeOfRequest)) {
            addUserPOSTRequest(user);
        }
    }

    public void forwardUserReqToBackups(ObjectId id, String typeOfRequest) {
        if ("delete".equals(typeOfRequest)) {
            deleteUserRequest(id);
        }
    }

    public void forwardUserReqToBackups(String userEmail, String friendEmail, String typeOfRequest) {
        if ("friendRequest".equals(typeOfRequest)) {
            sendFriendRequest(userEmail, friendEmail);
        }
    }

    public void forwardUserReqToBackups(ObjectId userId, String friendEmail, String typeOfRequest) {
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
                System.out.println("Failed to connect to server backup: " + e.getMessage());
                removeBackupServerFromReplicas(replicaURI);
            }
        }

        for (Future<String> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                System.out.println("Future.get() exception " + e.getMessage());
            }
        }
    }

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

    private void addUserPOSTRequest(User user) {
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
                System.out.println("Failed to connect to server backup: " + e.getMessage());
                removeBackupServerFromReplicas(replicaURI);
            }
        }

        for (Future<String> future : futures) {
            try {
                String result = future.get();
                System.out.println("future result: " + result);
            } catch (Exception e) {
                // I am not sure when this would be caught or if we have ever caught this exception before, but we can keep it
                System.out.println("Future.get() exception " + e.getMessage());
            }
        }
    }

    private void deleteUserRequest(ObjectId userId) {
        List<URI> replicaURIs = buildURIForEachReplica("https://%s-ey7sfy2hcq-wl.a.run.app/users/forwarded/");

        List<Future<String>> futures = new ArrayList<>();
        for (URI replicaURI : replicaURIs) {
            try {
                futures.add(asyncDelete(replicaURI + String.valueOf(userId)));
            } catch (Exception e) {
                System.out.println("Failed to connect to server backup: " + e.getMessage());
                removeBackupServerFromReplicas(replicaURI);
            }
        }

        for (Future<String> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                System.out.println("Future.get() exception " + e.getMessage());
            }
        }
    }

    private void sendFriendRequest(String userEmail, String friendEmail) {
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
                System.out.println("Failed to connect to server backup: " + e.getMessage());
                removeBackupServerFromReplicas(replicaURI);
            }
        }

        for (Future<String> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                System.out.println("Future.get() exception " + e.getMessage());
            }
        }
    }

    public void forwardCreateBoard(Board board) {
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
                System.out.println("Failed to connect to server backup: " + e.getMessage());
                removeBackupServerFromReplicas(replicaURI);
            }
        }

        for (Future<String> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                System.out.println("Future.get() exception " + e.getMessage());
            }
        }
    }

    public void forwardCreateMessage(ObjectId boardId, Message msg) {
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
                System.out.println("Failed to connect to server backup: " + e.getMessage());
                removeBackupServerFromReplicas(replicaURI);
            }
        }

        for (Future<String> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                System.out.println("Future.get() exception " + e.getMessage());
            }
        }
    }

    public void forwardBoardPatch(String url) {
        List<URI> replicaURIs = buildURIForEachReplica(url);

        HttpEntity<String> request = new HttpEntity<>(null, null);

        List<Future<String>> futures = new ArrayList<>();
        for (URI replicaURI : replicaURIs) {
            try {
                futures.add(asyncPatchForObject(replicaURI, request));
            } catch (Exception e) {
                System.out.println("Failed to connect to server backup: " + e.getMessage());
                removeBackupServerFromReplicas(replicaURI);
            }
        }

        for (Future<String> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                System.out.println("Future.get() exception " + e.getMessage());
            }
        }
    }

    public void forwardUpdateMessage(ObjectId boardId, ObjectId msgId, Map<String, String> payload) {
        List<URI> replicaURIs = buildURIForEachReplica("https://%s-ey7sfy2hcq-wl.a.run.app/boards/forwarded/" + boardId + "/messages/" + msgId);

        HttpEntity<?> request = new HttpEntity<>(payload, null);

        List<Future<String>> futures = new ArrayList<>();
        for (URI replicaURI : replicaURIs) {
            try {
                futures.add(asyncPatchForObject(replicaURI, request));
            } catch (Exception e) {
                System.out.println("Failed to connect to server backup: " + e.getMessage());
                removeBackupServerFromReplicas(replicaURI);
            }
        }

        for (Future<String> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                System.out.println("Future.get() exception " + e.getMessage());
            }
        }
    }

    public void forwardDeleteReq(String url) {
        List<URI> replicaURIs = buildURIForEachReplica(url);

        List<Future<String>> futures = new ArrayList<>();
        for (URI replicaURI : replicaURIs) {
            try {
                futures.add(asyncDelete(replicaURI));
            } catch (Exception e) {
                System.out.println("Failed to connect to server backup: " + e.getMessage());
                removeBackupServerFromReplicas(replicaURI);
            }
        }

        for (Future<String> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                System.out.println("Future.get() exception " + e.getMessage());
            }
        }
    }

    private void removeBackupServerFromReplicas(URI replicaURI) {
        String backupServerId = replicaURI.getPath().split("//")[1].split("-")[0];
        System.out.println("Server to remove: " + backupServerId);

        HttpEntity<?> httpEntity = new HttpEntity<>(null, null);

        // remove backup server from my own group (primary server)
        removeServerFromGroup(backupServerId);

        // remove backup server from all other backup servers
        for (String serverReplicaPort : getServerGroup()) {
            if (!serverReplicaPort.equals(getSystemId())) {
                // do not call endpoint to remove crashed server on primary server (we just removed it)
                String url = "https://%s-ey7sfy2hcq-wl.a.run.app/comm/removeServerFromGroup/" + backupServerId;
                URI portUri = URI.create(String.format(url, serverReplicaPort));
                System.out.println("Removing " + backupServerId + " from server group at server " + serverReplicaPort);
                restTemplate.exchange(portUri, HttpMethod.PATCH, httpEntity, String.class);
            }
        }

        // remove backup server from all proxies
        for (String proxyPort : proxies) {
            String url = "https://%s-ey7sfy2hcq-wl.a.run.app/removeServerFromGroup/" + backupServerId;
            URI portUri = URI.create(String.format(url, proxyPort));
            System.out.println("Removing " + backupServerId + " from server group at proxy " + proxyPort);
            restTemplate.exchange(portUri, HttpMethod.PATCH, httpEntity, String.class);
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
