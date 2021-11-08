package edu.sjsu.cmpe275.Repository;

import edu.sjsu.cmpe275.Model.Plane;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaneRepository extends JpaRepository<Plane,Long> {
    @Override
    List<Plane> findAll();
}
