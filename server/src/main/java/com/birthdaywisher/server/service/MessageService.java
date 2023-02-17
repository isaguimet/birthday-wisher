package com.birthdaywisher.server.service;

import com.birthdaywisher.server.model.Message;
import com.birthdaywisher.server.repository.MessageRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
public class MessageService {
    // TODO: delete this class
    private MessageRepository msgRepository;

    public MessageService(MessageRepository msgRepository) { this.msgRepository = msgRepository; }

    public Message createMessage(Message msg) {
        return msgRepository.save(msg);
    }

    public Optional<Message> getMsgById(ObjectId id) {
        return msgRepository.findById(id);
    }

    public Message updateMessage(ObjectId id, Map<String, String> payload) {
        Message msg = msgRepository.findById(id).get();
        msg.setMsgText(payload.get("msgText"));
        // TODO: should this date be from server or client?
        msg.setLastUpdatedDate(LocalDate.now());
        return msgRepository.save(msg);
    }

    public void deleteMessage(ObjectId id) {
        msgRepository.deleteById(id);
    }
}
