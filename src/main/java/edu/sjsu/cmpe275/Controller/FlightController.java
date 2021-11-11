package edu.sjsu.cmpe275.Controller;


import edu.sjsu.cmpe275.Helper.Error.Response;
import edu.sjsu.cmpe275.Model.Flight;
import edu.sjsu.cmpe275.Model.Passenger;
import edu.sjsu.cmpe275.Model.Plane;
import edu.sjsu.cmpe275.Repository.FlightRepository;
import edu.sjsu.cmpe275.Repository.PassengerRepository;
import edu.sjsu.cmpe275.Repository.PlaneRepository;
import edu.sjsu.cmpe275.Helper.Error.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.*;



import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
public class FlightController {

    @Autowired
    FlightRepository flightRepository;

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    PlaneRepository planeRepository;
    
    /**  
     * This method is used to get details of a flight. 
     * 
     * @return ResponseEntity<?> This returns a response entity with the flight details of a particular flight or an error message if flight is not found.
     */
    
    @GetMapping(value = "/flight/{flightNumber}", produces = {"application/json", "application/xml"})
    public ResponseEntity<?> getFlight(@PathVariable Long flightNumber) {
    
    	
    	Optional<Flight> flight = flightRepository.findById(flightNumber);
    	if(flight.isEmpty())
    	{
   			return new ResponseEntity<>(new Response("404","Sorry, the requested flight with number "+flightNumber+" does not exist"), HttpStatus.NOT_FOUND);
    		// not found
    	}
    	else
    	{
            flight.get().setReservations(null);
    		return new ResponseEntity<>(flight, HttpStatus.OK);
    	}
    	
    }

