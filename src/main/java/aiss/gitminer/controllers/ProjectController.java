package aiss.gitminer.controllers;

import aiss.gitminer.exceptions.ProjectNotFoundException;
import aiss.gitminer.model.Project;
import aiss.gitminer.repositories.ProjectRepository;
import io.swagger.v3.oas.annotations.Operation;
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
            tags = { "projects", "post" })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Project create(@Valid @RequestBody Project project) {
        Project _project = projectRepository.save(project);
        return _project;
    }

    // GET http://localhost:8080/gitminer/projects
    @Operation(
            summary = "Find all projects",
            description = "Retrieve every project found in database, with optinal pagination and sorting",
            tags = { "projects", "get" })
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
            tags = { "projects", "get", "id"})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Project findOne(@PathVariable String id) throws ProjectNotFoundException {
        Optional<Project> project =  projectRepository.findById(id);
        if (!project.isPresent()) {
            throw new ProjectNotFoundException();
        }
        return project.get();
    }

}
