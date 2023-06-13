package com.birthdaywisher.proxy.controller;

import com.birthdaywisher.proxy.service.ProxyService;
import jakarta.servlet.http.HttpServletRequest;
import org.json.simple.parser.JSONParser;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ProxyController {
    private ProxyService proxyService;
    private RestTemplate restTemplate;
    private Integer myPortNum;

    public ProxyController(ProxyService proxyService, RestTemplate restTemplate, ServerProperties serverProperties) {
        this.proxyService = proxyService;
        this.restTemplate = restTemplate;
        this.myPortNum = serverProperties.getPort();
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

    @GetMapping("/serverRegistration/{portId}")
    public ResponseEntity<?> serverRegistration(@PathVariable Integer portId) {
        try {
            HttpEntity<String> httpEntity = new HttpEntity<>(null, null);

            if (!proxyService.getServers().isEmpty()) {
                // Registering server is a backup. It needs to sync its db with primary.
                try {
                    // fetch data dump from primary server
                    Integer serverPort = proxyService.getServers().get(0);
                    URI uri = URI.create("http://localhost/comm/dataDump");
                    URI portUri = UriComponentsBuilder.fromUri(uri).port(serverPort).build().toUri();

                    System.out.println("Attempting to fetch data dump from: " + portUri);
                    HttpEntity<String> responseEntity = restTemplate.exchange(portUri, HttpMethod.GET, httpEntity, String.class);
                    JSONParser parser = new JSONParser();
                    List<Iterable<?>> data = (List<Iterable<?>>) parser.parse(responseEntity.getBody());

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    uri = URI.create("http://localhost/comm/dataReset");
                    portUri = UriComponentsBuilder.fromUri(uri).port(portId).build().toUri();
                    System.out.println("Resetting data on server " + portUri);
                    restTemplate.exchange(portUri, HttpMethod.PUT, new HttpEntity<>(data, headers), String.class);

                    proxyService.addServerToGroup(portId);
                } catch (Exception e) {
                    System.out.println("Failed to sync registering server db with primary: " + e.getMessage());
                }
            } else {
                // Registering server is the primary, no need to do any db sync, just add it to the group.
                proxyService.addServerToGroup(portId);
            }

            // Now tell the other proxy to add this server to their list too
            URI uri = URI.create("http://localhost/addServerToGroup/" + portId);
            for(Integer proxyPort : proxyService.getProxies()) {
                if (proxyPort.intValue() != myPortNum.intValue()) {
                    try {
                        System.out.println("Adding server " + portId + " to server group on proxy " + proxyPort);
                        URI portUri = UriComponentsBuilder.fromUri(uri).port(proxyPort).build().toUri();
                        restTemplate.exchange(portUri, HttpMethod.PATCH, httpEntity, String.class);
                    } catch (HttpStatusCodeException e) {
                        System.out.println("Bad response from proxy: " + e.getStatusCode() + "\n" +
                                e.getResponseHeaders() + "\n" + e.getResponseBodyAsString());
                    } catch (Exception e) {
                        System.out.println("Failed to send request to proxy " + proxyPort + ": " + e.getMessage());
                    }
                }
            }

            // Add server to all servers in server group to their list
            uri = URI.create("http://localhost/comm/setServerGroup");
            List<Integer> serverGroup = proxyService.getServers();
            String data = serverGroup.stream().map(String::valueOf).collect(Collectors.joining(","));

            httpEntity = new HttpEntity<>(data, null);
            for (Integer serverPort : serverGroup) {
                URI portUri = UriComponentsBuilder.fromUri(uri).port(serverPort).build().toUri();
                System.out.println("Setting server group " + serverGroup + " to server group on server " + serverPort);
                restTemplate.exchange(portUri, HttpMethod.PUT, httpEntity, String.class);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/addServerToGroup/{portNum}")
    public ResponseEntity<?> addServerToGroup(@PathVariable Integer portNum) {
        try {
            proxyService.addServerToGroup(portNum);
            return new ResponseEntity<>(proxyService.getServers(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/removeServerFromGroup/{portNum}")
    public ResponseEntity<?> removeServerFromGroup(@PathVariable Integer portNum) {
        try {
            proxyService.removeServerFromGroup(portNum);
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
