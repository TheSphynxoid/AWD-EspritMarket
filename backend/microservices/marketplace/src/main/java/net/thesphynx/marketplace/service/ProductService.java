package net.thesphynx.marketplace.service;

import java.util.List;
import net.thesphynx.marketplace.dto.ProductRequest;
import net.thesphynx.marketplace.dto.ProductResponse;
import net.thesphynx.marketplace.entity.Product;
import net.thesphynx.marketplace.entity.Shop;
import net.thesphynx.marketplace.entity.ShopStatus;
import net.thesphynx.marketplace.exception.ResourceNotFoundException;
import net.thesphynx.marketplace.repository.CategoryRepository;
import net.thesphynx.marketplace.repository.ProductRepository;
import net.thesphynx.marketplace.repository.ShopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {
    private final ProductRepository products;
    private final ShopRepository shops;
    private final CategoryRepository categories;
    public ProductService(ProductRepository products, ShopRepository shops, CategoryRepository categories) {
        this.products = products; this.shops = shops; this.categories = categories;
    }
    public ProductResponse create(Long shopId, ProductRequest request) {
        Shop shop = activeShop(shopId);
        Product product = new Product(); product.setShop(shop); copy(request, product);
        return toResponse(products.save(product));
    }
    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() { return products.findAll().stream().map(this::toResponse).toList(); }
    @Transactional(readOnly = true)
    public List<ProductResponse> findByShop(Long shopId) { findShop(shopId); return products.findByShopId(shopId).stream().map(this::toResponse).toList(); }
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) { return toResponse(find(id)); }
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = find(id); activeShop(product.getShop().getId()); copy(request, product);
        return toResponse(products.save(product));
    }
    public void delete(Long id) { products.delete(find(id)); }
    private Shop activeShop(Long id) {
        Shop shop = findShop(id);
        if (shop.getStatus() != ShopStatus.ACTIVE) throw new IllegalStateException("La boutique doit être ACTIVE");
        return shop;
    }
    private Shop findShop(Long id) { return shops.findById(id).orElseThrow(() -> new ResourceNotFoundException("Boutique introuvable: " + id)); }
    private Product find(Long id) { return products.findById(id).orElseThrow(() -> new ResourceNotFoundException("Produit introuvable: " + id)); }
    private void copy(ProductRequest request, Product product) {
        product.setName(request.name()); product.setDescription(request.description());
        product.setPrice(request.price()); product.setStock(request.stock()); product.setImageUrl(request.imageUrl());
        if (request.categoryId() == null) product.setCategory(null);
        else product.setCategory(categories.findById(request.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable: " + request.categoryId())));
    }
    private ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock(), p.getImageUrl(), p.getActive(),
                p.getCreatedAt(), p.getUpdatedAt(), p.getShop().getId(), p.getShop().getName(),
                p.getCategory() == null ? null : p.getCategory().getId(), p.getCategory() == null ? null : p.getCategory().getName());
    }
}
