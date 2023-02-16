package com.birthdaywisher.server.service;

import com.birthdaywisher.server.model.Board;
import com.birthdaywisher.server.repository.BoardRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

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
        board.setUser(new ObjectId(payload.get("user")));
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

    public void deleteBoard(ObjectId id) {
        boardRepository.deleteById(id);
    }
}
