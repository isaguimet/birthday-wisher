package com.birthdaywisher.server.controller;

import com.birthdaywisher.server.service.CommService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comm")
public class CommController {
    private CommService commService;

    public CommController(CommService commService) {
        this.commService = commService;
    }

    @PatchMapping("/addServerToGroup/{serverId}")
    public ResponseEntity<?> addServerToGroup(@PathVariable String serverId) {
        try {
            commService.addServerToGroup(serverId);
            return new ResponseEntity<>(commService.getServerGroup(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/removeServerFromGroup/{serverId}")
    public ResponseEntity<?> removeServerFromGroup(@PathVariable String serverId) {
        try {
            commService.removeServerFromGroup(serverId);
            return new ResponseEntity<>(commService.getServerGroup(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/dataDump")
    public ResponseEntity<?> dataDump() {
        try {
            return new ResponseEntity<>(commService.dataDump(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/dataReset")
    public ResponseEntity<?> dataRestore(@RequestBody List<Iterable<?>> data) {
        try {
            commService.dataReset(data);
            System.out.println("Successfully synced my db with primary & registered as backup server");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
