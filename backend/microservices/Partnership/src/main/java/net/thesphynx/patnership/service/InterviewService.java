package net.thesphynx.patnership.service;

import net.thesphynx.patnership.dto.InterviewDto;
import net.thesphynx.patnership.entity.Interview;
import net.thesphynx.patnership.repository.InterviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationService applicationService;

    public InterviewService(InterviewRepository interviewRepository,
                            ApplicationService applicationService) {
        this.interviewRepository = interviewRepository;
        this.applicationService = applicationService;
    }

    public List<InterviewDto> findAll() {
        return interviewRepository.findAll().stream().map(this::toDto).toList();
    }

    public InterviewDto findById(Long id) {
        return toDto(getInterview(id));
    }

    public List<InterviewDto> findByApplicationId(Long applicationId) {
        return interviewRepository.findByApplicationId(applicationId).stream().map(this::toDto).toList();
    }

    public InterviewDto create(InterviewDto dto) {
        return toDto(interviewRepository.save(toEntity(dto)));
    }

    public InterviewDto update(Long id, InterviewDto dto) {
        Interview interview = getInterview(id);
        updateEntity(interview, dto);
        return toDto(interviewRepository.save(interview));
    }

    public void delete(Long id) {
        interviewRepository.delete(getInterview(id));
    }

    public Interview getInterview(Long id) {
        return interviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Entretien introuvable : " + id));
    }

    private Interview toEntity(InterviewDto dto) {
        Interview interview = new Interview();
        updateEntity(interview, dto);
        return interview;
    }

    private void updateEntity(Interview interview, InterviewDto dto) {
        interview.setDate(dto.getDate());
        interview.setLocation(dto.getLocation());
        interview.setType(dto.getType());
        interview.setStatus(dto.getStatus());
        interview.setNotes(dto.getNotes());
        if (dto.getApplicationId() != null) {
            interview.setApplication(applicationService.getApplication(dto.getApplicationId()));
        }
    }

    private InterviewDto toDto(Interview interview) {
        Long applicationId = interview.getApplication() != null ? interview.getApplication().getId() : null;
        return new InterviewDto(
                interview.getId(),
                interview.getDate(),
                interview.getLocation(),
                interview.getType(),
                interview.getStatus(),
                interview.getNotes(),
                applicationId
        );
    }
}
