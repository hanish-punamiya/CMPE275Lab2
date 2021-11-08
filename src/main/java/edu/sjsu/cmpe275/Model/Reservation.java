package edu.sjsu.cmpe275.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.*;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @Column(name="reservationnumber")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long reservationNumber;

    @ManyToOne
    @JoinColumn(name = "passengerid", referencedColumnName = "id")
    @JsonIgnoreProperties({"age","gender","phone","reservations"})
    private Passenger passenger;

    @Column(name = "origin")
    private String origin;

    @Column(name = "destination")
    private String destination;

    @Column(name = "price")
    private int price;

    public Reservation(long reservationNumber, Passenger passenger, String origin, String destination, int price) {
        this.reservationNumber = reservationNumber;
        this.passenger = passenger;
        this.origin = origin;
        this.destination = destination;
        this.price = price;
    }

    public Reservation() {

    }

    public long getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(long reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
