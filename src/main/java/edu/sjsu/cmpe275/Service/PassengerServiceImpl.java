package edu.sjsu.cmpe275.Service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.sjsu.cmpe275.Model.Flight;
import edu.sjsu.cmpe275.Model.Passenger;
import edu.sjsu.cmpe275.Repository.FlightRepository;
import edu.sjsu.cmpe275.Repository.PassengerRepository;
import edu.sjsu.cmpe275.Repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * this class implements all the functions of passenger service.
 */
@Service
public class PassengerServiceImpl implements PassengerService {
    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    FlightRepository flightRepository;


    /**
     * if there is passenger with same phone number, return error else create new passenger and return new passenger
     * @param fn - first name
     * @param ln - last name
     * @param age - age
     * @param gen - gender
     * @param ph - phone
     * @return Passenger object
     */
    @Override
    @Transactional
    public Passenger createPassengerService(String fn, String ln, int age, String gen, String ph) {
        Passenger passenger = new Passenger();
        System.out.println("passenger by phone = " +  passengerRepository.findByPhone(ph));
        if(passengerRepository.findByPhone(ph)==null) {
            passenger.setFirstName(fn);
            passenger.setLastName(ln);
            passenger.setAge(age);
            passenger.setGender(gen);
            passenger.setPhone(ph);
            return passengerRepository.save(passenger);
        }
        return null;
    }

    /**
     *
     * @param id - passenger id
     * @return Passenger object
     */
    @Override
    public Optional<Passenger> getPassengerService(long id) {
        return  passengerRepository.findById(id);
    }

    /**
     * get the passenger, check if passenger has any flights, if yes delete all flights and delete passenger at the end.
     * @param id - passenger id
     * @return true if the passenger exits, else false
     */
    @Override
    @Transactional
    public boolean deletePassengerService(long id) {
        //check and delete reservations of passenger
        Passenger passenger = passengerRepository.getById(id);
        System.out.println("passenger  = " + passenger.getFirstName());
        //get all flight ids of the passenger
        List<Flight> flights_of_passenger = new ArrayList<>();
        flights_of_passenger = passenger.getFlights() ;
        for(Flight flight : flights_of_passenger){
            System.out.println("number of seats = " + flight.getSeatsLeft());
            flight.setSeatsLeft(flight.getSeatsLeft()+1);
            flightRepository.save(flight);
        }
        System.out.println("flight ids = " + flights_of_passenger);
        passenger.setFlights(new ArrayList<>());
        passengerRepository.save(passenger);
        passengerRepository.deleteById(id);
        return true;

    }

}
