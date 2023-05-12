package aiss.gitminer.controllers;

import aiss.gitminer.exceptions.ResourceNotFoundException;
import aiss.gitminer.model.Commit;
import aiss.gitminer.repositories.CommitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gitminer/commits")
public class CommitController {

    @Autowired
    CommitRepository commitRepository;

    // GET http://localhost:8080/gitminer/commits
    @GetMapping
    public List<Commit> findAll(@RequestParam("email") Optional<String> email) {
        List<Commit> commitList = commitRepository.findAll();
        if (email.isPresent()) {
            commitList = commitList.stream().filter(commit -> commit.getCommitterEmail().equals(email.get())).toList();
        }
        return commitList;

    }

    // GET http://localhost:8080/gitminer/commits/{id}
    @GetMapping("/{id}")
    public Commit findOne(@PathVariable String id)
            throws ResourceNotFoundException {
        Optional<Commit> commit = commitRepository.findById(id);
        if (!commit.isPresent()) {
            throw new ResourceNotFoundException("Commit not found");
        }
        return commit.get();
    }


}
