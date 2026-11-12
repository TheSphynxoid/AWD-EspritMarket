package net.thesphynx.marketplace.service;

import java.math.BigDecimal;
import java.util.List;
import net.thesphynx.marketplace.dto.AjouterAuPanierRequest;
import net.thesphynx.marketplace.dto.LignePanierResponse;
import net.thesphynx.marketplace.dto.PanierResponse;
import net.thesphynx.marketplace.dto.ModifierLignePanierRequest;
import net.thesphynx.marketplace.entity.Panier;
import net.thesphynx.marketplace.entity.LignePanier;
import net.thesphynx.marketplace.entity.Product;
import net.thesphynx.marketplace.entity.ShopStatus;
import net.thesphynx.marketplace.exception.ResourceNotFoundException;
import net.thesphynx.marketplace.repository.LignePanierRepository;
import net.thesphynx.marketplace.repository.PanierRepository;
import net.thesphynx.marketplace.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PanierService {
    private final PanierRepository paniers;
    private final LignePanierRepository lignes;
    private final ProductRepository products;

    public PanierService(PanierRepository paniers, LignePanierRepository lignes, ProductRepository products) {
        this.paniers = paniers;
        this.lignes = lignes;
        this.products = products;
    }

    @Transactional
    public PanierResponse getPanier(String userId) {
        return toResponse(getOrCreatePanier(userId));
    }

    public PanierResponse addItem(String userId, AjouterAuPanierRequest request) {
        Panier panier = getOrCreatePanier(userId);
        Product product = findProduct(request.productId());
        validateProduct(product);

        LignePanier ligne = lignes.findByPanierIdAndProductId(panier.getId(), product.getId()).orElse(null);
        int requestedQuantity = ligne == null ? request.quantity() : ligne.getQuantity() + request.quantity();
        validateStock(product, requestedQuantity);

        if (ligne == null) {
            ligne = new LignePanier();
            ligne.setProduct(product);
            ligne.setQuantity(request.quantity());
            ligne.setUnitPrice(product.getPrice());
            panier.addLigne(ligne);
        } else {
            ligne.setQuantity(requestedQuantity);
        }
        paniers.save(panier);
        return toResponse(panier);
    }

    public PanierResponse updateItem(String userId, Long productId, ModifierLignePanierRequest request) {
        Panier panier = findPanier(userId);
        LignePanier ligne = findLignePanier(panier, productId);
        Product product = ligne.getProduct();
        validateProduct(product);
        validateStock(product, request.quantity());
        ligne.setQuantity(request.quantity());
        return toResponse(panier);
    }

    public void removeItem(String userId, Long productId) {
        Panier panier = findPanier(userId);
        LignePanier ligne = findLignePanier(panier, productId);
        panier.getLignes().remove(ligne);
        lignes.delete(ligne);
    }

    public void clearPanier(String userId) {
        Panier panier = findPanier(userId);
        panier.getLignes().clear();
    }

    private Panier getOrCreatePanier(String userId) {
        return paniers.findByUserId(userId).orElseGet(() -> {
            Panier panier = new Panier();
            panier.setUserId(userId);
            return paniers.save(panier);
        });
    }

    private Panier findPanier(String userId) {
        return paniers.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Panier introuvable pour l'utilisateur: " + userId));
    }

    private Product findProduct(Long productId) {
        return products.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit introuvable: " + productId));
    }

    private LignePanier findLignePanier(Panier panier, Long productId) {
        return lignes.findByPanierIdAndProductId(panier.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit absent du panier: " + productId));
    }

    private void validateProduct(Product product) {
        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new IllegalStateException("Le produit doit être actif");
        }
        if (product.getShop().getStatus() != ShopStatus.ACTIVE) {
            throw new IllegalStateException("La boutique doit être ACTIVE");
        }
    }

    private void validateStock(Product product, int quantity) {
        if (quantity > product.getStock()) {
            throw new IllegalStateException("La quantité demandée dépasse le stock disponible");
        }
    }

    private PanierResponse toResponse(Panier panier) {
        List<LignePanierResponse> items = panier.getLignes().stream()
                .map(this::toItemResponse)
                .toList();
        BigDecimal total = items.stream()
                .map(LignePanierResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new PanierResponse(panier.getId(), panier.getUserId(), items, total);
    }

    private LignePanierResponse toItemResponse(LignePanier item) {
        BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return new LignePanierResponse(item.getId(), item.getProduct().getId(), item.getProduct().getName(),
                item.getUnitPrice(), item.getQuantity(), subtotal);
    }
}
