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
    
    
    
//    @PostMapping("/{number}")
//	public ResponseEntity<?> updateReservaton(
//			@PathVariable int number,
//			@RequestParam(value="flightsAdded", required=false) String flightsAdded, 
//			@RequestParam(value="flightsRemoved", required=false) String flightsRemoved){
//    	
//    
//    	
//        List<Flight> flightlist = new ArrayList<Flight>();
//    	//Set<Flight> flightlist = new HashSet<Flight>();
//        List<Flight> flightlisthere = new ArrayList<Flight>();
//        flightlist.clear();
//     
//        Optional<Reservation> reservationData = reservationRepository.findById((long)number); //get reservation details
//        Reservation currentReservation = reservationData.get();
//		List<Flight> currentReservationFlights = currentReservation.getFlights();
//		for(Flight fl : currentReservationFlights) {
//			if(fl!=null) {
//			flightlist.add(fl);
//			}
//				System.out.println("flight list for the reservation" + fl.getFlightNumber()); //flight list for particular reservation
//
//            }
//
//		
//	  	HashMap<String, Object> map = new HashMap<>();
//	   	HashMap<String, Object> mapnew = new HashMap<>();
//		List<Flight> flightAddedObects=null;
//		List<Flight> flightRemovedObects=null;
//
//		if(flightsAdded!=null){
//			//add the flights seperated by comma to the list
//			String[] flightsAddedList=flightsAdded.split(",");
//			List<Long> numberflightsAddedList=new ArrayList<Long>();
//			List<Long> FlightsAddedPresentinFlightTable=new ArrayList<Long>(); // consists of all the fights which are present in the flight table and hence can update the reservation.
//
//			
//			for(String nu : flightsAddedList) {
//				numberflightsAddedList.add(Long.parseLong(nu)); 
//				System.out.println("added flight params" + numberflightsAddedList);
//				}
//			
//			
//
//			System.out.print("numberflightsAddedList" + numberflightsAddedList.size());
//			List<Flight> flights=new ArrayList<Flight>();
//			for(Long s: numberflightsAddedList)
//			{
//				Optional<Flight> flightnew =flightRepository.findById(s);
//				if(flightnew.isEmpty())
//				{
//					    mapnew.clear();
//			   	   	    map.clear();
//			   		    map.put("code", "404");
//			   		    map.put("msg", "Flight number "+s+" does not exist and hence cannot be added");
//					    mapnew.put("Bad Request", map);
//			   			return new ResponseEntity<>(mapnew, HttpStatus.NOT_FOUND);
//				}
//				else
//				{
//						FlightsAddedPresentinFlightTable.add((s));
//				
//				}
//			}
//			
//			 Flight flight;
//			 System.out.print("FlightsAddedPresentinFlightTable" + FlightsAddedPresentinFlightTable);
//			for(Long fligtiter:FlightsAddedPresentinFlightTable)
//			{
//				Optional<Flight> flightData = flightRepository.findById(fligtiter);
//				  flight = flightData.get();
//				  if(flight!=null) {
//					  flightlist.add(flight);
//				  }
//				  
//				  //boolean isoverlapping = false;
//				 
//				 boolean isoverlapping = checkOverlap(new ArrayList<Flight> (flightlist));
//					System.out.print("\n isoverlapping" + isoverlapping);
////				 if(!isoverlapping)
////				 {
////					 currentReservation.getFlights().add(flight);
////				}
//				
//				 //reservationRepository.save(currentReservation);				 
//			}
//			
//			for(Flight flt:flightlist)
//			{
//				//boolean overlap = false;
//				boolean overlap = checkOverlap((flightlist));
//				if(!overlap)
//				{
//					currentReservation.getFlights().add(flt);
//				}
//				else
//				{
//					System.out.print("\n \n \n here false");
//				}
//				
//				reservationRepository.save(currentReservation);
//			}
//			 
//
//
//			 System.out.println(" inside for FlightsAddedPresentinFlightTable : " + flightlist.size());
//
//		}
//		else
//		{
//			return new ResponseEntity<>(mapnew, HttpStatus.NOT_FOUND);
//		}
//		
//		
//		
//		
//	
//		
//
//		
//		return new ResponseEntity<>(currentReservation, HttpStatus.NOT_FOUND);
//	}
//

    
    
    @PostMapping("/{number}")
   	public ResponseEntity<?> updateReservaton(
   			@PathVariable int number,
   			@RequestParam(value="flightsAdded", required=false) String flightsAdded, 
   			@RequestParam(value="flightsRemoved", required=false) String flightsRemoved){
       	 try {
       	  // List<Flight> flightlist = new ArrayList<Flight>();
              Set<Flight> flightlist = new HashSet<Flight>();
              Set<Flight> flights_new =new HashSet<Flight>();
              Set<Flight> flights_removed =new HashSet<Flight>();
			   Optional<Reservation> reservationData = reservationRepository.findById((long) number); // get																					// details
				Reservation currentReservation = reservationData.get();
				List<Flight> currentReservationFlights = currentReservation.getFlights();
				for (Flight fl : currentReservationFlights) {
					if (fl != null) {
						flightlist.add(fl);
					}
					System.out.println("flight list for the reservation" + fl.getFlightNumber()); // flight list for
				}
				
				List<Flight> flightAddedObects=null;
				List<Flight> flightRemovedObects=null;
		
				if(flightsAdded!=null){
					//add the flights seperated by comma to the list
					String[] flightsAddedList=flightsAdded.split(",");
					List<Long> numberflightsAddedList=new ArrayList<Long>();
					
					for(String nu : flightsAddedList) {
						numberflightsAddedList.add(Long.parseLong(nu)); 
						System.out.println("added flight params" + numberflightsAddedList);
						}
					
					
		
					System.out.print("numberflightsAddedList" + numberflightsAddedList.size());
					
					for(Long s: numberflightsAddedList)
					{
						Optional<Flight> flightnew =flightRepository.findById(s);
						if(!flightnew.isEmpty())
						{
							flights_new.add(flightnew.get());
						}
							    
					}
				}
				
				Set<Flight> nonOverlap_Flight = new HashSet<Flight>();
					
					for(Flight flight_new : flights_new) {
						boolean isOverLap = false;
						for(Flight flight: flightlist) {
							if(flight.getFlightNumber() == flight_new.getFlightNumber() ) {
								System.out.println("Already contain same flight" + flight.getFlightNumber() );
								isOverLap = true;
								return createBadRequest("Already contain same flight " + flight.getFlightNumber() );
							}
							if(flight.getDepartureTime().after(flight_new.getDepartureTime()) && flight.getArrivalTime().before(flight_new.getDepartureTime())) {
								System.out.println("Time conflict happens with flight: " + flight.getFlightNumber());
								isOverLap = true;
								return createBadRequest("Time conflict happens with flight: " + flight.getFlightNumber() + " for flight number: " + flight_new.getFlightNumber() );
							}
							
						}
						if(!isOverLap) {
							currentReservation.getFlights().add(flight_new);
							nonOverlap_Flight.add(flight_new);
						}
					}
					
					
					if(flightsRemoved!=null){
						//add the flights seperated by comma to the list
						String[] flightsRemovedList=flightsRemoved.split(",");
						List<Long> numberflightsRemovedList=new ArrayList<Long>();
						
						for(String nu : flightsRemovedList) {
							numberflightsRemovedList.add(Long.parseLong(nu)); 
							System.out.println("added flight params" + numberflightsRemovedList);
							}
						
						
			
						System.out.print("numberflightsAddedList" + numberflightsRemovedList.size());
						
						for(Long s: numberflightsRemovedList)
						{
							Optional<Flight> flightnew =flightRepository.findById(s);
							if(!flightnew.isEmpty())
							{
								flights_removed.add(flightnew.get());
							}
								    
						}
					}
					
					for(Flight flights_remove : flights_removed) {
						if(!nonOverlap_Flight.contains(flights_remove)) // to handle non-overlap scenario
						{
							currentReservation.getFlights().removeIf(flight -> flight.getFlightNumber() == flights_remove.getFlightNumber());
						}
						else
						{
							return createBadRequest("Trying to remove flight" + flights_remove.getFlightNumber() +" which was just added" );
						}
						
					} 
					updateReservation(currentReservation);
					reservationRepository.save(currentReservation);
					return new ResponseEntity<>(currentReservation, HttpStatus.OK);
					
					
				
				
       	 } catch (Exception e) {
       		 
       	 }
       	 
     	return createBadRequest("Unknown Error");
       	
       	
   	}



    void updateReservation(Reservation reservation) {
    	// update price 
    	List<Flight> flights = reservation.getFlights();
    	int price = 0;
    	for(Flight flight : flights) {
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
    
    ResponseEntity<?> createBadRequest(String msg)
    {
    	HashMap<String, Object> map = new HashMap<>();
	   	HashMap<String, Object> mapnew = new HashMap<>();
       	mapnew.clear();
	   	map.clear();
		map.put("code", "404");
		map.put("msg", msg);
	    mapnew.put("Bad Request", map);
	    return new ResponseEntity<>(mapnew, HttpStatus.NOT_FOUND);
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

    
    
//    public static boolean checkOverlappingReservationforFlights(Set<Flight> flights) {
//        try {
//            Date arrivalTime = new Date();
//            arrivalTime.setTime(0);
//            Date departureTime = new Date();
//            flights.sort(Comparator.comparing(Flight::getDepartureTime));
//            for (Flight flight :
//                    flights) {
//                departureTime = flight.getDepartureTime();
//
//                if (arrivalTime.after(departureTime))
//                    return false;
//
//                arrivalTime = flight.getArrivalTime();
//            }
//        } catch (Exception exception) {
//            return false;
//        }
//        return true;
//    }
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


