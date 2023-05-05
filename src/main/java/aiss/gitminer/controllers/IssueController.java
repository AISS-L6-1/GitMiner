package aiss.gitminer.controllers;

import aiss.gitminer.model.Comment;
import aiss.gitminer.model.Commit;
import aiss.gitminer.model.Issue;
import aiss.gitminer.repositories.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gitminer/issues")
public class IssueController {

    @Autowired
    IssueRepository issueRepository;

    // GET http://localhost:8080/gitminer/issues
    @GetMapping
    public List<Issue> findAll(@RequestParam("state") Optional<String> state, @RequestParam("authorId") Optional<String> authorId) {
        List<Issue> issueList = issueRepository.findAll();
        if (state.isPresent()) {
            issueList =  issueList.stream().filter(issue -> issue.getState().equals(state.get())).toList();
        } else if (authorId.isPresent()) {
            issueList =  issueList.stream().filter(issue -> issue.getAuthor().getId().equals(authorId.get())).toList();
        }
        return issueList;
    }

    // GET http://localhost:8080/gitminer/issues/{id}
    @GetMapping("/{id}")
    public Issue findOne(@PathVariable String id) {
        Optional<Issue> issue =  issueRepository.findById(id);
        return issue.get();
    }

    // GET http://localhost:8080/gitminer/issues/{id}/comments
    @GetMapping("/{id}/comments")
    public List<Comment> findCommentsFromIssue(@PathVariable String id) {
        Optional<Issue> issue =  issueRepository.findById(id);
        return issue.get().getComments();
    }

}
