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

    public Plane(int planeId, String model, int capacity, String manufacturer, int yearOfManufacture) {
        this.planeId = planeId;
        this.model = model;
        this.capacity = capacity;
        this.manufacturer = manufacturer;
        this.yearOfManufacture = yearOfManufacture;
    }

    public Plane() {
    }

    public int getPlaneId() {
        return planeId;
    }

    public void setPlaneId(int planeId) {
        this.planeId = planeId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getYearOfManufacture() {
        return yearOfManufacture;
    }

    public void setYearOfManufacture(int yearOfManufacture) {
        this.yearOfManufacture = yearOfManufacture;
    }
}
