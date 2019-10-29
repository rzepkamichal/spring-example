package pl.fis.java.reservationservice.mock;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used as DTO for Ticket object deserialization.
 * Ticket references Reservation and Discount, which are passed as links.
 * This DTO is needed, when trying to deserialize a Ticket-JSON object, which has nested referenced
 * objects and not links to them.
 */
public class TicketMockResource {

    @JsonProperty
    private Long seatId;

    @JsonProperty
    private ReservationMockResource reservation;

    @JsonProperty
    private DiscountMockResource discount;

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public ReservationMockResource getReservation() {
        return reservation;
    }

    public void setReservation(ReservationMockResource reservation) {
        this.reservation = reservation;
    }

    public DiscountMockResource getDiscount() {
        return discount;
    }

    public void setDiscount(DiscountMockResource discount) {
        this.discount = discount;
    }
}
