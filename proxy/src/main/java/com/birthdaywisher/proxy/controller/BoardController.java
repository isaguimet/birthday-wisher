package com.birthdaywisher.proxy.controller;

import com.birthdaywisher.proxy.model.Board;
import com.birthdaywisher.proxy.model.Message;
import com.birthdaywisher.proxy.service.ProxyService;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/boards")
public class BoardController {
    private ProxyService proxyService;

    public BoardController(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @PostMapping
    public ResponseEntity<?> createBoard(@RequestBody Board board) {
        try {
            return new ResponseEntity<>(proxyService.forwardCreateBoard(board), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> readBoard(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/setPublic/{id}")
    public ResponseEntity<?> setBoardPublic(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/setPrivate/{id}")
    public ResponseEntity<?> setBoardPrivate(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/setOpen/{id}")
    public ResponseEntity<?> setBoardOpen(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/setClosed/{id}")
    public ResponseEntity<?> setBoardClosed(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{boardId}/messages")
    public ResponseEntity<?> createMessage(@PathVariable ObjectId boardId, @RequestBody Message msg) {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{boardId}/messages/{msgId}")
    public ResponseEntity<?> readMessage(@PathVariable ObjectId boardId, @PathVariable ObjectId msgId) {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{boardId}/messages/{msgId}")
    public ResponseEntity<?> updateMessage(
            @PathVariable ObjectId boardId, @PathVariable ObjectId msgId, @RequestBody Map<String, String> payload) {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{boardId}/messages/{msgId}")
    public ResponseEntity<?> deleteMessage(@PathVariable ObjectId boardId, @PathVariable ObjectId msgId) {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/byUserId/{userId}")
    public ResponseEntity<?> getBoardsByUserId(@PathVariable ObjectId userId) {
        try {
            return new ResponseEntity<>(proxyService.forwardReqToPrimary(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
