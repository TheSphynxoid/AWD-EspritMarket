package net.thesphynx.srv.service;

import net.thesphynx.srv.entity.ServiceOffer;
import net.thesphynx.srv.repository.ServiceOfferRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceOfferService {

    private final ServiceOfferRepository repository;

    public ServiceOfferService(ServiceOfferRepository repository) {
        this.repository = repository;
    }

    public List<ServiceOffer> findAll() {
        return repository.findAll();
    }

    public Optional<ServiceOffer> findById(Long id) {
        return repository.findById(id);
    }

    public ServiceOffer create(ServiceOffer offer) {
        offer.setId(null);
        return repository.save(offer);
    }

    public Optional<ServiceOffer> update(Long id, ServiceOffer offer) {
        return repository.findById(id).map(existing -> {
            existing.setName(offer.getName());
            existing.setDescription(offer.getDescription());
            existing.setCategory(offer.getCategory());
            existing.setPrice(offer.getPrice());
            existing.setStatus(offer.getStatus());
            existing.setProviderId(offer.getProviderId());
            return repository.save(existing);
        });
    }

    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}
