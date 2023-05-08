package aiss.gitminer.controllers;

import aiss.gitminer.model.Comment;
import aiss.gitminer.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gitminer/comments")
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    // GET http://localhost:8080/gitminer/comments
    @GetMapping
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    // GET http://localhost:8080/gitminer/comments/{id}
    @GetMapping("/{id}")
    public Comment findOne(@PathVariable String id) {
        Optional<Comment> comment =  commentRepository.findById(id);
        return comment.get();
    }
}
