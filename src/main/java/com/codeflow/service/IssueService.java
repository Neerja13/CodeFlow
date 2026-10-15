package com.codeflow.service;

import com.codeflow.dto.IssueRequest;
import com.codeflow.dto.IssueResponse;
import com.codeflow.entity.Issue;
import com.codeflow.entity.IssueStatus;
import com.codeflow.exception.ResourceNotFoundException;
import com.codeflow.repository.IssueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class IssueService {

    private final IssueRepository issueRepository;

    public IssueService(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    public IssueResponse create(IssueRequest request) {
        Issue issue = new Issue();
        apply(issue, request, true);
        return IssueResponse.from(issueRepository.save(issue));
    }

    @Transactional(readOnly = true)
    public List<IssueResponse> findAll() {
        return issueRepository.findAll().stream()
                .map(IssueResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public IssueResponse findById(Long id) {
        return IssueResponse.from(getIssue(id));
    }

    public IssueResponse update(Long id, IssueRequest request) {
        Issue issue = getIssue(id);
        apply(issue, request, false);
        return IssueResponse.from(issueRepository.save(issue));
    }

    public void delete(Long id) {
        Issue issue = getIssue(id);
        issueRepository.delete(issue);
    }

    private Issue getIssue(Long id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + id));
    }

    private void apply(Issue issue, IssueRequest request, boolean creating) {
        issue.setTitle(request.getTitle().trim());
        issue.setDescription(request.getDescription());
        issue.setGithubIssueNumber(request.getGithubIssueNumber());
        issue.setRepositoryName(request.getRepositoryName());
        if (request.getStatus() != null) {
            issue.setStatus(request.getStatus());
        } else if (creating) {
            issue.setStatus(IssueStatus.OPEN);
        }
    }
}
