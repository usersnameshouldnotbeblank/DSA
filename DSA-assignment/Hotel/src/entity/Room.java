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
public class Room {
    private static final AtomicInteger roomIDCounter = new AtomicInteger(1);

    private int roomID;
    private String roomType;
    private RoomStatus roomStatus;

    // added enum class for room status
    public enum RoomStatus {
        Dirty,
        Cleaning_In_Progress,
        Inspected,
        Ready_for_Check_In,
        Occupied,
    }

    public Room() {
    }

    // new constructor for default room status
    public Room(String roomType) {
        this.roomID = roomIDCounter.getAndIncrement();
        this.roomType = roomType;
        this.roomStatus = RoomStatus.Ready_for_Check_In; // Default status
    }

    // modified full constructor from string to enum for room status, and counter for ID
    public Room(String roomType, RoomStatus roomStatus) {
        this.roomID = roomIDCounter.getAndIncrement();
        this.roomType = roomType;
        this.roomStatus = roomStatus;
    }

    // modified string > int
    public int getRoomID() {
        return roomID;
    }

    // modified
    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public RoomStatus getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomID='" + roomID + '\'' +
                ", roomType='" + roomType + '\'' +
                ", roomStatus='" + roomStatus + '\'' +
                '}';
    }
}
