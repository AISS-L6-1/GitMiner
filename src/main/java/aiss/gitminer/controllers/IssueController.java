package aiss.gitminer.controllers;

import aiss.gitminer.exceptions.ResourceNotFoundException;
import aiss.gitminer.model.Comment;
import aiss.gitminer.model.Issue;
import aiss.gitminer.model.Project;
import aiss.gitminer.repositories.IssueRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Issue", description = "Git issues mining API")
@RestController
@RequestMapping("/gitminer/issues")
public class IssueController {

    @Autowired
    IssueRepository issueRepository;

    // GET http://localhost:8080/gitminer/issues
    @Operation(
            summary = "Find all issues",
            description = "Retrieve every issue found in database, with optional pagination and sorting",
            tags = {"issue", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projects found succesfully", content = {@Content(schema = @Schema(implementation = Project.class), mediaType = "applications/json")})
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Issue> findAll(@RequestParam("state") Optional<String> state, @RequestParam("authorId") Optional<String> authorId,
                               @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size,
                               @RequestParam(value = "order", defaultValue = "+id") String order) {
        Pageable paging;
        if (order.startsWith("-")) {
            paging = PageRequest.of(page, size, Sort.by(order.substring(1)).descending());
        } else {
            paging = PageRequest.of(page, size, Sort.by(order.substring(1)).ascending());
        }
        Page<Issue> issuePage = issueRepository.findAll(paging);
        List<Issue> issueList = issuePage.stream().toList();

        if (state.isPresent()) {
            issueList =  issueList.stream().filter(issue -> issue.getState().equals(state.get())).toList();
        } else if (authorId.isPresent()) {
            issueList =  issueList.stream().filter(issue -> issue.getAuthor().getId().equals(authorId.get())).toList();
        }

        return issueList;
    }

    // GET http://localhost:8080/gitminer/issues/{id}
    @Operation(
            summary = "Find a certain issue",
            description = "Retrieve a certain issue found in database",
            tags = {"issue", "get", "id"})
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Issue found succesfully", content = {@Content(schema = @Schema(implementation = Project.class), mediaType = "applications/json")}),
            @ApiResponse(responseCode = "404", description = "Issue not found", content = {@Content(schema = @Schema())})
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Issue findOne(@Parameter(description = "Id of the issue to be found") @PathVariable String id)
    throws ResourceNotFoundException{
        Optional<Issue> issue =  issueRepository.findById(id);
        if (!issue.isPresent()) {
            throw new ResourceNotFoundException("Issue not found");
        }
        return issue.get();
    }

    // GET http://localhost:8080/gitminer/issues/{id}/comments
    @Operation(
            summary = "Find all comments from a certain issue",
            description = "Retrieve all comments from a certain issue found in database, with optional pagination and sorting",
            tags = {"issue", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Issue comments found succesfully", content = {@Content(schema = @Schema(implementation = Project.class), mediaType = "applications/json")}),
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/comments")
    public List<Comment> findCommentsFromIssue(@Parameter(description = "Id of the issue whose comments are to be found") @PathVariable String id,
                                               @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size,
                                               @RequestParam(value = "order", defaultValue = "+id") String order)
    throws ResourceNotFoundException{
        Optional<Issue> issue =  issueRepository.findById(id);
        if (!issue.isPresent()) {
            throw new ResourceNotFoundException("Issue not found");
        }
        List<Comment> commentList = issue.get().getComments();

        Pageable paging;
        if (order.startsWith("-")) {
            paging = PageRequest.of(page, size, Sort.by(order.substring(1)).descending());
        } else {
            paging = PageRequest.of(page, size, Sort.by(order.substring(1)).ascending());
        }
        PageImpl<Comment> commentPage = new PageImpl<>(commentList, paging, commentList.size());
        return commentPage.stream().toList();
    }

}
