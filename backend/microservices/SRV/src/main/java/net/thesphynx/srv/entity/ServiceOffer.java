package net.thesphynx.srv.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    private String category;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    /** Durée indicative de la prestation, en minutes. */
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ServiceMode mode;

    /** Ville / lieu (sur place). */
    private String location;

    @Column(length = 1000)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ServiceStatus status;

    /** Prestataire : identifiant Keycloak (sub), nom affiché et email (notifications). */
    private String providerId;
    private String providerName;
    private String providerEmail;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = ServiceStatus.ACTIVE;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
