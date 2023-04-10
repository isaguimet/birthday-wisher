package com.birthdaywisher.proxy;

import com.birthdaywisher.proxy.service.ProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableAutoConfiguration(exclude = MongoAutoConfiguration.class)
public class ProxyApplication {

	@Autowired
	private ProxyService proxyService;

	@Autowired
	private RestTemplate restTemplate;

	public static void main(String[] args) {
		SpringApplication.run(ProxyApplication.class, args);
	}

	/**
	 * Once the proxy is up, check if the other proxy is alive and request their server group list.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void getServerGroupList() {
		HttpEntity<String> requestEntity = new HttpEntity<>(null, null);
		String serverGroupUrl = "https://%s-ey7sfy2hcq-wl.a.run.app/serverGroup";

		for (String proxyId : proxyService.getProxies()) {
			if (!proxyId.equals(proxyService.getSystemId())) {
				try {
					URI uri = URI.create(String.format(serverGroupUrl, proxyId));
					System.out.println("Attempting to fetch server group from existing proxy instance: " + uri);
					HttpEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);
					proxyService.setServers(convertListStringToListObject(responseEntity.getBody()));

					// This only needs to be done once, so break out of the loop now
					break;
				} catch (HttpStatusCodeException e) {
					System.out.println("Bad response from proxy: " + e.getStatusCode() + "\n" + e.getResponseHeaders()
							+ "\n" + e.getResponseBodyAsString());
				} catch (Exception e) {
					// This is expected if the other proxy is not alive
					System.out.println("Failed to send request to " + proxyId + ": " + e.getMessage());
				}
			}
		}
	}

	public List<String> convertListStringToListObject(String listStr) {
		String replace = listStr.replaceAll("^\\[|]$", "");
		if (replace.isEmpty()) {
			return new ArrayList<>();
		} else {
			return new ArrayList<>(Arrays.asList(replace.split(",")));
		}
	}
}
