package edu.sjsu.cmpe275.Controller;


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


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/flight")
public class FlightController {

    @Autowired
    FlightRepository flightRepository;

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    PlaneRepository planeRepository;
    
    
    
    @GetMapping(value = "/{flightNumber}", produces = {"application/json", "application/xml"})
    public ResponseEntity<?> getFlight(@PathVariable Long flightNumber) {
    	HashMap<String, Object> map = new HashMap<>();
       	HashMap<String, Object> mapnew = new HashMap<>();
    	
    	Optional<Flight> flight = flightRepository.findById(flightNumber);
    	if(flight.isEmpty())
    	{
    		mapnew.clear();
   	   	    map.clear();
   		    map.put("code", "404");
   		    map.put("msg", "Sorry, the requested flight with number "+flightNumber+" does not exist");
		    mapnew.put("Bad Request", map);
   			return new ResponseEntity<>(mapnew, HttpStatus.NOT_FOUND);
    		// not found
    	}
    	else
    	{
    		return new ResponseEntity<>(flight, HttpStatus.OK);
    	}
    	
    }

    @PostMapping(value="/{flightNumber}")
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
                List<Flight> flight_list = new ArrayList<>();
                flight_list.add(currFlight);
                for(Passenger p : currFlight.getPassengers()) {
                    if(!checkReservationsOverlap(p, flight_list)) {
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
                return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }        
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

    //checks for the flights passed in the parameter
    public static boolean checkOverlap(List<Flight> flights) {
        try {
            Date arrivalTime = new Date();
            arrivalTime.setTime(0);
            Date departureTime = new Date();
            flights.sort(Comparator.comparing(Flight::getDepartureTime));
            for (Flight flight : flights) {
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

    @DeleteMapping("/{flightnumber}")
    public ResponseEntity<Object> deleteFlight(@PathVariable("flightnumber") long flightNumber) {
        Optional<Flight> FlightData = flightRepository.findById(flightNumber);

        if(FlightData.isPresent()) {
            if(FlightData.get().getPassengers().size() == 0) {
                try{
                    flightRepository.deleteById(flightNumber);
                    return new ResponseEntity<Object>(new edu.sjsu.cmpe275.Helper.Success.Response("200", "Flight with number "+flightNumber+" is deleted successfully."), HttpStatus.OK);           
                } catch (Exception e) {
                    return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<Object>(new Response("404", "This flight has a passenger on it."), HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<Object>(new Response("404", "This flight number doesn't exist."), HttpStatus.NOT_FOUND);
        }
    }

    public Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd hh").parse(date);
        } catch (Exception e) {
            return new Date();
        }
     }

}
