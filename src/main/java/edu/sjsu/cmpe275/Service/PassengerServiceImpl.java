package edu.sjsu.cmpe275.Service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

@Service
public class PassengerServiceImpl implements PassengerService {
    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    FlightRepository flightRepository;

    @Override
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
//            return passenger;
        }
        return null;
    }

    @Override
    public Optional<Passenger> getPassengerService(long id) {
        return passengerRepository.findById(id);
    }

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
        //delete all the reservations made by the passenger
//        deleteReservationsOfPassengerService(id);
        //delete the passenger
        passengerRepository.deleteById(id);
        if(true) throw new RuntimeException("e");
        return true;

    }

    @Override
    public void deleteReservationsOfPassengerService(long id) {
        reservationRepository.deleteReservationsByPassengerId(id) ;
    }
}
