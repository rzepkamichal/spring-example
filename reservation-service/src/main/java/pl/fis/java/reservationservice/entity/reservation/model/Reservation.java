package pl.fis.java.reservationservice.entity.reservation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.rest.core.annotation.RestResource;
import pl.fis.java.reservationservice.entity.ticket.model.Ticket;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Represents a single reservation, which is assigned to a particular user and a given show.
 * References a collection of assigned tickets.
 */
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @RestResource(exported = false)
    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @RestResource(exported = false)
    @NotNull
    @Column(name = "show_id", nullable = false)
    private Long showId;


    @NotNull
    @PastOrPresent
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MMM-yyyy h:mm a")
    @Column(nullable = false)
    private LocalDateTime time;

    @OneToMany(mappedBy = "reservation", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Ticket> tickets;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getShowId() {
        return showId;
    }

    public void setShowId(Long showId) {
        this.showId = showId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }
}
