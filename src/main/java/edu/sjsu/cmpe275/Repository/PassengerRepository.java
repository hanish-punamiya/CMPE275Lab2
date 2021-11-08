package edu.sjsu.cmpe275.Repository;

import edu.sjsu.cmpe275.Model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PassengerRepository extends JpaRepository<Passenger,Long> {
//    @Override
    List<Passenger> findAll();


}