    /**  
     * This method is used to create or update a flight. 
     * @param flightNumber is the flight id to be created or updated(if exists already) 
     * @param price The price of the flight
     * @param origin The origin of the flight
     * @param destination The destination of the flight
     * @param departureTime The string form of the departure time.  Convert to Date. 
     * @param arrivalTime The string form of the arrival time.  Convert to Date. 
     * @param description String description of the flight.
     * @param capacity Total capacity of the plane.
     * @param model Model of the plane.
     * @param manufacturer Manufacturer company that made the plane.
     * @param yearOfManufacture Year the plane was built.
     * @return ResponseEntity<Object> This returns a response entity with either the Flight created/updated or an error message.
     */
    @PostMapping(value="/flight/{flightNumber}")
    @Transactional(rollbackFor = {Exception.class})
    public ResponseEntity<Object> createUpdateFlight(
        @PathVariable("flightNumber") long flightNumber,
        @RequestParam int price,
        @RequestParam String origin,
        @RequestParam String destination,
        @RequestParam String departureTime,
        @RequestParam String arrivalTime,
        @RequestParam String description,
        @RequestParam int capacity,
        @RequestParam String model,
        @RequestParam String manufacturer,
        @RequestParam int yearOfManufacture
    ){
        Optional<Flight> FlightData = flightRepository.findById(flightNumber);
        if(FlightData.isPresent()) {
            Flight currFlight = FlightData.get();
            if(capacity < currFlight.getPlane().getCapacity() - currFlight.getSeatsLeft()) {
                return new ResponseEntity<Object>(new Response("400", "Reservation count is higher than target capacity."), HttpStatus.BAD_REQUEST);
            } 

            if(currFlight.getPassengers().size() > 0) {
                for(Passenger p : currFlight.getPassengers()) {
                    if(checkOverlapFlights(p, parseDate(departureTime), parseDate(arrivalTime), flightNumber)) {
                        return new ResponseEntity<Object>(new Response("400", "A passenger has an overlapping flight with this updated flight."), HttpStatus.BAD_REQUEST);
                    }
                }
            }

            currFlight.setPrice(price);
            currFlight.setOrigin(origin);
            currFlight.setDestination(destination);
            currFlight.setDepartureTime(parseDate(departureTime));
            currFlight.setArrivalTime(parseDate(arrivalTime));
            currFlight.setDescription(description);

            currFlight.setSeatsLeft(capacity - (currFlight.getPlane().getCapacity() - currFlight.getSeatsLeft()) );
            currFlight.getPlane().setCapacity(capacity);
            planeRepository.save(currFlight.getPlane());
            return new ResponseEntity<>(flightRepository.save(currFlight), HttpStatus.OK);
        } else {
            try {
                Plane createdPlane = planeRepository.save(new Plane(model, capacity, manufacturer, yearOfManufacture));
                Flight createdFlight = flightRepository.save(new Flight(
                    flightNumber, price, origin, destination,
                    parseDate(departureTime), parseDate(arrivalTime), capacity, description, 
                    createdPlane, new ArrayList<>(), new ArrayList<>()
                ));
                return new ResponseEntity<Object>(createdFlight, HttpStatus.OK);
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }        
    }

    /**  
     * This method is used to check for overlap between a passenger's reservations and a flight
     * @param passenger is the passenger to be checked
     * @param flight are the flights to check overlap with
     * @return boolean true=there is no overlap  false=there is overlap
     */
    public boolean checkOverlapFlights(Passenger p, Date newDeparture, Date newArrival, long flightNumber) {
        List<Long> flightIds = new ArrayList<>();
        p.getFlights().forEach(flight -> flightIds.add(flight.getFlightNumber()));
        List<Flight> flightList = new ArrayList<>();
        flightRepository.findAllById(flightIds).forEach(flightList::add);

        for(Flight pFlight : flightList) {
            if(newDeparture.before(pFlight.getArrivalTime()) && newArrival.after(pFlight.getDepartureTime()) && pFlight.getFlightNumber()!=flightNumber) {
                return true;
            }
        }
        return false;
    }

    /**  
     * This method is used to check for overlap between a passenger's reservations and a list of flights
     * @param passenger is the passenger to be checked
     * @param flights are the flights to check overlap with
     * @return boolean true=there is no overlap  false=there is overlap
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
     * This method is used check overlap between a list of sorted flights
     * @param flights are the flights to check overlap with
     * @return boolean true=there is no overlap  false=there is overlap
     */
    public static boolean checkOverlap(List<Flight> flights) {
        try {
            Date arrivalTime = new Date();
            arrivalTime.setTime(0);
            Date departureTime = new Date();
            flights.sort(Comparator.comparing(Flight::getDepartureTime));
            long prevId = -1;
            for (Flight flight : flights) {
                departureTime = flight.getDepartureTime();

                if (arrivalTime.after(departureTime) && flight.getFlightNumber()!=prevId)
                    return false;

                arrivalTime = flight.getArrivalTime();
                prevId = flight.getFlightNumber();
            }
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    /**  
     * This method is used to delete a flight. 
     * @param flightNumber is the flight id to be deleted
     * @return ResponseEntity<Object> This returns a response entity with a success message or an error message.
     */
    @DeleteMapping("/airline/{flightnumber}")
    @Transactional(rollbackFor = {Exception.class})
    public ResponseEntity<Object> deleteFlight(@PathVariable("flightnumber") long flightNumber) {
        Optional<Flight> FlightData = flightRepository.findById(flightNumber);

        if(FlightData.isPresent()) {
            if(FlightData.get().getPassengers().size() == 0) {
                try{
                    flightRepository.deleteById(flightNumber);
                    return new ResponseEntity<Object>(new edu.sjsu.cmpe275.Helper.Success.Response("200", "Flight with number "+flightNumber+" is deleted successfully."), HttpStatus.OK);           
                } catch (Exception e) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<Object>(new Response("404", "This flight has a passenger on it."), HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<Object>(new Response("404", "This flight number doesn't exist."), HttpStatus.NOT_FOUND);
        }
    }

    /**  
     * This method is used to convert a String date to a Date object. 
     * @param date is the String date to be converted
     * @return Date object parsed from string
     */
    public Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(date);
        } catch (Exception e) {
            return new Date();
        }
     }

}
