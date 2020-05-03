package ru.managementtool.ppmtool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.managementtool.ppmtool.domain.Backlog;
import ru.managementtool.ppmtool.domain.ProjectTask;
import ru.managementtool.ppmtool.repository.BacklogRepository;
import ru.managementtool.ppmtool.repository.ProjectTaskRepository;

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
        Integer backlogSequence = backlog.getPTSequence();
        backlogSequence++;
        backlog.setPTSequence(backlogSequence);
        projectTask.setBacklog(backlog);

        projectTask.setProjectSequence(backlog.getProjectIdentifier() + " - " + backlogSequence);
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

    public Iterable<ProjectTask> findBacklogById(String projectId)
    {
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(projectId);
    }
}
