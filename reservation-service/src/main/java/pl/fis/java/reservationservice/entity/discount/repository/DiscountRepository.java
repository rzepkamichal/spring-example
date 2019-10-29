package pl.fis.java.reservationservice.entity.discount.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.fis.java.reservationservice.entity.discount.model.Discount;


@Repository
public interface DiscountRepository extends CrudRepository<Discount, Long> {
}
