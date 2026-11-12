package net.thesphynx.patnership.service;

import feign.FeignException;
import net.thesphynx.patnership.dto.EventResponseDto;
import net.thesphynx.patnership.dto.JobOfferDto;
import net.thesphynx.patnership.entity.JobOffer;
import net.thesphynx.patnership.feignclient.EventClient;
import net.thesphynx.patnership.repository.JobOfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class JobOfferService {

    private static final Logger log = LoggerFactory.getLogger(JobOfferService.class);

    private final JobOfferRepository jobOfferRepository;
    private final PartnerCompanyService partnerCompanyService;
    private final EventClient eventClient;

    // Constructor injection (no @Autowired)
    public JobOfferService(JobOfferRepository jobOfferRepository,
                           PartnerCompanyService partnerCompanyService,
                           EventClient eventClient) {
        this.jobOfferRepository = jobOfferRepository;
        this.partnerCompanyService = partnerCompanyService;
        this.eventClient = eventClient;
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
        jobOffer.setEventId(dto.getEventId());
        if (dto.getCompanyId() != null) {
            jobOffer.setCompany(partnerCompanyService.getPartnerCompany(dto.getCompanyId()));
        }
    }

    /**
     * Converts a JobOffer entity to a DTO, enriching it with event data
     * fetched from the Event microservice via OpenFeign.
     * <p>
     * Graceful degradation: if the Event service is down or the event is not
     * found, the job offer is still returned with null event fields.
     */
    private JobOfferDto toDto(JobOffer jobOffer) {
        Long companyId = jobOffer.getCompany() != null ? jobOffer.getCompany().getId() : null;

        JobOfferDto dto = new JobOfferDto(
                jobOffer.getId(),
                jobOffer.getTitle(),
                jobOffer.getDescription(),
                jobOffer.getType(),
                jobOffer.getLocation(),
                jobOffer.getStatus(),
                companyId
        );

        // --- Enrich with event data via Feign ---
        if (jobOffer.getEventId() != null) {
            dto.setEventId(jobOffer.getEventId());
            try {
                EventResponseDto event = eventClient.findEventById(jobOffer.getEventId());
                dto.setEventName(event.name());
                dto.setEventDate(event.date());
                dto.setEventLocation(event.location());
            } catch (FeignException.NotFound ex) {
                // Event not found (404) — log warning, leave event fields null
                log.warn("Event {} not found in Event service: {}", jobOffer.getEventId(), ex.getMessage());
            } catch (FeignException ex) {
                // Event service is down, timed out, or returned an error
                // Graceful degradation: return the job offer without event details
                log.warn("Failed to fetch event {} from Event service: {} (status: {})",
                        jobOffer.getEventId(), ex.getMessage(), ex.status());
            }
        }

        return dto;
    }
}
