package edu.sjsu.cmpe275.Service;

import edu.sjsu.cmpe275.Model.Passenger;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface PassengerService {
    public boolean createPassengerService(String fn,String ln,int age,String gen,String ph);

    public Optional<Passenger> getPassengerService(long id);
    public boolean deletePassengerService(long id);
    public void deleteReservationsOfPassengerService(long id);
}
