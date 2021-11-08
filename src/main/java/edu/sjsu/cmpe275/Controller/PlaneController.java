package edu.sjsu.cmpe275.Controller;

import edu.sjsu.cmpe275.Model.Plane;
import edu.sjsu.cmpe275.Repository.PlaneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/plane")
public class PlaneController {

    @Autowired
    PlaneRepository planeRepository;


    @GetMapping("/planes")
    public ResponseEntity<List<Plane>> getAllPlanes() {
        try {
            List<Plane> planes = new ArrayList<Plane>();

            planeRepository.findAll().forEach(planes::add);

            if (planes.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(planes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
