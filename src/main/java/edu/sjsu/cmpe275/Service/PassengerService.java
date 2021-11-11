package edu.sjsu.cmpe275.Service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.sjsu.cmpe275.Model.Passenger;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * passenger service to create, delete and get passengers
 */
@Service
public interface PassengerService {
    /**
     *
     * @param fn - first name
     * @param ln - last name
     * @param age - age
     * @param gen - gender
     * @param ph - phone
     * @return created passenger object
     */
    public Passenger createPassengerService(String fn,String ln,int age,String gen,String ph);

    /**
     *
     * @param id - passenger id
     * @return Passenger object
     */
    public Optional<Passenger> getPassengerService(long id);

    /**
     *
     * @param id - passenger id
     * @return true if the passenger exits, else false
     */
    public boolean deletePassengerService(long id);
}
