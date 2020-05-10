package ru.managementtool.ppmtool.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.managementtool.ppmtool.domain.ProjectTask;
import ru.managementtool.ppmtool.service.ProjectTaskService;
import ru.managementtool.ppmtool.util.BindingResultValidator;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/backlog")
@CrossOrigin
public class BacklogController {
    @Autowired
    private ProjectTaskService projectTaskService;

    @PostMapping("/{backlog_id}")
    public ResponseEntity<?> addPTtoBacklog(@Valid @RequestBody ProjectTask projectTask,
                                            BindingResult result, @PathVariable String backlog_id, Principal principal) {
        ResponseEntity<?> errorMap = BindingResultValidator.validateBindingResult(result);

        if (errorMap != null)
            return  errorMap;

        ProjectTask createdProjectTask = projectTaskService.addProjectTask(backlog_id, projectTask, principal.getName());

        return new ResponseEntity<>(createdProjectTask, HttpStatus.CREATED);
    }

    @GetMapping("/{backlog_id}")
    public ResponseEntity<Iterable<ProjectTask>> getProjectBacklog(@PathVariable String backlog_id, Principal principal)
    {
        return new ResponseEntity<>(projectTaskService.findBacklogById(backlog_id, principal.getName()), HttpStatus.OK);
    }

    @GetMapping("/{backlog_id}/{pt_id}")
    public ResponseEntity<?> getProjectTask(@PathVariable String backlog_id, @PathVariable String pt_id, Principal principal){
        ProjectTask projectTask = projectTaskService.findPTbyProjectSequence(backlog_id, pt_id, principal.getName());
        return new ResponseEntity<>(projectTask, HttpStatus.OK);
    }

    @PatchMapping("/{backlog_id}/{pt_id}")
    public ResponseEntity<?> updateProjectTask(@Valid @RequestBody ProjectTask projectTask,
                                                BindingResult result,
                                                @PathVariable String backlog_id,
                                                @PathVariable String pt_id){
        ResponseEntity<?> errorMap = BindingResultValidator.validateBindingResult(result);

        if (errorMap != null)
            return  errorMap;

        ProjectTask updatedTask = projectTaskService.updateByProjectSequence(projectTask);

        return new ResponseEntity<>(updatedTask, HttpStatus.OK);
    }

    @DeleteMapping("/{backlog_id}/{pt_id}")
    public ResponseEntity<?> deleteProjectTask(@PathVariable String backlog_id,
                                               @PathVariable String pt_id, Principal principal) {
        projectTaskService.deletePTbyProjectSequence(backlog_id, pt_id, principal.getName());
        return new ResponseEntity<>("Project task with id " + pt_id + " was deleted successfully", HttpStatus.OK);
    }
}
