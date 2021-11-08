package edu.sjsu.cmpe275.Controller;


import edu.sjsu.cmpe275.Model.Flight;
import edu.sjsu.cmpe275.Model.Passenger;
import edu.sjsu.cmpe275.Model.Plane;
import edu.sjsu.cmpe275.Repository.FlightRepository;
import edu.sjsu.cmpe275.Repository.PassengerRepository;
import edu.sjsu.cmpe275.Repository.PlaneRepository;

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
    
    
    
    @GetMapping("/{flightNumber}")
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
    public ResponseEntity<Flight> createUpdateFlight(
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
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } 

            if(currFlight.getPassengers().size() > 0) {
                for(Passenger p : currFlight.getPassengers()) {

                }
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
                return new ResponseEntity<>(createdFlight, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }        
    }

    @DeleteMapping("/{flightnumber}")
    public ResponseEntity<String> deleteFlight(@PathVariable("flightnumber") long flightNumber) {
        Optional<Flight> FlightData = flightRepository.findById(flightNumber);

        if(FlightData.isPresent() && FlightData.get().getPassengers().size() == 0) {
            try{
                flightRepository.deleteById(flightNumber);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
