package net.thesphynx.patnership.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Entity
@Table(name = "application")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Candidate name is required")
    @Column(name = "candidate_name", nullable = false)
    private String candidateName;

    @Column(name = "candidate_email")
    private String candidateEmail;

    @Column(name = "resume_url", columnDefinition = "text")
    private String resumeUrl;

    private String status;

    private LocalDate date;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "job_offer_id")
    private JobOffer jobOffer;

    public Application() {
    }

    public Application(String candidateName, String candidateEmail, String resumeUrl,
                       String status, LocalDate date, Long userId, JobOffer jobOffer) {
        this.candidateName = candidateName;
        this.candidateEmail = candidateEmail;
        this.resumeUrl = resumeUrl;
        this.status = status;
        this.date = date;
        this.userId = userId;
        this.jobOffer = jobOffer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateEmail() {
        return candidateEmail;
    }

    public void setCandidateEmail(String candidateEmail) {
        this.candidateEmail = candidateEmail;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public JobOffer getJobOffer() {
        return jobOffer;
    }

    public void setJobOffer(JobOffer jobOffer) {
        this.jobOffer = jobOffer;
    }
}
