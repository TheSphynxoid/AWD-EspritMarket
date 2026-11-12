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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class ShopService {

    private final ShopRepository repository;
    private final ShopEventProducer producer;

    public ShopService(ShopRepository repository, ShopEventProducer producer) {
        this.repository = repository;
        this.producer = producer;
    }

    public ShopResponse create(ShopRequest request) {
        String email = request.ownerEmail().trim().toLowerCase();
        // Accepter toute adresse e-mail contenant '@' (pas seulement @esprit.tn)
        if (!email.contains("@")) {
            throw new IllegalArgumentException("L'adresse e-mail doit contenir '@'");
        }

        if (repository.existsByName(request.name())) throw new DuplicateResourceException("Une boutique existe déjà avec ce nom");

        Shop shop = new Shop();
        copy(request, shop);
        shop.setOwnerEmail(email);
        // Toute adresse e-mail contenant '@' active la boutique directement
        shop.setStatus(ShopStatus.ACTIVE);
        Shop saved = repository.save(shop);
        log.info("[ASYNC] Boutique creee id={}, email={}, status={}", saved.getId(), saved.getOwnerEmail(), saved.getStatus());
        producer.shopCreated(saved.getOwnerEmail(), saved.getName(), saved.getStatus());
        log.info("[ASYNC] Message 'shop.created' publie dans RabbitMQ pour {}", saved.getOwnerEmail());
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