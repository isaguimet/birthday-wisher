package com.birthdaywisher.proxy.controller;

import com.birthdaywisher.proxy.service.ProxyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

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

    @GetMapping("/serverRegistration/{portId}")
    public ResponseEntity<?> serverRegistration(@PathVariable Integer portId) {
        try {
            if (!proxyService.getServers().isEmpty()) {
                // send DB copy from one of the servers in that list to the server about to register
                Integer serverPort = proxyService.getServers().get(0);
                // call endpoint of getting users from this server's DB's

                HttpEntity<String> responseEntity;
                try {
                    HttpEntity<String> httpEntity = new HttpEntity<>(null, null);
                    URI uri = URI.create("http://localhost/users/");
                    URI portUri = UriComponentsBuilder.fromUri(uri).port(serverPort).build().toUri();
                    System.out.println("URI to retrieve DB copy from: " + portUri);

                    System.out.println("Attempting to retrieve all user documents from " + serverPort + " via " + portUri);
                    responseEntity = restTemplate.exchange(portUri, HttpMethod.GET, httpEntity, String.class);
                    System.out.println("Copy of database: " + responseEntity.getBody());
                    System.out.println("Successfully retrieved all user documents from " + serverPort);

                    // Send responseEntity (DB Copy) to portId trying to register
                    URI uriToSaveUserInfo = URI.create("http://localhost/users/saveAll");
                    portUri = UriComponentsBuilder.fromUri(uriToSaveUserInfo).port(portId).build().toUri();
                    System.out.println("URI to save user data into rebooted server: " + portUri);

                    // TODO: is HTTPEntity good?
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    httpEntity = new HttpEntity<>(responseEntity.getBody(), headers);
                    System.out.println("HTTP Entity: " + httpEntity);
                    System.out.println("Attempting to save user documents to " + portId);
                    responseEntity = restTemplate.exchange(portUri, HttpMethod.POST, httpEntity, String.class);
                    System.out.println(responseEntity);
                    System.out.println("Successfully saved all user documents into " + portId);

                    // Error I/O error on POST request for "http://localhost:8082/users/saveAll": Connect to
                    // http://localhost:8082 [localhost/127.0.0.1, localhost/0:0:0:0:0:0:0:1] failed: Connection refused

                    // MethodArgumentTypeMismatchException: Failed to convert value of type 'java.lang.String' to
                    // required type 'org.bson.types.ObjectId'; Failed to convert from type [java.lang.String] to
                    // type [@org.springframework.web.bind.annotation.PathVariable org.bson.types.ObjectId] for value 'saveAll']

                    // Only add new server if DB copy request is successful
                    proxyService.setServers(portId);

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else {
                // Only add new server if DB copy request is successful
                proxyService.setServers(portId);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
