package edu.sjsu.cmpe275.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @Column(name="reservationnumber")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long reservationNumber;

    @ManyToOne
    @JoinColumn(name = "passengerid", referencedColumnName = "id")
    @JsonIgnoreProperties({"age","gender","phone","reservations","flights"})
    private Passenger passenger;

    @Column(name = "origin")
    private String origin;

    @Column(name = "destination")
    private String destination;

    @Column(name = "price")
    private int price;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name="reservation_flight",
            joinColumns = {@JoinColumn(name = "reservation_id", referencedColumnName = "reservationnumber", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "flight_number", referencedColumnName = "flightnumber", nullable = false)}
    )
    @JsonIgnoreProperties({"price","seatsLeft","description","plane","passengers","reservations"})
    private List<Flight> flights;

    public Reservation(long reservationNumber, Passenger passenger, String origin, String destination, int price, List<Flight> flights) {
        this.reservationNumber = reservationNumber;
        this.passenger = passenger;
        this.origin = origin;
        this.destination = destination;
        this.price = price;
        this.flights = flights;
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

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }
}
