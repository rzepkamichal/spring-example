package pl.fis.java.reservationservice.mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.fis.java.reservationservice.entity.discount.model.Discount;
import pl.fis.java.reservationservice.entity.discount.repository.DiscountRepository;
import pl.fis.java.reservationservice.entity.reservation.model.Reservation;
import pl.fis.java.reservationservice.entity.reservation.repository.ReservationRepository;
import pl.fis.java.reservationservice.entity.ticket.model.Ticket;
import pl.fis.java.reservationservice.entity.ticket.repository.TicketRepository;

import javax.management.InstanceNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Provides a test-purpose service to make a data pump into the used db.
 * The pumped data is obtained from an extern api.
 * Usage only recommended if regular rdbms is not available.
 */
@Component
public class DumpService {

    private static final Logger logger = Logger.getLogger("DumpService");

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    DiscountRepository discountRepository;

    @Autowired
    TicketRepository ticketRepository;


    private static final String RESERVATIONS_URI = "http://localhost:3000/reservations";
    private static final String TICKETS_URI = "http://localhost:3000/tickets";
    private static final String DISCOUNTS_URI = "http://localhost:3000/discounts";
    private static final String FAIL_MSG = "Data-pump failed";


    public void dump() {

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<List<Reservation>> responseReservation = restTemplate.exchange(
                RESERVATIONS_URI,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Reservation>>() {
                }
        );

        Optional<List<Reservation>> reservations = Optional.ofNullable(responseReservation.getBody());

        if (!reservations.isPresent()) {
            logger.info(FAIL_MSG);
            return;
        }

        ResponseEntity<List<Discount>> responseDiscount = restTemplate.exchange(
                DISCOUNTS_URI,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Discount>>() {
                }
        );

        Optional<List<Discount>> discounts = Optional.ofNullable(responseDiscount.getBody());

        if (!discounts.isPresent()) {
            logger.info(FAIL_MSG);
            return;
        }

        ResponseEntity<List<TicketMockResource>> responseTicket = restTemplate.exchange(
                TICKETS_URI,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TicketMockResource>>() {
                }
        );

        Optional<List<TicketMockResource>> ticketResources = Optional.ofNullable(responseTicket.getBody());

        if (!discounts.isPresent()) {
            logger.info(FAIL_MSG);
            return;
        }

        reservationRepository.saveAll(reservations.get());
        discountRepository.saveAll(discounts.get());

        List<Ticket> tickets = ticketResources.get()
                .stream()
                .map(ticketResorce -> {
                    Ticket ticket = new Ticket();
                    Optional<Discount> discount = discountRepository.findById(ticketResorce.getDiscount().getId());

                    if (!discount.isPresent())
                        ticket.setDiscount(null);
                    else
                        ticket.setDiscount(discount.get());

                    Optional<Reservation> reservation = reservationRepository.findById(ticketResorce.getReservation().getId());

                    if (!reservation.isPresent())
                        ticket.setReservation(null);
                    else
                        ticket.setReservation(reservation.get());

                    ticket.setSeatId(ticketResorce.getSeatId());
                    return ticket;
                })
                .collect(Collectors.toList());

        ticketRepository.saveAll(tickets);
    }

}
