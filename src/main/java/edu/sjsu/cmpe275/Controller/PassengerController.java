package edu.sjsu.cmpe275.Controller;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.sjsu.cmpe275.Helper.Error.Response;
import edu.sjsu.cmpe275.Model.Passenger;
import edu.sjsu.cmpe275.Repository.PassengerRepository;
import edu.sjsu.cmpe275.Service.PassengerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
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
    @Transactional(rollbackFor = { Exception.class})
    public ResponseEntity<Object> updatePassenger(@PathVariable("id") long id, @RequestParam("firstname") String firstName,
                                                  @RequestParam("lastname") String lastName, @RequestParam("age") int age,
                                                  @RequestParam("gender") String gender, @RequestParam("phone") String phone) {
        try {
            Optional<Passenger> PassengerData = passengerRepository.findById(id);

            if (PassengerData.isPresent()) {
                if(passengerRepository.findByPhone(phone)!=null && phone!=PassengerData.get().getPhone()){
                    return new ResponseEntity<>(new Response("400", "Another passenger with the same number already exists"), HttpStatus.BAD_REQUEST);
                }
                Passenger _passenger = PassengerData.get();
                _passenger.setFirstName(firstName);
                _passenger.setLastName(lastName);
                _passenger.setAge(age);
                _passenger.setGender(gender);
                _passenger.setPhone(phone);
                Passenger newPassenger = passengerRepository.save(_passenger);
                newPassenger.setFlights(null);
                if(true)
                    throw new Exception("Hanish");
                return new ResponseEntity<Object>(newPassenger, HttpStatus.OK);
            } else {
                return new ResponseEntity<Object>(new Response("404", "Passenger not found"), HttpStatus.NOT_FOUND);
            }
        } catch (Exception exception) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ResponseEntity<Object>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPassenger(@PathVariable("id") long id) {
        try {
            Optional<Passenger> passenger = passengerService.getPassengerService(id);
            if (passenger.isEmpty()) {
//                Map<String, String> errorResponse = new HashMap<>();
//                Map<String, Map> error = new HashMap<>();
//                errorResponse.put("code", "404");
//                errorResponse.put("msg", "Sorry, the requested passenger with ID " + id + " does not exist");
//                error.put("BadRequest", errorResponse);
//                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(new Response("404", "Sorry, the requested passenger with ID " + id + " does not exist"), HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(passenger.get(), HttpStatus.OK);
            }
        } catch (Exception exception) {
            return new ResponseEntity<Object>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/")
    public ResponseEntity<Object> createPassenger(@RequestParam("firstname") String firstName, @RequestParam("lastname") String lastName, @RequestParam("age") int age, @RequestParam("gender") String gender, @RequestParam("phone") String phone) {
        try {
            Passenger passenger = passengerService.createPassengerService(firstName, lastName, age, gender, phone);
            if (passenger == null) {
//                Map<String, String> errorResponse = new HashMap<>();
//                Map<String, Map> error = new HashMap<>();
//                errorResponse.put("code", "400");
//                errorResponse.put("msg", "Another passenger with the same number already exists");
//                error.put("BadRequest", errorResponse);
//                return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
                return new ResponseEntity<>(new Response("400", "Another passenger with the same number already exists"), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(passenger, HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<Object>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePassenger(@PathVariable("id") long id) {
        try {
            Optional<Passenger> passenger = passengerService.getPassengerService(id);
            if (passenger.isEmpty()) {
//                Map<String, String> errorResponse = new HashMap<>();
//                Map<String, Map> error = new HashMap<>();
//                errorResponse.put("code", "404");
//                errorResponse.put("msg", "Passenger with ID " + id + " does not exist");
//                error.put("BadRequest", errorResponse);
//                return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(new Response("404", "Passenger with ID " + id + " does not exist"), HttpStatus.NOT_FOUND);
            }
            passengerService.deletePassengerService(id);
//            Map<String, String> successResponse = new HashMap<>();
//            successResponse.put("code", "200");
//            successResponse.put("msg", "Passenger with ID " + id + " is successfully deleted");
//
//            return new ResponseEntity<>(successResponse,HttpStatus.OK);
            return new ResponseEntity<>(new edu.sjsu.cmpe275.Helper.Success.Response("200", "Passenger with ID " + id + " is successfully deleted"), HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<Object>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
