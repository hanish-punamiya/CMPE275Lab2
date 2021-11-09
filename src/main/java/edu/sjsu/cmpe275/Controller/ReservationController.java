package edu.sjsu.cmpe275.Controller;

import edu.sjsu.cmpe275.Helper.Error.Response;
import edu.sjsu.cmpe275.Model.Flight;
import edu.sjsu.cmpe275.Model.Passenger;
import edu.sjsu.cmpe275.Model.Reservation;
import edu.sjsu.cmpe275.Repository.FlightRepository;
import edu.sjsu.cmpe275.Repository.PassengerRepository;
import edu.sjsu.cmpe275.Repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import com.fasterxml.jackson.xml.XmlMapper;


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
    public ResponseEntity<Object> getAllReservations() {
        try {

            return new ResponseEntity<Object>(new edu.sjsu.cmpe275.Helper.Success.Response("404", "Hanish"), HttpStatus.OK);

//            List<Reservation> reservations = new ArrayList<Reservation>();
//
//            List<Long> ids = new ArrayList<Long>();
//            ids.add(5L);
//            ids.add(2L);
//
//            reservationRepository.findAllById(ids).forEach(reservations::add);
//            reservations.sort(Comparator.comparing(Reservation::getReservationNumber));
//            Collections.reverse(reservations);
//
//            if (reservations.isEmpty()) {
//                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//            }
//            return new ResponseEntity<Object>(reservations, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{number}")
    public ResponseEntity<Object> getReservation(@PathVariable("number") Long id) {
        try {
            Optional<Reservation> reservationData = reservationRepository.findById(id);
            if (reservationData.isPresent())
                return new ResponseEntity<>(reservationData.get(), HttpStatus.OK);
            else
                return new ResponseEntity<Object>(new Response("404", "Passenger not found"), HttpStatus.NOT_FOUND);
        } catch (Exception exception) {
            return new ResponseEntity<Object>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{number}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long number) throws Exception {
        //System.out.print("inside delete");
        HashMap<String, Object> map = new HashMap<>();
        HashMap<String, Object> mapnew = new HashMap<>();
        Optional<Reservation> reservation =
                reservationRepository
                        .findById(number);
        if (!reservation.isPresent()) {
            mapnew.clear();
            map.clear();
            map.put("code", "404");
            map.put("msg", "reservation with number " + number + " does not exist");
            mapnew.put("Bad Request", map);
            return new ResponseEntity<>(mapnew, HttpStatus.NOT_FOUND);

        } else {
            Optional<Reservation> reservationData = reservationRepository.findById(number);
            if (reservationData.isPresent()) {
                Reservation currentReservation = reservationData.get();
                List<Flight> currentReservationFlights = currentReservation.getFlights();
                for (Flight fl : currentReservationFlights) {
                    fl.setSeatsLeft(fl.getSeatsLeft() + 1);

                }
            }
            reservationRepository.deleteById(number);

            return new ResponseEntity<>(reservation, HttpStatus.OK);
            //return new ResponseEntity<>((generateErrorMessage("Response", "200", "Passenger with id " + number + " is deleted successfully")),HttpStatus.OK);
        }


    }

    @PostMapping()
    public ResponseEntity<Object> makeReservation(@RequestParam("passengerId") Long passengerId, @RequestParam("flightNumbers") List<Long> flightNumbers) {
        try {
            List<Flight> flights = new ArrayList<Flight>();
            flightRepository.findAllById(flightNumbers).forEach(flights::add);
            if (!checkSeatsLeft(flights))
                return new ResponseEntity<Object>(new Response("400", "No seats left on the flight"), HttpStatus.BAD_REQUEST);
            if (!checkOverlap(flights))
                return new ResponseEntity<Object>(new Response("400", "The flights overlap with each other"), HttpStatus.BAD_REQUEST);
            Optional<Passenger> PassengerData = passengerRepository.findById(passengerId);
            Passenger passenger = PassengerData.get();
            if (PassengerData.isEmpty())
                return new ResponseEntity<Object>(new Response("404", "The passenger does not exist"), HttpStatus.NOT_FOUND);
            if (!checkReservationsOverlap(passenger, flights))
                return new ResponseEntity<Object>(new Response("400", "The flights overlap with other reservations"), HttpStatus.BAD_REQUEST);
            Reservation reservation = createReservation(passenger, flights);
            if(reservation==null)
                return new ResponseEntity<Object>(new Response("500", "Some error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
            for (Flight flight :
                    flights) {
                passenger.getFlights().add(flight);
                flight.setSeatsLeft(flight.getSeatsLeft() - 1);
                flightRepository.save(flight);
            }
            passengerRepository.save(passenger);
            return new ResponseEntity<Object>(reservationRepository.save(reservation), HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<Object>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //checks for the flights passed in the parameter
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

    //checks if there are seats left on the flight
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

    //checks for all the flights the passenger is on along with the flights passed in the parameter
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


