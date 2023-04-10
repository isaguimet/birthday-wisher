package com.birthdaywisher.server;

import com.birthdaywisher.server.service.CommService;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Application {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CommService commService;

    private List<String> proxyGroup = Arrays.asList("proxy1", "proxy2");

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder.serializerByType(ObjectId.class, new ToStringSerializer());
    }

    /**
     * Once the application is ready, sync its database with the primary server, then register itself with the proxies.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void registerServer() {
        HttpEntity<String> requestEntity = new HttpEntity<>(null, null);
        String registrationUrl = "https://%s-ey7sfy2hcq-wl.a.run.app/serverRegistration/" + commService.getSystemId();

        for (String proxyId : proxyGroup) {
            try {
                URI uri = URI.create(String.format(registrationUrl, proxyId));
                System.out.println("Attempting to register with proxy: " + uri);
                restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);
                System.out.println("Successfully registered with proxies");

                // This only needs to be done once, so break out of the loop now
                break;
            } catch (HttpStatusCodeException e) {
                System.out.println("Bad response from proxy: " + e.getStatusCode() + "\n" + e.getResponseHeaders()
                        + "\n" + e.getResponseBodyAsString());
            } catch (Exception e) {
                System.out.println("Failed to send register request to " + proxyId + ": " + e.getMessage());
            }
        }
    }
}
