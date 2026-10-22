package net.thesphynx.patnership.service;

import net.thesphynx.patnership.dto.JobOfferDto;
import net.thesphynx.patnership.entity.JobOffer;
import net.thesphynx.patnership.repository.JobOfferRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class JobOfferService {

    private final JobOfferRepository jobOfferRepository;
    private final PartnerCompanyService partnerCompanyService;

    public JobOfferService(JobOfferRepository jobOfferRepository,
                           PartnerCompanyService partnerCompanyService) {
        this.jobOfferRepository = jobOfferRepository;
        this.partnerCompanyService = partnerCompanyService;
    }

    public List<JobOfferDto> findAll() {
        return jobOfferRepository.findAll().stream().map(this::toDto).toList();
    }

    public JobOfferDto findById(Long id) {
        return toDto(getJobOffer(id));
    }

    public List<JobOfferDto> findByCompanyId(Long companyId) {
        return jobOfferRepository.findByCompanyId(companyId).stream().map(this::toDto).toList();
    }

    public JobOfferDto create(JobOfferDto dto) {
        return toDto(jobOfferRepository.save(toEntity(dto)));
    }

    public JobOfferDto update(Long id, JobOfferDto dto) {
        JobOffer jobOffer = getJobOffer(id);
        updateEntity(jobOffer, dto);
        return toDto(jobOfferRepository.save(jobOffer));
    }

    public void delete(Long id) {
        jobOfferRepository.delete(getJobOffer(id));
    }

    public JobOffer getJobOffer(Long id) {
        return jobOfferRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Offre d'emploi introuvable : " + id));
    }

    private JobOffer toEntity(JobOfferDto dto) {
        JobOffer jobOffer = new JobOffer();
        updateEntity(jobOffer, dto);
        return jobOffer;
    }

    private void updateEntity(JobOffer jobOffer, JobOfferDto dto) {
        jobOffer.setTitle(dto.getTitle());
        jobOffer.setDescription(dto.getDescription());
        jobOffer.setType(dto.getType());
        jobOffer.setLocation(dto.getLocation());
        jobOffer.setStatus(dto.getStatus());
        if (dto.getCompanyId() != null) {
            jobOffer.setCompany(partnerCompanyService.getPartnerCompany(dto.getCompanyId()));
        }
    }

    private JobOfferDto toDto(JobOffer jobOffer) {
        Long companyId = jobOffer.getCompany() != null ? jobOffer.getCompany().getId() : null;
        return new JobOfferDto(
                jobOffer.getId(),
                jobOffer.getTitle(),
                jobOffer.getDescription(),
                jobOffer.getType(),
                jobOffer.getLocation(),
                jobOffer.getStatus(),
                companyId
        );
    }
}
