package edu.sjsu.cmpe275.Repository;

import edu.sjsu.cmpe275.Model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PassengerRepository extends JpaRepository<Passenger,Long> {
//    @Override
    List<Passenger> findAll();

    @Query(value = "select p from Passenger p where p.phone=:phone")
    public Passenger findByPhone(String phone);

}
