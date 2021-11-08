package edu.sjsu.cmpe275.Repository;

import edu.sjsu.cmpe275.Model.Passenger;
import edu.sjsu.cmpe275.Model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Override
    List<Reservation> findAll();

    @Query(value = "delete from Reservation r where r.passenger = :id")
    public Boolean deleteReservationsByPassengerId(long id);
}
