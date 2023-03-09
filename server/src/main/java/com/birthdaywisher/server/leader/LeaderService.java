package com.birthdaywisher.server.leader;

import com.birthdaywisher.server.model.Board;
import com.birthdaywisher.server.model.User;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class LeaderService {

    private final RestTemplate restTemplate;

    private final ServerProperties serverProperties;

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

    private URI buildURIForEachReplica(URI uri) {
        if (uri.getPort() == -1) {
            uri = UriComponentsBuilder.fromUri(uri).port(8081).build().toUri();
        }
        return uri;
    }

    private void addUserPOSTRequest(User user, int response) {
        URI uri = URI.create("http://localhost/users/signUp");
        uri = buildURIForEachReplica(uri);

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

        String resultAsJsonStr = restTemplate.postForObject(uri, request, String.class);

        response++;

        // if response == number of replicas (for now), then we get all acks
        if (response == 1) {
            System.out.println("I have received 1 ACKS from the replicas");
        }
    }

    private void deleteUserRequest(ObjectId userId, int response) {
        URI uri = URI.create("http://localhost/users/");
        uri = buildURIForEachReplica(uri);

        restTemplate.delete(uri + String.valueOf(userId));
        response++;

        // if response == number of replicas (for now), then we get all acks
        if (response == 1) {
            System.out.println("I have received 1 ACK from the replicas");
        }
    }

    private void sendFriendRequest(String userEmail, String friendEmail, int response) {
        URI uri = URI.create("http://localhost/users/friendRequest");
        uri = buildURIForEachReplica(uri);

        HttpEntity<JSONObject> request = new HttpEntity<>(null, null);

        String url = UriComponentsBuilder.fromUri(uri)
                .queryParam("userEmail", userEmail)
                .queryParam("friendEmail", friendEmail)
                .encode()
                .toUriString();

        String result = restTemplate.patchForObject(url, request, String.class);
        response++;

        // if response == number of replicas (for now), then we get all acks
        if (response == 1) {
            System.out.println("I have received 1 ACK from the replicas");
        }
    }

    public void forwardBoardReqToBackups(Board board) {
        if (isLeader()) {
            int response = 0;

            URI uri1 = URI.create("http://localhost/boards");

            if (uri1.getPort() == -1) {
                uri1 = UriComponentsBuilder.fromUri(uri1).port(8081).build().toUri();
            }
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

            String resultAsJsonStr = restTemplate.postForObject(uri1, request, String.class);

            response++;

            // if response == number of replicas (for now), then we get all acks
            if (response == 1) {
                System.out.println(" I have received 1 ACKS from the replicas");
            }
        }
    }
}
