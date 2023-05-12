package aiss.gitminer.controllers;

import aiss.gitminer.exceptions.ResourceNotFoundException;
import aiss.gitminer.model.Project;
import aiss.gitminer.repositories.ProjectRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Tag(name = "Project", description = "Git projects mining API")
@RestController
@RequestMapping("/gitminer/projects")
public class ProjectController {

    @Autowired
    ProjectRepository projectRepository;

    // POST http://localhost:8080/gitminer/projects

    @Operation(
            summary = "Post a project",
            description = "Create a specific projects and post it into the database",
            tags = {"projects", "post"})
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Project posted succesfully", content = {@Content(schema = @Schema(implementation = Project.class), mediaType = "applications/json")}),
            @ApiResponse(responseCode = "400", description = "Validation error", content = {@Content(schema = @Schema())})
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Project create(@Valid @RequestBody Project project) {
        Project _project = projectRepository.save(project);
        return _project;
    }

    // GET http://localhost:8080/gitminer/projects
    @Operation(
            summary = "Find all projects",
            description = "Retrieve every project found in database, with optional pagination and sorting",
            tags = {"projects", "get"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projects found succesfully", content = {@Content(schema = @Schema(implementation = Project.class), mediaType = "applications/json")})
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping

    public List<Project> findAll(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size,
                                 @RequestParam(value = "order", defaultValue = "+id") String order) {
        Pageable paging;
        // en nuestra API, un + o un - sirve para indicar el orden (se ordena por una propiedad concreta, por defecto por id)
        if (order.startsWith("-")) {
            paging = PageRequest.of(page, size, Sort.by(order.substring(1)).descending());
        } else {
            paging = PageRequest.of(page, size, Sort.by(order.substring(1)).ascending());
        }
        Page<Project> projectPage = projectRepository.findAll(paging);
        return projectPage.getContent();
    }

    // GET http://localhost:8080/gitminer/projects/{id}
    @Operation(
            summary = "Find a project",
            description = "Find a single project from the database, specifically one whose id coincides with the one provided",
            tags = {"projects", "get", "id"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Project found succesfully", content = {@Content(schema = @Schema(implementation = Project.class), mediaType = "applications/json")}),
            @ApiResponse(responseCode = "404", description = "Project could not be found", content = {@Content(schema = @Schema())})
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Project findOne(@Parameter(description = "Id of the project to be found") @PathVariable String id)
            throws ResourceNotFoundException {
        Optional<Project> project = projectRepository.findById(id);
        if (!project.isPresent()) {
            throw new ResourceNotFoundException("Project not found");
        }
        return project.get();
    }

}
