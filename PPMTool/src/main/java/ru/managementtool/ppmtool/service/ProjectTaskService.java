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

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask)
    {
        projectIdentifier = projectIdentifier.toUpperCase();
        Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);

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

    public List<ProjectTask> findBacklogById(String projectId)
    {
        List<ProjectTask> projectTasks = projectTaskRepository.findByProjectIdentifierOrderByPriority(projectId);
        if (projectTasks.isEmpty())
        {
            throw new ProjectException("Project with ID " + projectId + " does not exist");
        }
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(projectId);
    }

    public ProjectTask findPTbyProjectSequence(String backlogId, String sequence)
    {
        Backlog backlog = backlogRepository.findByProjectIdentifier(backlogId);

        if (backlog == null)
            throw new ProjectException("Project with ID " + backlogId + " does not exist");

        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(sequence);

        if (projectTask == null)
            throw new ProjectException("Project task with sequence " + sequence + " does not exist");

        if (!projectTask.getProjectIdentifier().equals(backlogId))
            throw new ProjectException("Project task with " + sequence + " does not exist in project with ID " + backlogId);

        return projectTask;
    }
}
