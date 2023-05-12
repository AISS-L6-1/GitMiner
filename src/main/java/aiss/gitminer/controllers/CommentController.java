package aiss.gitminer.controllers;

import aiss.gitminer.exceptions.ResourceNotFoundException;
import aiss.gitminer.model.Comment;
import aiss.gitminer.model.Project;
import aiss.gitminer.repositories.CommentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Comment", description = "Git comments mining API")
@RestController
@RequestMapping("/gitminer/comments")
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    // GET http://localhost:8080/gitminer/comments
    @Operation(
            summary = "Find all comments",
            description = "Retrieve every comment found in database, with optional pagination and sorting",
            tags = {"comment", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comments found succesfully", content = {@Content(schema = @Schema(implementation = Project.class), mediaType = "applications/json")})
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Comment> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "order", defaultValue = "+id") String order
    ) {
        Pageable paging;
        if (order.startsWith("-")) {
            paging = PageRequest.of(page, size, Sort.by(order.substring(1)).descending());
        } else {
            paging = PageRequest.of(page, size, Sort.by(order.substring(1)).ascending());
        }
        return commentRepository.findAll(paging).getContent();
    }

    // GET http://localhost:8080/gitminer/comments/{id}
    @Operation(
            summary = "Find a certain comment",
            description = "Retrieve a certain comment found in database",
            tags = {"comment", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment found succesfully", content = {@Content(schema = @Schema(implementation = Project.class), mediaType = "applications/json")}),
            @ApiResponse(responseCode = "404", description = "Comment not found", content = {@Content(schema = @Schema())})
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Comment findOne(@Parameter(description = "Id of the comment to be found") @PathVariable String id)
            throws ResourceNotFoundException {
        Optional<Comment> comment = commentRepository.findById(id);
        if (!comment.isPresent()) {
            throw new ResourceNotFoundException("Comment not found");
        }
        return comment.get();
    }
}
