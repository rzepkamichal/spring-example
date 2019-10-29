package pl.fis.java.reservationservice.seat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import pl.fis.java.reservationservice.entity.discount.repository.DiscountRepository;
import pl.fis.java.reservationservice.entity.reservation.repository.ReservationRepository;
import pl.fis.java.reservationservice.entity.ticket.repository.TicketRepository;
import pl.fis.java.reservationservice.mock.DumpService;
import pl.fis.java.reservationservice.seat.dto.Seat;
import pl.fis.java.reservationservice.seat.dto.Show;
import pl.fis.java.reservationservice.seat.dto.util.SeatMapper;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/seats-for-show")
@Transactional
public class SeatController {

    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    DiscountRepository discountRepository;
    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    DumpService dumpService;

    private void doDumpIfRepoEmpty() {
        boolean reservationRepoEmpty = StreamSupport
                .stream(reservationRepository.findAll().spliterator(), false)
                .collect(Collectors.toList()).isEmpty();

        boolean discountRepoEmpty = StreamSupport
                .stream(discountRepository.findAll().spliterator(), false)
                .collect(Collectors.toList()).isEmpty();

        boolean ticketRepoEmpty = StreamSupport
                .stream(ticketRepository.findAll().spliterator(), false)
                .collect(Collectors.toList()).isEmpty();

        if (reservationRepoEmpty || discountRepoEmpty || ticketRepoEmpty)
            dumpService.dump();
    }


    @GetMapping(value = "/{show_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Seat>> getSeatsByShowId(@PathVariable(name = "show_id") Long showId) {


        RestTemplate restTemplate = new RestTemplate();
        List<Seat> results = new ArrayList<>();

        //all booked seatIds for the given show
        List<Long> bookedSeatIds = ticketRepository.findAllReservedSeatsForShow(showId);

        //get show from show-service by id
        Optional<ServiceInstance> showService = discoveryClient.getInstances("show-service").stream().findFirst();

        if (showService.isEmpty()) {
            return new ResponseEntity<>(results, HttpStatus.NOT_FOUND);
        }

        final String showResourceUri = showService.get().getUri() + "/api/show/shows/" + showId.toString();
        ResponseEntity<Show> show = restTemplate.getForEntity(showResourceUri, Show.class);

        if (!Optional.ofNullable(show.getBody()).isPresent()) {
            return new ResponseEntity<>(results, HttpStatus.NOT_FOUND);
        }

        //retrieve hall id to be able to search for its seats
        Long hallId = show.getBody().getHallId();

        //get seats from cinema service by hall id
        Optional<ServiceInstance> cinemaService = discoveryClient.getInstances("cinema-service").stream().findFirst();

        if (cinemaService.isEmpty()) {
            return new ResponseEntity<>(results, HttpStatus.NOT_FOUND);
        }

        final String seatsFromHallUri = cinemaService.get().getUri() + "/api/cinema/halls/"
                + hallId.toString() + "/seats";

        ResponseEntity<JsonNode> jsonNode = restTemplate.getForEntity(seatsFromHallUri, JsonNode.class);

        Optional<List<Seat>> seatsbyHallId = SeatMapper.map(jsonNode.getBody());

        if (seatsbyHallId.isEmpty()) {
            return new ResponseEntity<>(results, HttpStatus.NOT_FOUND);
        }

        //check which seats are available and set the appropriate value
        seatsbyHallId.get()
                .stream()
                .forEach(seat -> {
                    seat.setAvailable(false);
                    boolean isSeatReserved = bookedSeatIds
                            .stream()
                            .map(id -> id.longValue())
                            .collect(Collectors.toList())
                            .contains(seat.getId().longValue());
                    if (!isSeatReserved)
                        seat.setAvailable(true);
                });

        results.addAll(seatsbyHallId.get());
        return new ResponseEntity<>(results, HttpStatus.OK);

    }
}
