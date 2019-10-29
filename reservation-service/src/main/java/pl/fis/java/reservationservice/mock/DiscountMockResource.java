package pl.fis.java.reservationservice.mock;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used to initialize embedded object in TicketMockResource from serialized input.
 */
public class DiscountMockResource {

    @JsonProperty
    private Long id;

    DiscountMockResource() {
    }

    DiscountMockResource(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
