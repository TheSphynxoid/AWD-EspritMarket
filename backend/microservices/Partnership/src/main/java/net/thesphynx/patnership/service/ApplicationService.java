package net.thesphynx.patnership.service;

import net.thesphynx.patnership.dto.ApplicationDto;
import net.thesphynx.patnership.entity.Application;
import net.thesphynx.patnership.repository.ApplicationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobOfferService jobOfferService;

    public ApplicationService(ApplicationRepository applicationRepository,
                              JobOfferService jobOfferService) {
        this.applicationRepository = applicationRepository;
        this.jobOfferService = jobOfferService;
    }

    public List<ApplicationDto> findAll() {
        return applicationRepository.findAll().stream().map(this::toDto).toList();
    }

    public ApplicationDto findById(Long id) {
        return toDto(getApplication(id));
    }

    public List<ApplicationDto> findByJobOfferId(Long jobOfferId) {
        return applicationRepository.findByJobOfferId(jobOfferId).stream().map(this::toDto).toList();
    }

    public List<ApplicationDto> findByUserId(Long userId) {
        return applicationRepository.findByUserId(userId).stream().map(this::toDto).toList();
    }

    public ApplicationDto create(ApplicationDto dto) {
        return toDto(applicationRepository.save(toEntity(dto)));
    }

    public ApplicationDto update(Long id, ApplicationDto dto) {
        Application application = getApplication(id);
        updateEntity(application, dto);
        return toDto(applicationRepository.save(application));
    }

    public void delete(Long id) {
        applicationRepository.delete(getApplication(id));
    }

    public Application getApplication(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Candidature introuvable : " + id));
    }

    private Application toEntity(ApplicationDto dto) {
        Application application = new Application();
        updateEntity(application, dto);
        return application;
    }

    private void updateEntity(Application application, ApplicationDto dto) {
        application.setCandidateName(dto.getCandidateName());
        application.setCandidateEmail(dto.getCandidateEmail());
        application.setResumeUrl(dto.getResumeUrl());
        application.setStatus(dto.getStatus());
        application.setDate(dto.getDate());
        application.setUserId(dto.getUserId());
        if (dto.getJobOfferId() != null) {
            application.setJobOffer(jobOfferService.getJobOffer(dto.getJobOfferId()));
        }
    }

    private ApplicationDto toDto(Application application) {
        Long jobOfferId = application.getJobOffer() != null ? application.getJobOffer().getId() : null;
        return new ApplicationDto(
                application.getId(),
                application.getCandidateName(),
                application.getCandidateEmail(),
                application.getResumeUrl(),
                application.getStatus(),
                application.getDate(),
                application.getUserId(),
                jobOfferId
        );
    }
}
