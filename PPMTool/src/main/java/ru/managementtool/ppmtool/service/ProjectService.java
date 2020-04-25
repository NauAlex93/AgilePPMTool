package ru.managementtool.ppmtool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.managementtool.ppmtool.domain.Project;
import ru.managementtool.ppmtool.exceptions.ProjectIdException;
import ru.managementtool.ppmtool.repository.ProjectRepository;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    public Project saveOrUpdate(Project project)
    {
        try
        {
            project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
            return projectRepository.save(project);
        }
        catch (Exception ex)
        {
            throw new ProjectIdException("Project ID '" + project.getProjectIdentifier().toUpperCase() + "' already exists.");
        }
    }

    public Project findByProjectIdentifier(String projectId)
    {
        projectId = projectId.toUpperCase();
        Project project = projectRepository.findByProjectIdentifier(projectId);

        if (project == null)
            throw new ProjectIdException("Project ID '" + projectId + "' does not exist.");

        return project;
    }

    public Iterable<Project> findAllProjects()
    {
        return projectRepository.findAll();
    }

    public void deleteProjectByIdentifier(String projectId)
    {
        Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());

        if (project != null)
            projectRepository.delete(project);
        else
            throw new ProjectIdException("Project with id '" + projectId + "'. Does not exist.");
    }
}
