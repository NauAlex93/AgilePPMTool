package ru.managementtool.ppmtool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.managementtool.ppmtool.domain.Backlog;
import ru.managementtool.ppmtool.domain.Project;
import ru.managementtool.ppmtool.domain.User;
import ru.managementtool.ppmtool.exceptions.ProjectException;
import ru.managementtool.ppmtool.repository.BacklogRepository;
import ru.managementtool.ppmtool.repository.ProjectRepository;
import ru.managementtool.ppmtool.repository.UserRepository;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private UserRepository userRepository;

    public Project saveOrUpdate(Project project, String username){

        if(project.getId() != null){
            Project existingProject = projectRepository.findByProjectIdentifier(project.getProjectIdentifier());
            if(existingProject !=null &&(!existingProject.getProjectLeader().equals(username))){
                throw new ProjectException("Project not found in your account");
            }else if(existingProject == null){
                throw new ProjectException("Project with ID: '"+project.getProjectIdentifier()+"' cannot be updated because it doesn't exist");
            }
        }

        try{

            User user = userRepository.findByUsername(username);
            project.setUser(user);
            project.setProjectLeader(user.getUsername());
            project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());

            if(project.getId()==null){
                Backlog backlog = new Backlog();
                project.setBacklog(backlog);
                backlog.setProject(project);
                backlog.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
            }

            if(project.getId()!=null){
                project.setBacklog(backlogRepository.findByProjectIdentifier(project.getProjectIdentifier().toUpperCase()));
            }

            return projectRepository.save(project);

        }catch (Exception e){
            throw new ProjectException("Project ID '"+project.getProjectIdentifier().toUpperCase()+"' already exists");
        }
    }

    public Project findByProjectIdentifier(String projectId, String username)
    {
        projectId = projectId.toUpperCase();
        Project project = projectRepository.findByProjectIdentifier(projectId);

        if (project == null)
            throw new ProjectException("Project ID '" + projectId + "' does not exist.");

        if(!project.getProjectLeader().equals(username)){
            throw new ProjectException("Project not found in your account");
        }

        return project;
    }

    public Iterable<Project> findAllProjects(String username)
    {
        return projectRepository.findAllByProjectLeader(username);
    }

    public void deleteProjectByIdentifier(String projectId, String username)
    {
        Project project = findByProjectIdentifier(projectId.toUpperCase(), username);

        if (project != null)
            projectRepository.delete(project);
        else
            throw new ProjectException("Project with id '" + projectId + "'. Does not exist.");
    }
}
