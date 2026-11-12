package net.thesphynx.srv.entity;

/**
 * Cycle de vie d'une réservation :
 *   PENDING -> ACCEPTED | DECLINED  (prestataire)
 *   ACCEPTED -> COMPLETED           (prestataire)
 *   PENDING | ACCEPTED -> CANCELLED (client)
 */
public enum BookingStatus { PENDING, ACCEPTED, DECLINED, CANCELLED, COMPLETED }
