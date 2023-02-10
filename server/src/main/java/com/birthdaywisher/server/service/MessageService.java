package com.birthdaywisher.server.service;

import com.birthdaywisher.server.model.Message;
import com.birthdaywisher.server.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    public List<Message> allMessages() {
        return messageRepository.findAll();
    }
}
