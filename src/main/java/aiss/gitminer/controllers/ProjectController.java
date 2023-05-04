package aiss.gitminer.controllers;

import aiss.gitminer.model.Project;
import aiss.gitminer.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    @Autowired
    ProjectRepository projectRepository;

    // POST http://localhost:8080/api/albums
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Project create(@Valid @RequestBody Project project) {
        Project _project = projectRepository.save(project);
        return _project;
    }
}
