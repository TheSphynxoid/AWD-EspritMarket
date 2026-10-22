package net.thesphynx.patnership.service;

import net.thesphynx.patnership.dto.PartnerCompanyDto;
import net.thesphynx.patnership.entity.PartnerCompany;
import net.thesphynx.patnership.repository.PartnerCompanyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PartnerCompanyService {

    private final PartnerCompanyRepository partnerCompanyRepository;

    public PartnerCompanyService(PartnerCompanyRepository partnerCompanyRepository) {
        this.partnerCompanyRepository = partnerCompanyRepository;
    }

    public List<PartnerCompanyDto> findAll() {
        return partnerCompanyRepository.findAll().stream().map(this::toDto).toList();
    }

    public PartnerCompanyDto findById(Long id) {
        return toDto(getPartnerCompany(id));
    }

    public PartnerCompanyDto create(PartnerCompanyDto dto) {
        return toDto(partnerCompanyRepository.save(toEntity(dto)));
    }

    public PartnerCompanyDto update(Long id, PartnerCompanyDto dto) {
        PartnerCompany company = getPartnerCompany(id);
        updateEntity(company, dto);
        return toDto(partnerCompanyRepository.save(company));
    }

    public void delete(Long id) {
        partnerCompanyRepository.delete(getPartnerCompany(id));
    }

    public PartnerCompany getPartnerCompany(Long id) {
        return partnerCompanyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Entreprise partenaire introuvable : " + id));
    }

    private PartnerCompany toEntity(PartnerCompanyDto dto) {
        PartnerCompany company = new PartnerCompany();
        updateEntity(company, dto);
        return company;
    }

    private void updateEntity(PartnerCompany company, PartnerCompanyDto dto) {
        company.setName(dto.getName());
        company.setSector(dto.getSector());
        company.setContactEmail(dto.getContactEmail());
        company.setPartnershipStatus(dto.getPartnershipStatus());
        company.setWebsite(dto.getWebsite());
        company.setPhone(dto.getPhone());
    }

    private PartnerCompanyDto toDto(PartnerCompany company) {
        return new PartnerCompanyDto(
                company.getId(),
                company.getName(),
                company.getSector(),
                company.getContactEmail(),
                company.getPartnershipStatus(),
                company.getWebsite(),
                company.getPhone()
        );
    }
}
