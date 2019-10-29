package pl.fis.java.reservationservice.seat.dto.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import pl.fis.java.reservationservice.seat.dto.Seat;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SeatMapper {

    private static Optional<Long> mapId(String linkWithId) {

        int lastSlashIndex = linkWithId.lastIndexOf("/");

        Long result = null;

        if (lastSlashIndex >= linkWithId.length()) {
            return Optional.ofNullable(result);
        }

        result = Long.valueOf(linkWithId.substring(lastSlashIndex + 1));

        return Optional.ofNullable(result);
    }

    public static Optional<List<Seat>> map(JsonNode cinemaServiceResponse) {

        JsonNode embeddedNode = cinemaServiceResponse.get("_embedded");
        JsonNode seatsNode = embeddedNode.get("seats");
        ObjectMapper mapper = new ObjectMapper();

        List<Seat> seats = new LinkedList<>();
        seatsNode.forEach(node -> {
            Seat seat = new Seat();
            seat.setAvailable(false);
            seat.setColumn(node.get("column").asInt());
            seat.setRow(node.get("row").asInt());

            //Warning! need to add check for NullNode!
            Optional<Long> seatId = mapId(node.get("_links")
                    .get("self")
                    .get("href")
                    .asText());
            seatId.ifPresentOrElse(value -> seat.setId(value), ()->seat.setId(null));
            seats.add(seat);
        });


        return Optional.ofNullable(seats);
    }
}
