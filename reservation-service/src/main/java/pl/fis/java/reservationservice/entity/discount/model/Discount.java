package pl.fis.java.reservationservice.entity.discount.model;

import pl.fis.java.reservationservice.entity.ticket.model.Ticket;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Set;

/**
 * Represents the discount a user can get on a ticket.
 */
@Entity
@Table(name = "discount")
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20, message = "discount name can be up to 20 characters long")
    @Column(nullable = false)
    private String name;

    @NotNull
    @Positive(message = "not a valid percentage rate")
    @Max(value = 100, message = "not a valid percentage rate")
    @Column(nullable = false)
    private Integer amount;

    @OneToMany(mappedBy = "discount", fetch = FetchType.LAZY)
    private Set<Ticket> tickets;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }
}
