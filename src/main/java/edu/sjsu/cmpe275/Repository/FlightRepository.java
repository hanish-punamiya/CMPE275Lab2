package edu.sjsu.cmpe275.Repository;

import edu.sjsu.cmpe275.Model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlightRepository extends JpaRepository<Flight,Long> {
    @Override
    List<Flight> findAll();
}
