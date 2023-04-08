package com.birthdaywisher.server.controller;

import com.birthdaywisher.server.service.CommService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comm")
public class CommController {
    private CommService commService;

    public CommController(CommService commService) {
        this.commService = commService;
    }

    @PatchMapping("/addServerToGroup/{portNum}")
    public ResponseEntity<?> addServerToGroup(@PathVariable Integer portNum) {
        try {
            commService.addServerToGroup(portNum);
            return new ResponseEntity<>(commService.getServerGroup(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/removeServerFromGroup/{portNum}")
    public ResponseEntity<?> removeServerFromGroup(@PathVariable Integer portNum) {
        try {
            commService.removeServerFromGroup(portNum);
            return new ResponseEntity<>(commService.getServerGroup(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
