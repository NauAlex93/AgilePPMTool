package ru.managementtool.ppmtool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.managementtool.ppmtool.domain.Backlog;
import ru.managementtool.ppmtool.domain.Project;
import ru.managementtool.ppmtool.exceptions.ProjectException;
import ru.managementtool.ppmtool.repository.BacklogRepository;
import ru.managementtool.ppmtool.repository.ProjectRepository;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BacklogRepository backlogRepository;

    public Project saveOrUpdate(Project project)
    {
        try
        {
            String projectIdentifier = project.getProjectIdentifier().toUpperCase();
            project.setProjectIdentifier(projectIdentifier);

            if (project.getId() == null) {
                Backlog backlog = new Backlog();
                project.setBacklog(backlog);
                backlog.setProject(project);
                backlog.setProjectIdentifier(projectIdentifier);
            }
            else
            {
                project.setBacklog(backlogRepository.findByProjectIdentifier(projectIdentifier));
            }

            return projectRepository.save(project);
        }
        catch (Exception ex)
        {
            throw new ProjectException("Project ID '" + project.getProjectIdentifier().toUpperCase() + "' already exists.");
        }
    }

    public Project findByProjectIdentifier(String projectId)
    {
        projectId = projectId.toUpperCase();
        Project project = projectRepository.findByProjectIdentifier(projectId);

        if (project == null)
            throw new ProjectException("Project ID '" + projectId + "' does not exist.");

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
            throw new ProjectException("Project with id '" + projectId + "'. Does not exist.");
    }
}
