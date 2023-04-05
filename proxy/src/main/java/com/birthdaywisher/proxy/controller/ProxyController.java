package com.birthdaywisher.proxy.controller;

import com.birthdaywisher.proxy.service.ProxyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProxyController {
    private ProxyService proxyService;

    public ProxyController(ProxyService proxyService) {
        this.proxyService = proxyService;
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
            proxyService.setServers(portId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
