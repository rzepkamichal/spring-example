package pl.fis.java.reservationservice.entity.ticket.model;

import org.springframework.data.rest.core.annotation.RestResource;
import pl.fis.java.reservationservice.entity.discount.model.Discount;
import pl.fis.java.reservationservice.entity.reservation.model.Reservation;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Represents a single ticket - one ticket, one seat on a show.
 * References a particular reservation and discount.
 */
@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @RestResource(exported = false)
    @NotNull
    @Column(name = "seat_id")
    private Long seatId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "discount_id", nullable = false)
    private Discount discount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }
}
