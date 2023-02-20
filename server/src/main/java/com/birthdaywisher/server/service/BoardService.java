package com.birthdaywisher.server.service;

import com.birthdaywisher.server.model.Board;
import com.birthdaywisher.server.model.Message;
import com.birthdaywisher.server.repository.BoardRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BoardService {
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Board createBoard(Map<String, String> payload) {
        Board board = new Board();
        board.setUserId(new ObjectId(payload.get("user")));
        return boardRepository.save(board);
    }

    public Optional<Board> getBoardById(ObjectId id) {
        return boardRepository.findById(id);
    }

    public Board setBoardPublic(ObjectId id) {
        Board board = boardRepository.findById(id).get();
        board.setPublic(true);
        return boardRepository.save(board);
    }

    public Board setBoardPrivate(ObjectId id) {
        Board board = boardRepository.findById(id).get();
        board.setPublic(false);
        return boardRepository.save(board);
    }

    public Board setBoardOpen(ObjectId id) {
        Board board = boardRepository.findById(id).get();
        board.setOpen(true);
        return boardRepository.save(board);
    }

    public Board setBoardClosed(ObjectId id) {
        Board board = boardRepository.findById(id).get();
        board.setOpen(false);
        return boardRepository.save(board);
    }

    public void deleteBoard(ObjectId id) {
        boardRepository.deleteById(id);
    }

    public Board createMessage(ObjectId boardId, Message msg) {
        Board board = boardRepository.findById(boardId).get();
        Map<ObjectId, Message> messages = board.getMessages();
        messages.put(msg.getId(), msg);
        board.setMessages(messages);
        return boardRepository.save(board);
    }

    public Message getMsgById(ObjectId boardId, ObjectId msgId) {
        Board board = boardRepository.findById(boardId).get();
        return board.getMessages().get(msgId);
    }

    public Board updateMessage(ObjectId boardId, ObjectId msgId, Map<String, String> payload) {
        Board board = boardRepository.findById(boardId).get();

        Map<ObjectId, Message> messages = board.getMessages();
        Message msg = messages.get(msgId);
        msg.setMsgText(payload.get("msgText"));
        // TODO: should this date be server-generated or provided by client?
        msg.setLastUpdatedDate(LocalDate.now());
        messages.replace(msgId, msg);

        board.setMessages(messages);
        return boardRepository.save(board);
    }

    public Board deleteMessage(ObjectId boardId, ObjectId msgId) {
        Board board = boardRepository.findById(boardId).get();

        Map<ObjectId, Message> messages = board.getMessages();
        messages.remove(msgId);

        board.setMessages(messages);
        return boardRepository.save(board);
    }

    public List<Board> getBoardsByUserId(ObjectId userId) {
        return boardRepository.findByUserId(userId);
    }
}
