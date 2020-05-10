package ru.managementtool.ppmtool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.managementtool.ppmtool.domain.Backlog;
import ru.managementtool.ppmtool.domain.ProjectTask;
import ru.managementtool.ppmtool.exceptions.ProjectException;
import ru.managementtool.ppmtool.repository.BacklogRepository;
import ru.managementtool.ppmtool.repository.ProjectTaskRepository;

import java.util.List;

@Service
public class ProjectTaskService {
    @Autowired
    private BacklogRepository backlogRepository;
    @Autowired
    private ProjectTaskRepository projectTaskRepository;
    @Autowired
    private ProjectService projectService;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username)
    {
        projectIdentifier = projectIdentifier.toUpperCase();
        Backlog backlog = projectService.findByProjectIdentifier(projectIdentifier, username).getBacklog();

        if (backlog == null)
            throw new ProjectException("Project with ID '" + projectIdentifier + "' does not exist.");

        Integer backlogSequence = backlog.getPTSequence();
        backlogSequence++;
        backlog.setPTSequence(backlogSequence);
        projectTask.setBacklog(backlog);

        projectTask.setProjectSequence(backlog.getProjectIdentifier() + "-" + backlogSequence);
        projectTask.setProjectIdentifier(projectIdentifier);

        Integer priority = projectTask.getPriority();
        if (priority == null)
        {
            projectTask.setPriority(3);
        }

        if (projectTask.getStatus() == null || projectTask.getStatus().equals(""))
        {
            projectTask.setStatus("TO_DO");
        }

        return projectTaskRepository.save(projectTask);
    }

    public List<ProjectTask> findBacklogById(String projectId, String username)
    {
        projectService.findByProjectIdentifier(projectId, username);
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(projectId);
    }

    public ProjectTask findPTbyProjectSequence(String backlogId, String sequence, String username)
    {
        projectService.findByProjectIdentifier(backlogId, username);

        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(sequence);

        if (projectTask == null)
            throw new ProjectException("Project task with sequence " + sequence + " does not exist");

        if (!projectTask.getProjectIdentifier().equals(backlogId))
            throw new ProjectException("Project task with " + sequence + " does not exist in project with ID " + backlogId);

        return projectTask;
    }

    public ProjectTask updateByProjectSequence(ProjectTask updatedTask)
    {
        return projectTaskRepository.save(updatedTask);
    }

    public void deletePTbyProjectSequence(String backlog, String sequence, String username)
    {
        projectTaskRepository.delete(findPTbyProjectSequence(backlog, sequence, username));
    }
}
