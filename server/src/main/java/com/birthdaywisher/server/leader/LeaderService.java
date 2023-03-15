package com.birthdaywisher.server.leader;

import com.birthdaywisher.server.model.Board;
import com.birthdaywisher.server.model.Message;
import com.birthdaywisher.server.model.User;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LeaderService {

    private final RestTemplate restTemplate;

    private final ServerProperties serverProperties;

    // server group
    private final List<Integer> serverGroup = new ArrayList<>(Arrays.asList(8080, 8081, 8082));

    private final Pattern pattern = Pattern.compile("I/O error on (POST|GET|PATCH|DELETE) request for \"http://localhost:(\\d{4,})/users/signUp\": " +
            "Connect to http://localhost:(\\d{4,}) \\[localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1\\] failed: Connection refused");

    public LeaderService(ServerProperties serverProperties, RestTemplate restTemplate) {
        this.serverProperties = serverProperties;
        this.restTemplate = restTemplate;
    }

    public boolean isLeader() {
        // check if this is a leader somehow for now just getting ports
        return serverProperties.getPort() == 8080;
    }

    public void forwardUserReqToBackups(User user, String typeOfRequest) {
        if (isLeader()) {
            int response = 0;

            if ("signUp".equals(typeOfRequest)) {
                addUserPOSTRequest(user, response);
            }
        }
    }

    public void forwardUserReqToBackups(ObjectId id, String typeOfRequest) {
        if (isLeader()) {
            int response = 0;

            if ("delete".equals(typeOfRequest)) {
                deleteUserRequest(id, response);
            }
        }
    }

    public void forwardUserReqToBackups(String userEmail, String friendEmail, String typeOfRequest) {
        if (isLeader()) {
            int response = 0;

            if ("friendRequest".equals(typeOfRequest)) {
                sendFriendRequest(userEmail, friendEmail, response);
            }
        }
    }

    public void forwardUserReqToBackups(ObjectId userId, String friendEmail, String typeOfRequest) {
        if (isLeader()) {
            int response = 0;

            URI uri = URI.create("");
            if ("acceptFriendRequest".equals(typeOfRequest)) {
                uri = URI.create("http://localhost/users/pendingFriendRequests/accept");
            }
            if ("declineFriendRequest".equals(typeOfRequest)) {
                uri = URI.create("http://localhost/users/pendingFriendRequests/decline");
            }

            List<URI> replicaURIs = buildURIForEachReplica(uri);
            HttpEntity<JSONObject> request = new HttpEntity<>(null, null);

            List<Future<String>> futures = new ArrayList<>();
            for (URI replicaURI : replicaURIs) {
                String url = UriComponentsBuilder.fromUri(replicaURI)
                        .queryParam("userId", userId)
                        .queryParam("friendEmail", friendEmail)
                        .encode()
                        .toUriString();

                futures.add(asyncPatchForObject(url, request));
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
    }

    private List<URI> buildURIForEachReplica(URI uri) {
        List<URI> replicaURIs = new ArrayList<>();
        for (Integer port : serverGroup) {
            if (!Objects.equals(port, serverProperties.getPort())) {
                uri = UriComponentsBuilder.fromUri(uri).port(port).build().toUri();
                replicaURIs.add(uri);
            }
        }
        return replicaURIs;
    }

    private void addUserPOSTRequest(User user, int response) {
        URI uri = URI.create("http://localhost/users/signUp");
        List<URI> replicaURIs = buildURIForEachReplica(uri);

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
        try {
            for (URI replicaURI : replicaURIs) {
                futures.add(asyncPostForObject(replicaURI, request));
            }
        }
        catch (Exception e) {
            Matcher matcher = pattern.matcher(e.getMessage());
            if (matcher.find()) {
                Integer portToRemove = Integer.valueOf(matcher.group(2));
                try {
                    serverGroup.remove(portToRemove);
                } catch (Exception e1) {
                    System.out.println("Error from removing port: " + e1.getMessage());
                }
                System.out.println("server group: " + serverGroup);
            } else {
                System.out.println("did not match");
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
        URI uri = URI.create("http://localhost/users/");
        List<URI> replicaURIs = buildURIForEachReplica(uri);

        List<Future<String>> futures = new ArrayList<>();
        for (URI replicaURI : replicaURIs) {
            futures.add(asyncDelete(replicaURI + String.valueOf(userId)));
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
        URI uri = URI.create("http://localhost/users/friendRequest");
        List<URI> replicaURIs = buildURIForEachReplica(uri);

        HttpEntity<JSONObject> request = new HttpEntity<>(null, null);

        List<Future<String>> futures = new ArrayList<>();
        for (URI replicaURI : replicaURIs) {
            String url = UriComponentsBuilder.fromUri(replicaURI)
                    .queryParam("userEmail", userEmail)
                    .queryParam("friendEmail", friendEmail)
                    .encode()
                    .toUriString();

            futures.add(asyncPatchForObject(url, request));
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
        if (isLeader()) {
            int response = 0;

            URI uri1 = URI.create("http://localhost/boards");
            List<URI> replicaURIs = buildURIForEachReplica(uri1);

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
                futures.add(asyncPostForObject(replicaURI, request));
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
    }

    public void forwardCreateMessage(ObjectId boardId, Message msg) {
        if (isLeader()) {
            int response = 0;

            URI uri1 = URI.create("http://localhost/boards/" + boardId + "/messages");

            List<URI> replicaURIs = buildURIForEachReplica(uri1);

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
                futures.add(asyncPostForObject(replicaURI, request));
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
    }

    public void forwardBoardPatch(String url) {
        if (isLeader()) {
            int response = 0;

            URI uri1 = URI.create(url);
            List<URI> replicaURIs = buildURIForEachReplica(uri1);

            HttpEntity<String> request = new HttpEntity<>(null, null);

            List<Future<String>> futures = new ArrayList<>();
            for (URI replicaURI : replicaURIs) {
                futures.add(asyncPatchForObject(replicaURI, request));
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
    }

    public void forwardUpdateMessage(ObjectId boardId, ObjectId msgId, Map<String, String> payload) {
        if (isLeader()) {
            int response = 0;

            URI uri1 = URI.create("http://localhost/boards/" + boardId + "/messages/" + msgId);
            List<URI> replicaURIs = buildURIForEachReplica(uri1);

            HttpEntity<?> request = new HttpEntity<>(payload, null);

            List<Future<String>> futures = new ArrayList<>();
            for (URI replicaURI : replicaURIs) {
                futures.add(asyncPatchForObject(replicaURI, request));
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
    }

    public void forwardDeleteReq(String url) {
        if (isLeader()) {
            int response = 0;

            URI uri1 = URI.create(url);
            List<URI> replicaURIs = buildURIForEachReplica(uri1);

            List<Future<String>> futures = new ArrayList<>();
            for (URI replicaURI : replicaURIs) {
                futures.add(asyncDelete(replicaURI));
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
}
