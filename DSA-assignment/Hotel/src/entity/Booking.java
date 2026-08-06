/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author jlohz
 */
public class Booking {
    private static final AtomicInteger bookingIDCounter = new AtomicInteger(1);

    private int bookingID;
    private String bookingDate;
    private String checkInDate;
    private String checkOutDate;

    private Member member;
    private Room room;

    public Booking() {
    }

    // partial constructor
    public Booking(String bookingDate,
                   String checkInDate, String checkOutDate,
                   Member member, Room room) {

        this.bookingID = bookingIDCounter.getAndIncrement();
        this.bookingDate = bookingDate;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.member = member;
        this.room = room;
    }

    // full constructor for Booking class
    public Booking(int bookingID, String bookingDate,
                   String checkInDate, String checkOutDate,
                   Member member, Room room) {

        this.bookingID = bookingID;
        this.bookingDate = bookingDate;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.member = member;
        this.room = room;
    }

    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingID='" + bookingID + '\'' +
                ", bookingDate='" + bookingDate + '\'' +
                ", checkInDate='" + checkInDate + '\'' +
                ", checkOutDate='" + checkOutDate + '\'' +
                ", member=" + member +
                ", room=" + room +
                '}';
    }
}
