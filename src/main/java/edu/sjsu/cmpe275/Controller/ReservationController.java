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


    /**
     * This method sends the reservation as response in its full form.
     *
     * @param id Id of the reservation to be retrieved
     * @return The retrieved reservation in its full form
     */
    @GetMapping("/{number}")
    public ResponseEntity<Object> getReservation(@PathVariable("number") Long id) {
        try {
            Optional<Reservation> reservationData = reservationRepository.findById(id);
            if (reservationData.isPresent())
                return new ResponseEntity<>(reservationData.get(), HttpStatus.OK);
            else
                return new ResponseEntity<Object>(new Response("404", "Reservation with number " + id + " not found"), HttpStatus.NOT_FOUND);
        } catch (Exception exception) {
            return new ResponseEntity<Object>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * This method deletes a reservation which exists.
     *
     * 
     * @return Success message when a reservation is cancelled, error message otherwise.
     */
    
    @DeleteMapping("/{number}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long number) throws Exception {
        Optional<Reservation> reservation =
                reservationRepository
                        .findById(number);
        if (!reservation.isPresent()) {
            return new ResponseEntity<Object>(new Response("404", "Reservation with number " + number + " does not exist"), HttpStatus.NOT_FOUND);

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
        }


    }

    /**
     * This method returns the successfully booked reservation as a response. It checks for the overlap of given flights as well as other flight reservations.
     *
     * @param passengerId Id of the passenger for whom the reservation needs to be made
     * @param flightNumbers List of flights in the reservation
     * @return The newly created reservation.
     */
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
            if (reservation == null)
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
    
    /**
     * This method updates the reservation by adding and removing flights. It also recalculates attributes such as price, origin and destination.
     *
     * @param flightsAdded List of the flights to be added
     * @param flightsRemoved List of the flights to be removed
     * @return The updated reservation in its full form
     */

    @PutMapping("/{number}")
    public ResponseEntity<?> updateReservaton(
            @PathVariable int number,
            @RequestParam(value = "flightsAdded", required = false) String flightsAdded,
            @RequestParam(value = "flightsRemoved", required = false) String flightsRemoved) {
        try {
            Set<Flight> flightlist = new HashSet<Flight>();
            Set<Flight> flights_new = new HashSet<Flight>();
            Set<Flight> flights_removed = new HashSet<Flight>();
            Optional<Reservation> reservationData = reservationRepository.findById((long) number); // get																					// details
            Reservation currentReservation = reservationData.get();
            List<Flight> currentReservationFlights = currentReservation.getFlights();
            for (Flight fl : currentReservationFlights) {
                if (fl != null) {
                    flightlist.add(fl);
                }
                //System.out.println("flight list for the reservation" + fl.getFlightNumber()); // flight list for
            }

            List<Flight> flightAddedObects = null;
            List<Flight> flightRemovedObects = null;

            if (flightsAdded != null) {
                //add the flights seperated by comma to the list
                String[] flightsAddedList = flightsAdded.split(",");
                List<Long> numberflightsAddedList = new ArrayList<Long>();

                for (String nu : flightsAddedList) {
                    numberflightsAddedList.add(Long.parseLong(nu));
                    System.out.println("added flight params" + numberflightsAddedList);
                }

                for (Long s : numberflightsAddedList) {
                    Optional<Flight> flightnew = flightRepository.findById(s);
                    if (!flightnew.isEmpty()) {
                        flights_new.add(flightnew.get());
                    }

                }
            }

            Set<Flight> nonOverlap_Flight = new HashSet<Flight>();

            for (Flight flight_new : flights_new) {
                boolean isOverLap = false;
                for (Flight flight : flightlist) {
                    if (flight.getFlightNumber() == flight_new.getFlightNumber()) {
                        isOverLap = true;
                        return new ResponseEntity<Object>(new Response("404", "Already contains the same flight " + flight.getFlightNumber()), HttpStatus.NOT_FOUND);
                    }
                    if (flight.getDepartureTime().after(flight_new.getDepartureTime()) && flight.getArrivalTime().before(flight_new.getDepartureTime())) {
                        isOverLap = true;
                        return new ResponseEntity<Object>(new Response("404", "Time Conflicts with flight" + flight.getFlightNumber() + " for flight number: " + flight_new.getFlightNumber()), HttpStatus.NOT_FOUND);
                    }

                }
                if (!isOverLap) {
                    currentReservation.getFlights().add(flight_new);
                    nonOverlap_Flight.add(flight_new);
                }
            }

            if (flightsRemoved != null) {
                //add the flights seperated by comma to the list
                String[] flightsRemovedList = flightsRemoved.split(",");
                List<Long> numberflightsRemovedList = new ArrayList<Long>();

                for (String nu : flightsRemovedList) {
                    numberflightsRemovedList.add(Long.parseLong(nu));
                    System.out.println("added flight params" + numberflightsRemovedList);
                }

                for (Long s : numberflightsRemovedList) {
                    Optional<Flight> flightnew = flightRepository.findById(s);
                    if (!flightnew.isEmpty()) {
                        flights_removed.add(flightnew.get());
                    }

                }
            }

            for (Flight flights_remove : flights_removed) {
                if (!nonOverlap_Flight.contains(flights_remove)) // to handle non-overlap scenario
                {
                    currentReservation.getFlights().removeIf(flight -> flight.getFlightNumber() == flights_remove.getFlightNumber());
                } else {
                    return new ResponseEntity<Object>(new Response("404", "Trying to remove flight" + flights_remove.getFlightNumber() + " which was just added"), HttpStatus.NOT_FOUND);
                }

            }
            updateReservation(currentReservation);
            reservationRepository.save(currentReservation);
            return new ResponseEntity<>(currentReservation, HttpStatus.OK);
        } catch (Exception e) {

        }

        return new ResponseEntity<Object>(new Response("404", "Unknown Error"), HttpStatus.NOT_FOUND);


    }

    /**
     * This method updates the reservation price, origin and destination.
     *@param current Reservation object
     */

    void updateReservation(Reservation reservation) {
        // update price
        List<Flight> flights = reservation.getFlights();
        int price = 0;
        for (Flight flight : flights) {
            price += flight.getPrice();
        }
        reservation.setPrice(price);
        // update origin and destination

        // sort by departure date
        flights.sort(Comparator.comparing(Flight::getDepartureTime));
        String origin = flights.get(0).getOrigin();
        String destination = flights.get(flights.size() - 1).getDestination();

        reservation.setOrigin(origin);
        reservation.setDestination(destination);

    }


    /**
     * Checks for the overlap of the flights within the given list
     * @param flights to be checked if they overlap
     * @return true if flights do not overlap
     */
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

    /**
     * Checks if there are any seats left on the flight
     * @param flights to be checked if they have seats left on them
     * @return true if seats are left on the flight
     */
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

    /**
     * Checks for all the flights the passenger is on along with the flights passed in the parameter
     * @param passenger who's other flights need to be checked for overlap
     * @param flights to be checked for overlap along with other flights the passenger is on
     * @return true if the flights do not overlap
     */
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

    /**
     * Creates a reservation object with all the details of the passenger and the flights
     * @param passenger details to be used to create a reservation
     * @param flights to be added to the reservation
     * @return newly created reservation object
     */
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

