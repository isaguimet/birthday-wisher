package com.birthdaywisher.proxy.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

@Service
public class ProxyService {

    private final RestTemplate restTemplate;
    private List<Integer> servers = Arrays.asList(8082, 8083, 8084, 8085, 8086, 8087);

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
            try {
                String queryString = request.getQueryString() == null
                        ? request.getQueryString()
                        : URLDecoder.decode(request.getQueryString(), "UTF-8");

                uri = UriComponentsBuilder.fromUri(uri).port(serverPort.intValue()).query(queryString).build().toUri();

                System.out.println("Attempting to forward request to " + uri);
                responseEntity = restTemplate.exchange(uri, method, httpEntity, String.class);

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
