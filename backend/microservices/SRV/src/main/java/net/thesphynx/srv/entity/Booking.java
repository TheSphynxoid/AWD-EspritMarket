package net.thesphynx.srv.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private ServiceOffer service;

    private String providerId;

    /** Client : identifiant Keycloak (sub), nom affiché et email (notifications). */
    private String bookerId;
    private String bookerName;
    private String bookerEmail;

    /** Date / heure souhaitée par le client. */
    private LocalDateTime scheduledAt;

    /** Message du client au prestataire. */
    @Column(length = 1000)
    private String note;

    /** Réponse du prestataire (motif de refus, précisions...). */
    @Column(length = 1000)
    private String providerNote;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BookingStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = BookingStatus.PENDING;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
