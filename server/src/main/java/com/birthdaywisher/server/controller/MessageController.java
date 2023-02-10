package com.birthdaywisher.server.controller;

import com.birthdaywisher.server.model.Message;
import com.birthdaywisher.server.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;
    @GetMapping
    public ResponseEntity<List<Message>> getMessages() {
        return new ResponseEntity<List<Message>>(messageService.allMessages(), HttpStatus.OK);
    }
}
