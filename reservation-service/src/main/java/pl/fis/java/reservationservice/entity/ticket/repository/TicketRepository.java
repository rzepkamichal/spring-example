package pl.fis.java.reservationservice.entity.ticket.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import pl.fis.java.reservationservice.entity.reservation.model.Reservation;
import pl.fis.java.reservationservice.entity.ticket.model.Ticket;

import java.util.List;

@Repository
public interface TicketRepository extends CrudRepository<Ticket, Long> {

    @Query("select t.seatId from Ticket t" +
            " where t.reservation.showId = :showId")
    List<Long> findAllReservedSeatsForShow(@Param("showId") Long showId);
}
