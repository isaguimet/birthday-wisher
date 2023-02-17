package com.birthdaywisher.server.controller;

import com.birthdaywisher.server.model.Message;
import com.birthdaywisher.server.service.MessageService;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/messages")
public class MessageController {
    // TODO: delete this class
    private MessageService msgService;

    public MessageController(MessageService msgService) {
        this.msgService = msgService;
    }

    @PostMapping
    public ResponseEntity<?> createMessage(@RequestBody Message msg) {
        try {
            return new ResponseEntity<>(msgService.createMessage(msg), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> readMessage(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(msgService.getMsgById(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateMessage(@PathVariable ObjectId id, @RequestBody Map<String, String> payload) {
        try {
            return new ResponseEntity<>(msgService.updateMessage(id, payload), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMessage(@PathVariable ObjectId id) {
        try {
            msgService.deleteMessage(id);
            return new ResponseEntity<>(
                    "Message with id " + id + " has been successfully deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
