package com.birthdaywisher.server.controller;

import com.birthdaywisher.server.leader.LeaderService;
import com.birthdaywisher.server.model.Board;
import com.birthdaywisher.server.model.Message;
import com.birthdaywisher.server.service.BoardService;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/boards")
public class BoardController {
    private BoardService boardService;
    private LeaderService leaderService;

    public BoardController(BoardService boardService, LeaderService leaderService) {
        this.boardService = boardService;
        this.leaderService = leaderService;
    }

    @PostMapping
    public ResponseEntity<?> createBoard(@RequestBody Board board) {
        try {
            ObjectId id = board.getUserId();
            if (boardService.shouldCreateNewBoard(board)) {
                Board newBoard = boardService.createBoard(board);
                leaderService.forwardCreateBoard(newBoard);
                return new ResponseEntity<>(boardService.getBoardsByUserId(id), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(
                        "User " + id + " already has a board for this year.", HttpStatus.BAD_REQUEST);
            }
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
            leaderService.forwardSetBoardPublic(id);
            return new ResponseEntity<>(boardService.setBoardPublic(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/setPrivate/{id}")
    public ResponseEntity<?> setBoardPrivate(@PathVariable ObjectId id) {
        try {
            leaderService.forwardSetBoardPrivate(id);
            return new ResponseEntity<>(boardService.setBoardPrivate(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/setOpen/{id}")
    public ResponseEntity<?> setBoardOpen(@PathVariable ObjectId id) {
        try {
            leaderService.forwardSetBoardOpen(id);
            return new ResponseEntity<>(boardService.setBoardOpen(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/setClosed/{id}")
    public ResponseEntity<?> setBoardClosed(@PathVariable ObjectId id) {
        try {
            leaderService.forwardSetBoardClosed(id);
            return new ResponseEntity<>(boardService.setBoardClosed(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(boardService.deleteBoard(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{boardId}/messages")
    public ResponseEntity<?> createMessage(@PathVariable ObjectId boardId, @RequestBody Message msg) {
        try {
            if (boardService.alreadySentMessage(boardId, msg)) {
                return new ResponseEntity<>(
                        "User " + msg.getToUserId() + " has already submitted a message to board " + boardId,
                        HttpStatus.BAD_REQUEST);
            } else {
                Board updatedBoard = boardService.createMessage(boardId, msg);
                for (Map.Entry<ObjectId, Message> msgEntry : updatedBoard.getMessages().entrySet()) {
                    // a board contains at most one msg with a given fromUserId
                    if (msgEntry.getValue().getFromUserId().equals(msg.getFromUserId())) {
                        leaderService.forwardCreateMessage(boardId, msgEntry.getValue());
                        break;
                    }
                }
                return new ResponseEntity<>(boardService.getBoardsByUserId(updatedBoard.getUserId()), HttpStatus.CREATED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{boardId}/messages/{msgId}")
    public ResponseEntity<?> readMessage(@PathVariable ObjectId boardId, @PathVariable ObjectId msgId) {
        try {
            return new ResponseEntity<>(boardService.getMsgById(boardId, msgId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{boardId}/messages/{msgId}")
    public ResponseEntity<?> updateMessage(
            @PathVariable ObjectId boardId, @PathVariable ObjectId msgId, @RequestBody Map<String, String> payload) {
        try {
            return new ResponseEntity<>(boardService.updateMessage(boardId, msgId, payload), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{boardId}/messages/{msgId}")
    public ResponseEntity<?> deleteMessage(@PathVariable ObjectId boardId, @PathVariable ObjectId msgId) {
        try {
            return new ResponseEntity<>(boardService.deleteMessage(boardId, msgId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/byUserId/{userId}")
    public ResponseEntity<?> getBoardsByUserId(@PathVariable ObjectId userId) {
        try {
            return new ResponseEntity<>(boardService.getBoardsByUserId(userId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
