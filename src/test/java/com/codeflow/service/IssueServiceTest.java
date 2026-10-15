package com.codeflow.service;

import com.codeflow.dto.IssueRequest;
import com.codeflow.dto.IssueResponse;
import com.codeflow.entity.IssueStatus;
import com.codeflow.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(IssueService.class)
@ActiveProfiles("test")
class IssueServiceTest {

    @Autowired
    private IssueService issueService;

    @Test
    void createPersistsIssueAndAssignsId() {
        IssueRequest request = new IssueRequest();
        request.setTitle(" First issue ");
        request.setDescription("Stored in H2 for this test");
        request.setRepositoryName("owner/codeflow");

        IssueResponse created = issueService.create(request);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getTitle()).isEqualTo("First issue");
        assertThat(created.getStatus()).isEqualTo(IssueStatus.OPEN);
        assertThat(created.getRepositoryName()).isEqualTo("owner/codeflow");
        assertThat(created.getCreatedAt()).isNotNull();
        assertThat(created.getUpdatedAt()).isNotNull();

        IssueResponse loaded = issueService.findById(created.getId());
        assertThat(loaded.getTitle()).isEqualTo("First issue");
        assertThat(loaded.getDescription()).isEqualTo("Stored in H2 for this test");
    }

    @Test
    void findAllReturnsPersistedIssues() {
        IssueRequest first = new IssueRequest();
        first.setTitle("Alpha");
        IssueRequest second = new IssueRequest();
        second.setTitle("Beta");

        issueService.create(first);
        issueService.create(second);

        List<IssueResponse> issues = issueService.findAll();

        assertThat(issues).extracting(IssueResponse::getTitle).containsExactlyInAnyOrder("Alpha", "Beta");
    }

    @Test
    void findByIdThrowsWhenMissing() {
        assertThatThrownBy(() -> issueService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateChangesPersistedFields() {
        IssueRequest create = new IssueRequest();
        create.setTitle("Original");
        IssueResponse created = issueService.create(create);

        IssueRequest update = new IssueRequest();
        update.setTitle("Updated");
        update.setStatus(IssueStatus.IN_PROGRESS);
        update.setRepositoryName("owner/codeflow");

        IssueResponse updated = issueService.update(created.getId(), update);

        assertThat(updated.getTitle()).isEqualTo("Updated");
        assertThat(updated.getStatus()).isEqualTo(IssueStatus.IN_PROGRESS);
        assertThat(issueService.findById(created.getId()).getTitle()).isEqualTo("Updated");
    }

    @Test
    void deleteRemovesIssue() {
        IssueRequest create = new IssueRequest();
        create.setTitle("To delete");
        IssueResponse created = issueService.create(create);

        issueService.delete(created.getId());

        assertThatThrownBy(() -> issueService.findById(created.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
        assertThat(issueService.findAll()).isEmpty();
    }
}
