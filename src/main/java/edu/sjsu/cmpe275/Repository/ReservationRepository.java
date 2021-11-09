package edu.sjsu.cmpe275.Repository;

import edu.sjsu.cmpe275.Model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Override
    List<Reservation> findAll();
}
