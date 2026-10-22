package net.thesphynx.marketplace.service;

import java.util.List;
import net.thesphynx.marketplace.dto.ShopRequest;
import net.thesphynx.marketplace.dto.ShopResponse;
import net.thesphynx.marketplace.entity.Shop;
import net.thesphynx.marketplace.entity.ShopStatus;
import net.thesphynx.marketplace.exception.DuplicateResourceException;
import net.thesphynx.marketplace.exception.ResourceNotFoundException;
import net.thesphynx.marketplace.repository.ShopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ShopService {
    private final ShopRepository repository;
    public ShopService(ShopRepository repository) { this.repository = repository; }

    public ShopResponse create(ShopRequest request) {
        if (repository.existsByName(request.name())) throw new DuplicateResourceException("Une boutique existe déjà avec ce nom");
        Shop shop = new Shop();
        copy(request, shop);
        return toResponse(repository.save(shop));
    }
    @Transactional(readOnly = true)
    public List<ShopResponse> findAll() { return repository.findAll().stream().map(this::toResponse).toList(); }
    @Transactional(readOnly = true)
    public ShopResponse findById(Long id) { return toResponse(find(id)); }
    public ShopResponse update(Long id, ShopRequest request) {
        Shop shop = find(id);
        if (!shop.getName().equals(request.name()) && repository.existsByName(request.name()))
            throw new DuplicateResourceException("Une boutique existe déjà avec ce nom");
        copy(request, shop);
        return toResponse(repository.save(shop));
    }
    public ShopResponse changeStatus(Long id, ShopStatus status) {
        Shop shop = find(id);
        shop.setStatus(status);
        return toResponse(repository.save(shop));
    }
    public void delete(Long id) { repository.delete(find(id)); }
    private Shop find(Long id) { return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Boutique introuvable: " + id)); }
    private void copy(ShopRequest request, Shop shop) {
        shop.setName(request.name()); shop.setDescription(request.description());
        shop.setLogoUrl(request.logoUrl()); shop.setOwnerId(request.ownerId());
    }
    private ShopResponse toResponse(Shop s) { return new ShopResponse(s.getId(), s.getName(), s.getDescription(), s.getLogoUrl(), s.getOwnerId(), s.getStatus(), s.getCreatedAt()); }
}
