package com.birthdaywisher.server.controller;

import com.birthdaywisher.server.model.Message;
import com.birthdaywisher.server.service.MessageService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @PostMapping
    public ResponseEntity<Message> createMessage(@RequestBody Message msg) {
        try {
            return new ResponseEntity<Message>(messageService.createMessage(msg), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Message>> readMessage(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<Optional<Message>>(messageService.getMsgById(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Message> updateMessage(@PathVariable ObjectId id, @RequestBody Map<String, String> payload) {
        try {
            return new ResponseEntity<Message>(messageService.updateMessage(id, payload), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMessage(@PathVariable ObjectId id) {
        try {
            messageService.deleteMessage(id);
            return new ResponseEntity<String>(
                    "Message with id " + id + " has been successfully deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
