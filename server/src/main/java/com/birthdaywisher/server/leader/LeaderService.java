package com.birthdaywisher.server.leader;

import com.birthdaywisher.server.model.Board;
import com.birthdaywisher.server.model.Message;
import com.birthdaywisher.server.model.User;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class LeaderService {

    @Autowired
    private RestTemplate restTemplate;

    private final ServerProperties serverProperties;

    public LeaderService(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }
    public boolean isLeader() {
        // check if this is a leader somehow for now just getting ports
        return serverProperties.getPort() == 8080;
    }

    public void forwardUserReqToBackups(User user) {
        if (isLeader()) {
            int response = 0;

            URI uri1 = URI.create("http://localhost/users/signUp");

            if (uri1.getPort() == -1) {
                uri1 = UriComponentsBuilder.fromUri(uri1).port(8081).build().toUri();
            }
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

            String resultAsJsonStr = restTemplate.postForObject(uri1, request, String.class);

            response++;

            // if response == number of replicas (for now), then we get all acks
            if (response == 1) {
                System.out.println(" I have received 1 ACKS from the replicas");
            }
        }
    }

    public void forwardCreateBoard(Board board) {
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
    public void forwardCreateMessage(ObjectId boardId, Message msg) {
        if (isLeader()) {
            int response = 0;

            URI uri1 = URI.create("http://localhost/boards/" + boardId + "/messages");

            if (uri1.getPort() == -1) {
                uri1 = UriComponentsBuilder.fromUri(uri1).port(8081).build().toUri();
            }
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

            String resultAsJsonStr = restTemplate.postForObject(uri1, request, String.class);

            response++;

            // if response == number of replicas (for now), then we get all acks
            if (response == 1) {
                System.out.println(" I have received 1 ACKS from the replicas");
            }
        }
    }
}