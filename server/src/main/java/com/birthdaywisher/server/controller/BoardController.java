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

    @PostMapping("/forwarded")
    public ResponseEntity<?> forwardCreateBoard(@RequestBody Board board) {
        try {
            ObjectId id = board.getUserId();
            if (boardService.shouldCreateNewBoard(board)) {
                Board newBoard = boardService.createBoard(board);
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
            leaderService.forwardBoardPatch("http://localhost/boards/forwarded/setPublic/" + id);
            return new ResponseEntity<>(boardService.setBoardPublic(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/forwarded/setPublic/{id}")
    public ResponseEntity<?> forwardSetBoardPublic(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(boardService.setBoardPublic(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/setPrivate/{id}")
    public ResponseEntity<?> setBoardPrivate(@PathVariable ObjectId id) {
        try {
            leaderService.forwardBoardPatch("http://localhost/boards/forwarded/setPrivate/" + id);
            return new ResponseEntity<>(boardService.setBoardPrivate(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/forwarded/setPrivate/{id}")
    public ResponseEntity<?> forwardSetBoardPrivate(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(boardService.setBoardPrivate(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/setOpen/{id}")
    public ResponseEntity<?> setBoardOpen(@PathVariable ObjectId id) {
        try {
            leaderService.forwardBoardPatch("http://localhost/boards/forwarded/setOpen/" + id);
            return new ResponseEntity<>(boardService.setBoardOpen(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/forwarded/setOpen/{id}")
    public ResponseEntity<?> forwardSetBoardOpen(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(boardService.setBoardOpen(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/setClosed/{id}")
    public ResponseEntity<?> setBoardClosed(@PathVariable ObjectId id) {
        try {
            leaderService.forwardBoardPatch("http://localhost/boards/forwarded/setClosed/" + id);
            return new ResponseEntity<>(boardService.setBoardClosed(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/forwarded/setClosed/{id}")
    public ResponseEntity<?> forwardSetBoardClosed(@PathVariable ObjectId id) {
        try {
            return new ResponseEntity<>(boardService.setBoardClosed(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable ObjectId id) {
        try {
            leaderService.forwardDeleteReq("http://localhost/boards/forwarded/" + id);
            return new ResponseEntity<>(boardService.deleteBoard(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/forwarded/{id}")
    public ResponseEntity<?> forwardDeleteBoard(@PathVariable ObjectId id) {
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

    @PostMapping("/forwarded/{boardId}/messages")
    public ResponseEntity<?> forwardCreateMessage(@PathVariable ObjectId boardId, @RequestBody Message msg) {
        try {
            if (boardService.alreadySentMessage(boardId, msg)) {
                return new ResponseEntity<>(
                        "User " + msg.getToUserId() + " has already submitted a message to board " + boardId,
                        HttpStatus.BAD_REQUEST);
            } else {
                Board updatedBoard = boardService.createMessage(boardId, msg);
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
            leaderService.forwardUpdateMessage(boardId, msgId, payload);
            return new ResponseEntity<>(boardService.updateMessage(boardId, msgId, payload), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/forwarded/{boardId}/messages/{msgId}")
    public ResponseEntity<?> forwardUpdateMessage(
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
            leaderService.forwardDeleteReq("http://localhost/boards/forwarded/" + boardId + "/messages/" + msgId);
            return new ResponseEntity<>(boardService.deleteMessage(boardId, msgId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/forwarded/{boardId}/messages/{msgId}")
    public ResponseEntity<?> forwardDeleteMessage(@PathVariable ObjectId boardId, @PathVariable ObjectId msgId) {
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
