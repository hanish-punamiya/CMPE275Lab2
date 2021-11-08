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
	
    @DeleteMapping("/{number}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long number) throws Exception {
   	 //System.out.print("inside delete");
   	HashMap<String, Object> map = new HashMap<>();
   	HashMap<String, Object> mapnew = new HashMap<>();
   	  Optional<Reservation> reservation =
   			   reservationRepository
   	           .findById(number);  		
   		if(!reservation.isPresent())
   		{   			
   	   	    mapnew.clear();
   	   	    map.clear();
   		    map.put("code", "404");
   		    map.put("msg", "reservation with number "+number+" does not exist");
		    mapnew.put("Bad Request", map);
   			return new ResponseEntity<>(mapnew, HttpStatus.NOT_FOUND);

   		}
   		else
   		{   		
   			Optional<Reservation> reservationData = reservationRepository.findById(number);
   			if(reservationData.isPresent())
   			{
   				Reservation currentReservation = reservationData.get();
   				List<Flight> currentReservationFlights = currentReservation.getFlights();
   				for(Flight fl : currentReservationFlights) {
   					fl.setSeatsLeft(fl.getSeatsLeft()+1);

	                }
   			}
   			reservationRepository.deleteById(number);

   			return new ResponseEntity<>(reservation, HttpStatus.OK);
   			//return new ResponseEntity<>((generateErrorMessage("Response", "200", "Passenger with id " + number + " is deleted successfully")),HttpStatus.OK);
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
