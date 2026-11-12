package net.thesphynx.marketplace.service;

import java.util.List;
import net.thesphynx.marketplace.dto.ShopRequest;
import net.thesphynx.marketplace.dto.ShopResponse;
import net.thesphynx.marketplace.entity.Shop;
import net.thesphynx.marketplace.entity.ShopStatus;
import net.thesphynx.marketplace.exception.DuplicateResourceException;
import net.thesphynx.marketplace.exception.ResourceNotFoundException;
import net.thesphynx.marketplace.messaging.ShopEventProducer;
import net.thesphynx.marketplace.repository.ShopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ShopService {

    // Seuls les emails qui se terminent par ce domaine peuvent ouvrir une boutique
    private static final String ALLOWED_DOMAIN = "@esprit.tn";

    private final ShopRepository repository;
    private final ShopEventProducer producer;

    public ShopService(ShopRepository repository, ShopEventProducer producer) {
        this.repository = repository;
        this.producer = producer;
    }

    public ShopResponse create(ShopRequest request) {
        // 1. Refuser tout email qui n'est pas @esprit.tn (rien n'est enregistré)
        String email = request.ownerEmail().trim().toLowerCase();
        if (!email.endsWith(ALLOWED_DOMAIN)) {
            throw new IllegalStateException("Seul un email @esprit.tn peut ouvrir une boutique");
        }

        if (repository.existsByName(request.name())) throw new DuplicateResourceException("Une boutique existe déjà avec ce nom");

        Shop shop = new Shop();
        copy(request, shop);
        shop.setOwnerEmail(email);
        shop.setStatus(ShopStatus.ACTIVE);   // un email @esprit.tn ouvre la boutique directement
        Shop saved = repository.save(shop);
        producer.shopCreated(saved.getOwnerEmail(), saved.getName(), saved.getStatus());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ShopResponse> findAll() { return repository.findAll().stream().map(this::toResponse).toList(); }

    @Transactional(readOnly = true)
    public ShopResponse findById(Long id) { return toResponse(find(id)); }

    public ShopResponse update(Long id, ShopRequest request) {
        Shop shop = find(id);
        if (!shop.getName().equals(request.name()) && repository.existsByName(request.name()))
            throw new DuplicateResourceException("Une boutique existe déjà avec ce nom");
        copy(request, shop);   // l'email et le statut ne changent pas ici
        return toResponse(repository.save(shop));
    }

    public ShopResponse changeStatus(Long id, ShopStatus status) {
        Shop shop = find(id);
        shop.setStatus(status);
        return toResponse(repository.save(shop));
    }

    public void delete(Long id) { repository.delete(find(id)); }

    private Shop find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Boutique introuvable: " + id));
    }

    private void copy(ShopRequest request, Shop shop) {
        shop.setName(request.name()); shop.setDescription(request.description());
        shop.setLogoUrl(request.logoUrl()); shop.setOwnerId(request.ownerId());
    }

    private ShopResponse toResponse(Shop s) {
        return new ShopResponse(s.getId(), s.getName(), s.getDescription(), s.getLogoUrl(),
                s.getOwnerId(), s.getOwnerEmail(), s.getStatus(), s.getCreatedAt());
    }
}