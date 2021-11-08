package edu.sjsu.cmpe275.Controller;

import edu.sjsu.cmpe275.Model.Flight;
import edu.sjsu.cmpe275.Model.Passenger;
import edu.sjsu.cmpe275.Model.Reservation;
import edu.sjsu.cmpe275.Repository.FlightRepository;
import edu.sjsu.cmpe275.Repository.PassengerRepository;
import edu.sjsu.cmpe275.Repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    FlightRepository flightRepository;

    @GetMapping("/")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        try {

            List<Reservation> reservations = new ArrayList<Reservation>();

            List<Long> ids = new ArrayList<Long>();
            ids.add(5L);
            ids.add(2L);

            reservationRepository.findAllById(ids).forEach(reservations::add);
            reservations.sort(Comparator.comparing(Reservation::getReservationNumber));
            Collections.reverse(reservations);

            if (reservations.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(reservations, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{number}")
    public ResponseEntity<Reservation> getReservation(@PathVariable("number") Long id) {
        try {

            Optional<Reservation> reservationData = reservationRepository.findById(id);

            if (reservationData.isPresent()) {
                return new ResponseEntity<>(reservationData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
//            return new ResponseEntity<>(reservations, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/")
    public ResponseEntity<Reservation> makeReservation(@PathVariable("passengerId") Long passengerId, @PathVariable("flightNumbers") List<String> flightNumbers) {

        return null;

        //        try {
//
//            List<Flight> flights = new ArrayList<Flight>();
//            flightRepository.findAll().forEach(flights::add);
//
//            Reservation reservation = new Reservation();
//
//            if(flights.isEmpty()){
//                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//            }
//
//            flights.sort(Comparator.comparing(Flight::getDepartureTime));
//
//            for(int i =0;i<flights.size();i++){
//
//            }
//
//            Optional<Reservation> reservationData = reservationRepository.findById(id);
//
//            if (reservationData.isPresent()) {
//                return new ResponseEntity<>(reservationData.get(), HttpStatus.OK);
//            }
//            else {
//                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//            }
//            return new ResponseEntity<>(reservation, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
        //hanish punamiya
    }

}
