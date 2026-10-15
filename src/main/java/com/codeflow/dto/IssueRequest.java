package com.codeflow.dto;

import com.codeflow.entity.IssueStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class IssueRequest {

    @NotBlank(message = "title is required")
    @Size(max = 255, message = "title must be at most 255 characters")
    private String title;

    private String description;

    private IssueStatus status;

    private Integer githubIssueNumber;

    @Size(max = 255, message = "repositoryName must be at most 255 characters")
    private String repositoryName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public Integer getGithubIssueNumber() {
        return githubIssueNumber;
    }

    public void setGithubIssueNumber(Integer githubIssueNumber) {
        this.githubIssueNumber = githubIssueNumber;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }
}
