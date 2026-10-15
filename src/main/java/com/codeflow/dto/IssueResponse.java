package com.codeflow.dto;

import com.codeflow.entity.Issue;
import com.codeflow.entity.IssueStatus;

import java.time.Instant;

public class IssueResponse {

    private Long id;
    private String title;
    private String description;
    private IssueStatus status;
    private Integer githubIssueNumber;
    private String repositoryName;
    private Instant createdAt;
    private Instant updatedAt;

    public static IssueResponse from(Issue issue) {
        IssueResponse response = new IssueResponse();
        response.id = issue.getId();
        response.title = issue.getTitle();
        response.description = issue.getDescription();
        response.status = issue.getStatus();
        response.githubIssueNumber = issue.getGithubIssueNumber();
        response.repositoryName = issue.getRepositoryName();
        response.createdAt = issue.getCreatedAt();
        response.updatedAt = issue.getUpdatedAt();
        return response;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public Integer getGithubIssueNumber() {
        return githubIssueNumber;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
