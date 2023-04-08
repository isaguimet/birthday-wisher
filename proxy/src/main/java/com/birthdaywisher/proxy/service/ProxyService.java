package com.birthdaywisher.proxy.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;

@Service
public class ProxyService {

    private final RestTemplate restTemplate;
    private List<Integer> servers = new ArrayList<>();

    public ProxyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void addServerToGroup(Integer portNum) {
        servers.add(portNum);
        System.out.println("Server Group: " + servers);
    }

    public void removeServerFromGroup(Integer portNum) {
        servers.remove(portNum);
        System.out.println("Server Group: " + servers);
    }

    public List<Integer> getServers() {
        return this.servers;
    }

    public ResponseEntity<?> forwardReqToPrimary(String body, HttpMethod method, HttpServletRequest request) throws UnsupportedEncodingException {
        ResponseEntity<?> responseEntity = null;

        // copy request headers
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headersNames = request.getHeaderNames();
        while (headersNames.hasMoreElements()) {
            String headerName = headersNames.nextElement();
            headers.set(headerName, request.getHeader(headerName));
        }

        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

        URI uri = URI.create(String.valueOf(request.getRequestURL()));
        String queryString = request.getQueryString() == null
                ? request.getQueryString()
                : URLDecoder.decode(request.getQueryString(), "UTF-8");

        // try forwarding the request to one of the server ports until one succeeds
        for (Integer serverPort : servers) {
            try {
                URI portUri = UriComponentsBuilder.fromUri(uri).port(serverPort.intValue()).query(queryString).build().toUri();
                System.out.println("Attempting to forward request to " + portUri);
                responseEntity = restTemplate.exchange(portUri, method, httpEntity, String.class);
                System.out.println("Successfully forwarded to " + serverPort);

                return ResponseEntity.status(responseEntity.getStatusCode())
                        .headers(responseEntity.getHeaders())
                        .body(responseEntity.getBody());
            } catch (HttpStatusCodeException e) {
                // connection refused seems to go to the general Exception catch block anyway but doesn't hurt to check
                if (!e.getMessage().contains("Connection refused")) {
                    System.out.println(e.getStatusCode() + "\n" + e.getResponseHeaders() + "\n" + e.getResponseBodyAsString());
                    return ResponseEntity.status(e.getStatusCode())
                            .headers(e.getResponseHeaders())
                            .body(e.getResponseBodyAsString());
                }
            } catch (Exception e) {
                System.out.println("Failed to forward request to " + serverPort + ": " + e.getMessage());
            }
        }

        if (responseEntity == null) {
            // can probably change error handling so that general Exception case does what's in HttpStatusCodeException
            // case and then can remove this if case
            return new ResponseEntity<>("Was not able to connect to any servers", HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return responseEntity;
        }
    }
}
