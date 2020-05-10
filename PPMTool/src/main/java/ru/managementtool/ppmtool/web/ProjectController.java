package ru.managementtool.ppmtool.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
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

@RestController
@RequestMapping("/api/project")
@CrossOrigin    // react app trying to get access from another port
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @PostMapping("")
    public ResponseEntity<?> createNewProject(@Valid @RequestBody Project project, BindingResult result)
    {
        ResponseEntity<?> errorMap = BindingResultValidator.validateBindingResult(result);

        if (errorMap != null)
            return errorMap;

        project = projectService.saveOrUpdate(project);
        return new ResponseEntity<>(project, HttpStatus.CREATED);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<?> getProjectById(@PathVariable String projectId)
    {
        Project project = projectService.findByProjectIdentifier(projectId);
        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @GetMapping("/all")
    public Iterable<?> getAllProjects()
    {
        return projectService.findAllProjects();
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProjectById(@PathVariable String projectId)
    {
        projectService.deleteProjectByIdentifier(projectId);
        return new ResponseEntity<>("Project with ID '" + projectId + "' was deleted.", HttpStatus.OK);
    }
}
