package com.digibusy.EventEverBookingService.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "ticket")
@Builder
@Data
@AllArgsConstructor
public class Ticket{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "booking_id",nullable = false)
    private Booking booking;
    private String seatNumber;
    @Lob
    private String qrCode;

    public Ticket() {
    }

//    public Ticket(long id, Booking booking, String seatNumber, String qrCode) {
//        this.id = id;
//        this.booking = booking;
//        this.seatNumber = seatNumber;
//        this.qrCode = qrCode;
//    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", booking=" + booking +
                ", seatNumber='" + seatNumber + '\'' +
                ", qrCode='" + qrCode + '\'' +
                '}';
    }
}
