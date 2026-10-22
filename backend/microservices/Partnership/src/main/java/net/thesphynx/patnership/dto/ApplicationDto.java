package net.thesphynx.patnership.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ApplicationDto {

    private Long id;

    @NotBlank(message = "Le nom du candidat est obligatoire")
    private String candidateName;

    private String candidateEmail;
    private String resumeUrl;
    private String status;
    private LocalDate date;
    private Long userId;

    @NotNull(message = "L'offre d'emploi est obligatoire")
    private Long jobOfferId;

    public ApplicationDto() {
    }

    public ApplicationDto(Long id, String candidateName, String candidateEmail,
                          String resumeUrl, String status, LocalDate date,
                          Long userId, Long jobOfferId) {
        this.id = id;
        this.candidateName = candidateName;
        this.candidateEmail = candidateEmail;
        this.resumeUrl = resumeUrl;
        this.status = status;
        this.date = date;
        this.userId = userId;
        this.jobOfferId = jobOfferId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getCandidateEmail() { return candidateEmail; }
    public void setCandidateEmail(String candidateEmail) { this.candidateEmail = candidateEmail; }

    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getJobOfferId() { return jobOfferId; }
    public void setJobOfferId(Long jobOfferId) { this.jobOfferId = jobOfferId; }
}
