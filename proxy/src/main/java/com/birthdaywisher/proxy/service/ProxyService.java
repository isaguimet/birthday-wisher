package com.birthdaywisher.proxy.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class ProxyService {

    private final RestTemplate restTemplate;
    private Set<String> servers = new LinkedHashSet<>();
    private Set<String> proxies = new LinkedHashSet<>(Arrays.asList("proxy1", "proxy2"));

    @Value("${systemId}")
    private String systemId;

    public ProxyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Set<String> getProxies() {
        return this.proxies;
    }

    public synchronized void addServerToGroup(String id) {
        servers.add(id);
        System.out.println("Server Group: " + servers);
    }

    public synchronized void removeServerFromGroup(String id) {
        servers.remove(id);
        System.out.println("Server Group: " + servers);
    }

    public synchronized Set<String> getServers() {
        return this.servers;
    }

    public synchronized void setServers(Set<String> serverGroup) {
        this.servers = serverGroup;
        System.out.println("Server Group: " + servers);
    }

    public String getSystemId() {
        return this.systemId;
    }

    // TODO: this reads "servers"...should it also be synchronized?
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

        String urlTemplate = String.valueOf(request.getRequestURL()).replaceFirst("proxy\\d", "%s");
        String queryString = request.getQueryString() == null
                ? request.getQueryString()
                : URLDecoder.decode(request.getQueryString(), "UTF-8");

        // try forwarding the request to one of the server ports until one succeeds
        for (String serverId : servers) {
            try {
                URI uri = URI.create(String.format(urlTemplate, serverId));
                uri = UriComponentsBuilder.fromUri(uri).query(queryString).build().toUri();
                System.out.println("Attempting to forward request to " + uri);
                responseEntity = restTemplate.exchange(uri, method, httpEntity, String.class);
                System.out.println("Successfully forwarded to " + serverId);

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
                System.out.println("Failed to forward request to " + serverId + ": " + e.getMessage());
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
