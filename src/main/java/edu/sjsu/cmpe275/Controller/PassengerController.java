package edu.sjsu.cmpe275.Controller;

import edu.sjsu.cmpe275.Model.Passenger;
import edu.sjsu.cmpe275.Repository.PassengerRepository;
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

    @PutMapping("/{id}")
    public ResponseEntity<Passenger> updatePassenger(@PathVariable("id") long id, @RequestParam("firstname") String firstName, @RequestParam("lastname") String lastName, @RequestParam("age") int age, @RequestParam("gender") String gender, @RequestParam("phone") String phone) {
        Optional<Passenger> PassengerData = passengerRepository.findById(id);

        if (PassengerData.isPresent()) {
            Passenger _passenger = PassengerData.get();
            _passenger.setFirstName(firstName);
            _passenger.setLastName(lastName);
            _passenger.setAge(age);
            _passenger.setGender(gender);
            _passenger.setPhone(phone);
            return new ResponseEntity<>(passengerRepository.save(_passenger), HttpStatus.OK);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            Map<String, Map> error = new HashMap<>();
            errorResponse.put("code", "404");
            errorResponse.put("msg", "User not found");
            error.put("BadRequest",errorResponse);
//            return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
