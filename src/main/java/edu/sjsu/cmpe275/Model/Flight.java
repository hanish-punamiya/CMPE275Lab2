package edu.sjsu.cmpe275.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "flight")
public class Flight {


    @Id
    @Column(name="flightnumber")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long flightNumber;

    @Column(name="price")
    private int price;

    @Column(name="origin")
    private String origin;

    @Column(name="destination")
    private String destination;

    /*  Date format: yy-mm-dd-hh, do not include minutes and seconds.
     ** Example: 2017-03-22-19
     **The system only needs to support PST. You can ignore other time zones.
     */

    @Column(name="departuretime")
    private Date departureTime;

    @Column(name="arrivaltime")
    private Date arrivalTime;

    @Column(name="seatsleft")
    private int seatsLeft;

    @Column(name="description")
    private String description;

    @OneToOne(optional = false)
    @JoinColumn(name = "planeid", referencedColumnName = "planeid")
    private Plane plane;

    @ManyToMany(mappedBy = "flights", fetch = FetchType.LAZY)
    private List<Passenger> passengers = new ArrayList<>();

    public long getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(long flightNumber) {
        this.flightNumber = flightNumber;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getSeatsLeft() {
        return seatsLeft;
    }

    public void setSeatsLeft(int seatsLeft) {
        this.seatsLeft = seatsLeft;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Plane getPlane() {
        return plane;
    }

    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }
    //    @Column(name="price")
//    private List<Passenger> passengers;


}
