package edu.sjsu.cmpe275.Controller;

import edu.sjsu.cmpe275.Model.Flight;
import edu.sjsu.cmpe275.Model.Passenger;
import edu.sjsu.cmpe275.Model.Reservation;
import edu.sjsu.cmpe275.Repository.FlightRepository;
import edu.sjsu.cmpe275.Repository.PassengerRepository;
import edu.sjsu.cmpe275.Repository.ReservationRepository;
import net.minidev.json.JSONObject;
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

    @Autowired
    PassengerRepository passengerRepository;

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

    @PostMapping("")
    public ResponseEntity<Reservation> makeReservation(@RequestParam("passengerId") Long passengerId, @RequestParam("flightNumbers") List<Long> flightNumbers) {

        try {

            List<Flight> flights = new ArrayList<Flight>();
            flightRepository.findAllById(flightNumbers).forEach(flights::add);

            if (!checkSeatsLeft(flights)) {
//                return error
                return null;
            }

            if (!checkOverlap(flights)) {
                //return error
                return null;
            }
            Optional<Passenger> PassengerData = passengerRepository.findById(passengerId);
            Passenger passenger = PassengerData.get();
            if (PassengerData.isEmpty()) {
                //return error
                return null;
            }

            if (!checkReservationsOverlap(passenger, flights)) {
//                return error
                return null;
            }

            Reservation reservation = createReservation(passenger, flights);


            for (Flight flight :
                    flights) {
                passenger.getFlights().add(flight);
                flight.setSeatsLeft(flight.getSeatsLeft() - 1);
                flightRepository.save(flight);
            }

            passengerRepository.save(passenger);
            return new ResponseEntity<>(reservationRepository.save(reservation), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static boolean checkOverlap(List<Flight> flights) {
        try {
            Date arrivalTime = new Date();
            arrivalTime.setTime(0);
            Date departureTime = new Date();
            flights.sort(Comparator.comparing(Flight::getDepartureTime));
            for (Flight flight :
                    flights) {
                departureTime = flight.getDepartureTime();

                if (arrivalTime.after(departureTime))
                    return false;

                arrivalTime = flight.getArrivalTime();
            }
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    public static boolean checkSeatsLeft(List<Flight> flights) {
        try {
            for (Flight flight :
                    flights) {
                if (flight.getSeatsLeft() <= 0)
                    return false;
            }
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    public boolean checkReservationsOverlap(Passenger passenger, List<Flight> flights) {
        try {
//            Optional<Passenger> PassengerData = passengerRepository.findById(passengerId);
            List<Long> flightIds = new ArrayList<>();
            passenger.getFlights().forEach(flight -> flightIds.add(flight.getFlightNumber()));
            List<Flight> newList = new ArrayList<>();
            flights.forEach(newList::add);
            flightRepository.findAllById(flightIds).forEach(newList::add);
            if (!checkOverlap(newList))
                return false;
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    private Reservation createReservation(Passenger passenger, List<Flight> flights) {
        try {
            Reservation reservation = new Reservation();

            reservation.setPrice(0);
            flights.sort(Comparator.comparing(Flight::getDepartureTime));
            for (Flight flight :
                    flights) {
                reservation.setPrice(reservation.getPrice() + flight.getPrice());
            }
            reservation.setPassenger(passenger);
            reservation.setFlights(flights);
            reservation.setOrigin(flights.get(0).getOrigin());
            reservation.setDestination(flights.get(flights.size() - 1).getDestination());
            return reservation;
        } catch (Exception exception) {
            return null;
        }
    }

}


