package edu.sjsu.cmpe275.Controller;


import edu.sjsu.cmpe275.Model.Flight;
import edu.sjsu.cmpe275.Model.Passenger;
import edu.sjsu.cmpe275.Model.Plane;
import edu.sjsu.cmpe275.Repository.FlightRepository;
import edu.sjsu.cmpe275.Repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/flight")
public class FlightController {

    @Autowired
    FlightRepository flightRepository;

    @Autowired
    PassengerRepository passengerRepository;

    @GetMapping("/flights")
    public ResponseEntity<List<Flight>> getAllPlanes() {
        try {
            List<Flight> flights = new ArrayList<Flight>();

            Passenger passenger = new Passenger();
            Optional<Passenger> PassengerData = passengerRepository.findById(1L);
            passenger = PassengerData.get();

            flightRepository.findAll().forEach(flights::add);
            flights.sort(Comparator.comparing(Flight::getDepartureTime));

//            flights.get(1).getPassengers().add(passenger);
//            flights.get(1).setOrigin("MAA");
//            flightRepository.save(flights.get(1));
            passenger.getFlights().add(flights.get(1));
            passengerRepository.save(passenger);

            if (flights.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(flights, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
