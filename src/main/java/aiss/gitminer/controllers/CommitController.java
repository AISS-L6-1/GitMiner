package aiss.gitminer.controllers;

import aiss.gitminer.exceptions.ResourceNotFoundException;
import aiss.gitminer.model.Commit;
import aiss.gitminer.model.Project;
import aiss.gitminer.repositories.CommitRepository;
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

@Tag(name = "Commit", description = "Git commit mining API")
@RestController
@RequestMapping("/gitminer/commits")
public class CommitController {

    @Autowired
    CommitRepository commitRepository;

    // GET http://localhost:8080/gitminer/commits
    @Operation(
            summary = "Find all commits",
            description = "Retrieve all commits found in database, with optional pagination and sorting",
            tags = {"commit", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Commits found succesfully", content = {@Content(schema = @Schema(implementation = Project.class), mediaType = "applications/json")}),
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<Commit> findAll(@RequestParam("email") Optional<String> email,
                                @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size,
                                @RequestParam(value = "order", defaultValue = "+id") String order) {
        Pageable paging;
        if (order.startsWith("-")) {
            paging = PageRequest.of(page, size, Sort.by(order.substring(1)).descending());
        } else {
            paging = PageRequest.of(page, size, Sort.by(order.substring(1)).ascending());
        }
        List<Commit> commitList = commitRepository.findAll(paging).getContent();
        if (email.isPresent()) {
            commitList = commitList.stream().filter(commit -> commit.getCommitterEmail().equals(email.get())).toList();
        }
        return commitList;

    }

    // GET http://localhost:8080/gitminer/commits/{id}
    @GetMapping("/{id}")
    @Operation(
            summary = "Find a certain commit",
            description = "Retrieve a certain commit found in database",
            tags = {"commit", "get", "id"})
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Commit found succesfully", content = {@Content(schema = @Schema(implementation = Project.class), mediaType = "applications/json")}),
            @ApiResponse(responseCode = "404", description = "Commit not found", content = {@Content(schema = @Schema())})
    })
    @ResponseStatus(HttpStatus.OK)
    public Commit findOne(@Parameter(description = "Id of the commit to be found") @PathVariable String id)
            throws ResourceNotFoundException {
        Optional<Commit> commit = commitRepository.findById(id);
        if (!commit.isPresent()) {
            throw new ResourceNotFoundException("Commit not found");
        }
        return commit.get();
    }


}
