package com.birthdaywisher.proxy.controller;

import com.birthdaywisher.proxy.service.ProxyService;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.parser.JSONParser;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
public class ProxyController {
    private ProxyService proxyService;
    private RestTemplate restTemplate;

    public ProxyController(ProxyService proxyService, RestTemplate restTemplate) {
        this.proxyService = proxyService;
        this.restTemplate = restTemplate;
    }

    @RequestMapping("/**")
    public ResponseEntity<?> forwardReq(@RequestBody(required = false) String body, HttpMethod method, HttpServletRequest request) {
        try {
            return proxyService.forwardReqToPrimary(body, method, request);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/serverRegistration/{serverId}")
    public ResponseEntity<?> serverRegistration(@PathVariable String serverId) {
        try {
            HttpEntity<String> httpEntity = new HttpEntity<>(null, null);

            if (!proxyService.getServers().isEmpty()) {
                // Registering server is a backup. It needs to sync its db with primary.
                try {
                    // fetch data dump from primary server
                    String primaryServerId = proxyService.getServers().iterator().next();
                    URI uri = URI.create(String.format("https://%s-ey7sfy2hcq-wl.a.run.app/comm/dataDump", primaryServerId));

                    System.out.println("Attempting to fetch data dump from: " + uri);
                    HttpEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
                    JSONParser parser = new JSONParser();
                    List<Iterable<?>> data = (List<Iterable<?>>) parser.parse(responseEntity.getBody());

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    uri = URI.create(String.format("https://%s-ey7sfy2hcq-wl.a.run.app/comm/dataReset", serverId));
                    System.out.println("Resetting data on server " + uri);
                    restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(data, headers), String.class);

                    proxyService.addServerToGroup(serverId);
                } catch (Exception e) {
                    System.out.println("Failed to sync registering server db with primary: " + e.getMessage());
                }
            } else {
                // Registering server is the primary, no need to do any db sync, just add it to the group.
                proxyService.addServerToGroup(serverId);
            }

            // Now tell the other proxy to add this server to their list too
            String addToGroupUrl = "https://%s-ey7sfy2hcq-wl.a.run.app/addServerToGroup/" + serverId;
            for(String proxyId : proxyService.getProxies()) {
                if (!proxyId.equals(proxyService.getSystemId())) {
                    try {
                        System.out.println("Adding " + serverId + " to server group on " + proxyId);
                        URI uri = URI.create(String.format(addToGroupUrl, proxyId));
                        restTemplate.exchange(uri, HttpMethod.PATCH, httpEntity, String.class);
                    } catch (HttpStatusCodeException e) {
                        System.out.println("Bad response from proxy: " + e.getStatusCode() + "\n" +
                                e.getResponseHeaders() + "\n" + e.getResponseBodyAsString());
                    } catch (Exception e) {
                        System.out.println("Failed to send request to " + proxyId + ": " + e.getMessage());
                    }
                }
            }

            // Add server to all servers in server group to their list
            String setServerToGroupUrl = "https://%s-ey7sfy2hcq-wl.a.run.app/comm/setServerGroup";
            Set<String> serverGroup = proxyService.getServers();
            String data = serverGroup.toString();

            httpEntity = new HttpEntity<>(data, null);
            for (String serverPort : serverGroup) {
                URI uri = URI.create(String.format(setServerToGroupUrl, serverPort));
                System.out.println("Setting server group " + serverGroup + " to server group on server " + serverPort);
                restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, String.class);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/addServerToGroup/{serverId}")
    public ResponseEntity<?> addServerToGroup(@PathVariable String serverId) {
        try {
            proxyService.addServerToGroup(serverId);
            return new ResponseEntity<>(proxyService.getServers(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/removeServerFromGroup/{serverId}")
    public ResponseEntity<?> removeServerFromGroup(@PathVariable String serverId) {
        try {
            proxyService.removeServerFromGroup(serverId);
            return new ResponseEntity<>(proxyService.getServers(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/serverGroup")
    public ResponseEntity<?> getServerGroupList() {
        try {
            return new ResponseEntity<>(proxyService.getServers(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
