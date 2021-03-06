package ru.managementtool.ppmtool.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.managementtool.ppmtool.domain.Project;
import ru.managementtool.ppmtool.service.ProjectService;
import ru.managementtool.ppmtool.util.BindingResultValidator;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/project")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @PostMapping("")
    public ResponseEntity<?> createNewProject(@Valid @RequestBody Project project, BindingResult result, Principal principal)
    {
        ResponseEntity<?> errorMap = BindingResultValidator.validateBindingResult(result);

        if (errorMap != null)
            return errorMap;

        project = projectService.saveOrUpdate(project, principal.getName());
        return new ResponseEntity<>(project, HttpStatus.CREATED);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<?> getProjectById(@PathVariable String projectId, Principal principal)
    {
        Project project = projectService.findByProjectIdentifier(projectId, principal.getName());
        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @GetMapping("/all")
    public Iterable<?> getAllProjects(Principal principal)
    {
        return projectService.findAllProjects(principal.getName());
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProjectById(@PathVariable String projectId, Principal principal)
    {
        projectService.deleteProjectByIdentifier(projectId, principal.getName());
        return new ResponseEntity<>("Project with ID '" + projectId + "' was deleted.", HttpStatus.OK);
    }
}
