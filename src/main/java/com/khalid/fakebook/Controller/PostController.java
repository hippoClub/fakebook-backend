package com.khalid.fakebook.Controller;

import com.khalid.fakebook.Exception.ResponseException;
import com.khalid.fakebook.Repo.UserRepo;
import com.khalid.fakebook.Service.PostService;
import com.khalid.fakebook.Service.SessionService;
import com.khalid.fakebook.dto.PostReq;
import com.khalid.fakebook.model.Post;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final PostService postService;
    private final SessionService sessionService;
    private final UserRepo userRepo;

    @GetMapping("/allPost")
    public ResponseEntity<?> getAllPosts(@RequestHeader(value = "session") String validateSession) {
        if (sessionService.findBySession(validateSession) == null)
            return new ResponseEntity<>(ResponseException.jsonResponse("error", "Access denied. You need to login first"), HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(postService.findAllPosts(), HttpStatus.OK);
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<?> upload(@RequestBody PostReq req, @RequestHeader(value = "session") String validateSession, @PathVariable(value = "id") Long userId) {

        if (sessionService.findBySession(validateSession) == null)
            return new ResponseEntity<>(ResponseException.jsonResponse("error", "Access denied. You need to login first"), HttpStatus.FORBIDDEN);

        Post newPost = postService.addPost(req, userId);
        return new ResponseEntity<>(ResponseException.jsonResponse("success", "Image added successfully"), HttpStatus.CREATED);
    }


    @PutMapping("/{userId}/editPost/{id}")
    public ResponseEntity<?> editPost(@RequestBody PostReq req, @RequestHeader(value = "session") String validateSession, @PathVariable("id") Long postId, @PathVariable(value = "userId") Long userId) {
        if (sessionService.findBySession(validateSession) == null)
            return new ResponseEntity<>(ResponseException.jsonResponse("error", "Access denied. You need to login first"), HttpStatus.FORBIDDEN);

        if (postId == null)
            return new ResponseEntity<>(ResponseException.jsonResponse("error", "Post id " + postId + " not found"), HttpStatus.BAD_REQUEST);

        if (!userRepo.existsById(userId))
            return new ResponseEntity<>(ResponseException.jsonResponse("error", "userId " + userId + " not found"), HttpStatus.BAD_REQUEST);

        postService.updatePost(req, postId);
        return new ResponseEntity<>(ResponseException.jsonResponse("success", "Post updated successfully"), HttpStatus.CREATED);
    }
}
