package com.birthdaywisher.server;

import com.birthdaywisher.server.service.UserService;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Application {

    @Autowired
    private ServerProperties serverProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserService userService;

    private List<Integer> proxyGroup = Arrays.asList(8080, 8081);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder.serializerByType(ObjectId.class, new ToStringSerializer());
    }

    @Bean
    InitializingBean registerServer() {
        return () -> {
            HttpEntity<String> httpEntity = new HttpEntity<>(null, null);

            URI uri = URI.create("http://localhost/serverRegistration/" + serverProperties.getPort());

            HttpEntity responseEntity = null;
            // try forwarding the request to one of the proxy ports until one succeeds
            for (Integer proxyPort : proxyGroup) {
                try {
                    URI portUri = UriComponentsBuilder.fromUri(uri).port(proxyPort).build().toUri();
                    System.out.println("Attempting to forward register request to " + portUri);
                    responseEntity = restTemplate.exchange(portUri, HttpMethod.GET, httpEntity, String.class);
                    System.out.println("Successfully forwarded to " + proxyPort);

                    // Case where server list is not empty. Response returned contains DB copy to save
                    if (responseEntity.getBody() != null) {
                        JSONParser parser = new JSONParser();
                        JSONArray object = (JSONArray) parser.parse((String) responseEntity.getBody());

                        System.out.println(object);
                        userService.saveAllUsers(object);
                        // TODO: add this rebooted server to server list
                    }
                } catch (HttpStatusCodeException e) {
                    // connection refused seems to go to the general Exception catch block anyway but doesn't hurt to check
                    if (!e.getMessage().contains("Connection refused")) {
                        System.out.println(e.getStatusCode() + "\n" + e.getResponseHeaders() + "\n" + e.getResponseBodyAsString());
                    }
                } catch (Exception e) {
                    System.out.println("Failed to forward register request to " + proxyPort + ": " + e.getMessage());
                }

                if (responseEntity == null) {
                    // can probably change error handling so that general Exception case does what's in HttpStatusCodeException
                    // case and then can remove this if case
                    System.out.println("Was not able to register the server " + serverProperties.getPort());
                }
            }
        };
    }

}
