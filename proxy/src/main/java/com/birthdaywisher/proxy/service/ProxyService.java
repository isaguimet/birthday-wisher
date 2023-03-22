package com.birthdaywisher.proxy.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

@Service
public class ProxyService {

    private final RestTemplate restTemplate;
    private List<Integer> servers = Arrays.asList(8081, 8082, 8083);

    public ProxyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<?> forwardReqToPrimary(String body, HttpMethod method, HttpServletRequest request) {
        ResponseEntity<?> responseEntity = null;

        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headersNames = request.getHeaderNames();
        while (headersNames.hasMoreElements()) {
            String headerName = headersNames.nextElement();
            headers.set(headerName, request.getHeader(headerName));
        }

        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

        URI uri = URI.create(String.valueOf(request.getRequestURL()));
        for (Integer serverPort : servers) {
            uri = UriComponentsBuilder.fromUri(uri).port(serverPort.intValue()).build().toUri();

            try {
                System.out.println("Attempting to forward request to " + uri);
                return restTemplate.exchange(uri, method, httpEntity, String.class);
            } catch (HttpStatusCodeException e) {
                System.out.println("Failed to forward request to " + serverPort + "(HttpStatusCodeException): " + e.getMessage());
                responseEntity = ResponseEntity.status(e.getStatusCode())
                        .headers(e.getResponseHeaders())
                        .body(e.getResponseBodyAsString());
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
