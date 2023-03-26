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
            uri = UriComponentsBuilder.fromUri(uri).port(serverPort.intValue()).build().toUri();

            try {
                System.out.println("Attempting to forward request to " + uri);
                responseEntity = restTemplate.exchange(uri, method, httpEntity, String.class);

                // if response from server does not have Access-Control-Allow-Origin response header, then keep the
                // headers from the server's response. otherwise, ignore headers from server in order to avoid cors error
                HttpHeaders responseHeaders = (responseEntity.getHeaders().getAccessControlAllowOrigin() == null)
                        ? responseEntity.getHeaders() : new HttpHeaders();

                return ResponseEntity.status(responseEntity.getStatusCode())
                        .headers(responseHeaders)
                        .body(responseEntity.getBody());
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
