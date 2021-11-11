package edu.sjsu.cmpe275.Controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.sjsu.cmpe275.Helper.Error.Response;
import edu.sjsu.cmpe275.Model.Passenger;
import edu.sjsu.cmpe275.Repository.PassengerRepository;
import edu.sjsu.cmpe275.Service.PassengerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/passenger")
public class PassengerController {

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    PassengerServiceImpl passengerService;

    @GetMapping("/passengers")
    public ResponseEntity<List<Passenger>> getAllPassengers() {
        try {
            List<Passenger> passengers = new ArrayList<Passenger>();

            passengerRepository.findAll().forEach(passengers::add);

            if (passengers.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(passengers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates the details of the passenger based on the provided parameters
     *
     * @param id        of the passenger to be updated
     * @param firstName to be updated of the passenger
     * @param lastName  to be updated of the passenger
     * @param age       to be updated of the passenger
     * @param gender    to be updated of the passenger
     * @param phone     to be updated of the passenger
     * @return Updated passenger details
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updatePassenger(@PathVariable("id") long id, @RequestParam("firstname") String firstName,
                                                  @RequestParam("lastname") String lastName, @RequestParam("age") int age,
                                                  @RequestParam("gender") String gender, @RequestParam("phone") String phone) {
        try {
            Optional<Passenger> PassengerData = passengerRepository.findById(id);
            if (PassengerData.isPresent()) {
                Passenger _passenger = PassengerData.get();
                _passenger.setFirstName(firstName);
                _passenger.setLastName(lastName);
                _passenger.setAge(age);
                _passenger.setGender(gender);
                _passenger.setPhone(phone);
                Passenger newPassenger = passengerRepository.save(_passenger);
                newPassenger.setFlights(null);
                return new ResponseEntity<Object>(newPassenger, HttpStatus.OK);
            } else {
                return new ResponseEntity<Object>(new Response("404", "Passenger not found"), HttpStatus.NOT_FOUND);
            }
        } catch (Exception exception) {
            return new ResponseEntity<Object>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     *
     * @param id - passenger id
     * @return if there is no passenger return error else return passenger
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getPassenger(@PathVariable("id") long id) {
        try {
            Optional<Passenger> passenger = passengerService.getPassengerService(id);
            if (passenger.isEmpty()) {
                return new ResponseEntity<>(new Response("404", "Sorry, the requested passenger with ID " + id + " does not exist"), HttpStatus.NOT_FOUND);
            } else {
                passenger.get().setFlights(null);
                return new ResponseEntity<>(passenger.get(), HttpStatus.OK);
            }
        } catch (Exception exception) {
            return new ResponseEntity<Object>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     *
     * @param firstName
     * @param lastName
     * @param age
     * @param gender
     * @param phone
     * @return the created passenger if the phone number is unique
     */
    @PostMapping("/")
    public ResponseEntity<Object> createPassenger(@RequestParam("firstname") String firstName, @RequestParam("lastname") String lastName, @RequestParam("age") int age, @RequestParam("gender") String gender, @RequestParam("phone") String phone) {
        try {
            Passenger passenger = passengerService.createPassengerService(firstName, lastName, age, gender, phone);
            if (passenger == null) {
                return new ResponseEntity<>(new Response("400", "Another passenger with the same number already exists"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(passenger, HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<Object>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     *
     * @param id - passenger id
     * @return success message if the passenger is deleted successfully else error message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePassenger(@PathVariable("id") long id) {
        try {
            Optional<Passenger> passenger = passengerService.getPassengerService(id);
            if (passenger.isEmpty()) {
                return new ResponseEntity<>(new Response("404", "Passenger with ID " + id + " does not exist"), HttpStatus.NOT_FOUND);
            }
            passengerService.deletePassengerService(id);
            return new ResponseEntity<>(new edu.sjsu.cmpe275.Helper.Success.Response("200", "Passenger with ID " + id + " is successfully deleted"), HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<Object>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
