package com.birthdaywisher.server.service;

import com.birthdaywisher.server.model.Board;
import com.birthdaywisher.server.model.Message;
import com.birthdaywisher.server.repository.BoardRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BoardService {
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public boolean shouldCreateNewBoard(Board board) {
        Optional<Board> userBoardForThisYear = boardRepository.findBoardByUserIdAndYear(
                board.getUserId(), String.valueOf(LocalDate.now().getYear()));
        return userBoardForThisYear.isEmpty();
    }

    public Board createBoard(Board board) {
        return boardRepository.save(board);
    }

    public Optional<Board> getBoardById(ObjectId id) {
        return boardRepository.findById(id);
    }

    public List<Board> setBoardPublic(ObjectId id) {
        Board board = boardRepository.findById(id).get();
        board.setPublic(true);
        board = boardRepository.save(board);
        return boardRepository.findBoardsByUserId(board.getUserId());
    }

    public List<Board> setBoardPrivate(ObjectId id) {
        Board board = boardRepository.findById(id).get();
        board.setPublic(false);
        board = boardRepository.save(board);
        return boardRepository.findBoardsByUserId(board.getUserId());
    }

    public List<Board> setBoardOpen(ObjectId id) {
        Board board = boardRepository.findById(id).get();
        board.setOpen(true);
        board = boardRepository.save(board);
        return boardRepository.findBoardsByUserId(board.getUserId());
    }

    public List<Board> setBoardClosed(ObjectId id) {
        Board board = boardRepository.findById(id).get();
        board.setOpen(false);
        board = boardRepository.save(board);
        return boardRepository.findBoardsByUserId(board.getUserId());
    }

    public List<Board> deleteBoard(ObjectId id) {
        Optional<Board> board = boardRepository.findById(id);
        boardRepository.deleteById(id);
        if (board.isPresent()) {
            return boardRepository.findBoardsByUserId(board.get().getUserId());
        }

        return Collections.emptyList();
    }

    public boolean alreadySentMessage(ObjectId boardId, Message msg) {
        ObjectId requesterId = msg.getFromUserId();
        Board board = boardRepository.findById(boardId).get();
        Map<ObjectId, Message> messages = board.getMessages();
        for (Message message : messages.values()) {
            if (message.getFromUserId().equals(requesterId)) {
                return true;
            }
        }
        return false;
    }

    public List<Board> createMessage(ObjectId boardId, Message msg) {
        Board board = boardRepository.findById(boardId).get();
        Map<ObjectId, Message> messages = board.getMessages();
        messages.put(msg.getId(), msg);
        board.setMessages(messages);
        board = boardRepository.save(board);
        return boardRepository.findBoardsByUserId(board.getUserId());
    }

    public Message getMsgById(ObjectId boardId, ObjectId msgId) {
        Board board = boardRepository.findById(boardId).get();
        return board.getMessages().get(msgId);
    }

    public List<Board> updateMessage(ObjectId boardId, ObjectId msgId, Map<String, String> payload) {
        Board board = boardRepository.findById(boardId).get();

        Map<ObjectId, Message> messages = board.getMessages();
        Message msg = messages.get(msgId);
        msg.setMsgText(payload.get("msgText"));
        // TODO: should this date be server-generated or provided by client?
        msg.setLastUpdatedDate(LocalDate.now());
        messages.replace(msgId, msg);

        board.setMessages(messages);
        board = boardRepository.save(board);
        return boardRepository.findBoardsByUserId(board.getUserId());
    }

    public List<Board> deleteMessage(ObjectId boardId, ObjectId msgId) {
        Board board = boardRepository.findById(boardId).get();

        Map<ObjectId, Message> messages = board.getMessages();
        messages.remove(msgId);

        board.setMessages(messages);
        board = boardRepository.save(board);
        return boardRepository.findBoardsByUserId(board.getUserId());
    }

    public List<Board> getBoardsByUserId(ObjectId userId) {
        return boardRepository.findBoardsByUserId(userId);
    }
}
