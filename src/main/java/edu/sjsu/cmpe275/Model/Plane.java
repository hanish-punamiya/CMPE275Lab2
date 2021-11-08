package edu.sjsu.cmpe275.Model;

import javax.persistence.*;

@Entity
@Table(name="plane")
public class Plane {

    @Id
    @Column(name="planeid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int planeId;

    @Column(name="model")
    private String model;

    @Column(name="capacity")
    private int capacity;

    @Column(name="manufacturer")
    private String manufacturer;

    @Column(name="yearofmanufacturer")
    private int yearOfManufacture;
}
