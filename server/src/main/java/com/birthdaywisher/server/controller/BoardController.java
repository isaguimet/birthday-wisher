package com.birthdaywisher.server.controller;

import com.birthdaywisher.server.service.BoardService;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/boards")
public class BoardController {
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping
    public ResponseEntity<?> createBoard(@RequestBody Map<String, String> payload) {
        try {
            return new ResponseEntity<>(boardService.createBoard(payload), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> readBoard(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(boardService.getBoardById(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/setPublic/{id}")
    public ResponseEntity<?> setBoardPublic(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(boardService.setBoardPublic(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/setPrivate/{id}")
    public ResponseEntity<?> setBoardPrivate(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(boardService.setBoardPrivate(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBoard(@PathVariable ObjectId id) {
        try {
            boardService.deleteBoard(id);
            return new ResponseEntity<>("Board with id " + id + " has been successfully deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
