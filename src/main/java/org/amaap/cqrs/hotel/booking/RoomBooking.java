package org.amaap.cqrs.hotel.booking;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class RoomBooking {
    static Scanner sc = new Scanner(System.in);

    static void showMenu() {
        while (true) {
            System.out.println("********** Welcome to Room Booking System Using CQRS *********");
            System.out.println("Select From Menu\n1.Book Room\n2.Show Available Rooms\n3.Exit");
            System.out.print("Enter Your Choice : ");

            int choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    bookRoom();
                    break;
                case 2:
                    System.out.println("Available Rooms:");
                    ShowRoomsAvailable.showAvailableRooms();
                    break;
                case 3:
                    System.exit(0);
                default:
                    System.out.println("Select from menu only");
            }
        }
    }

    public static void bookRoom() {
        System.out.println("Available Rooms:");
        ShowRoomsAvailable.showAvailableRooms();

        System.out.print("Enter Room Name you Want to book : ");
        String roomName = sc.nextLine();

        System.out.print("Enter Your Arrival Date in Format(YYYY-MM-DD) : ");
        String arrdate = sc.nextLine();

        System.out.print("Enter Your Departure Date in Format(YYYY-MM-DD) : ");
        String depdate = sc.nextLine();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date arrivalDate;
        Date departureDate;
        try {
            arrivalDate = new Date(sdf.parse(arrdate).getTime());
            departureDate = new Date(sdf.parse(depdate).getTime());
        } catch (ParseException e) {
            System.out.println("Enter date in exact format");
            return;
        }

        String query = "INSERT INTO command_db.Booking (room_name, arrival_date, departure_date) VALUES (?, ?, ?)";

        String url = "jdbc:mysql://localhost:3306/command_db";
        try (Connection connection = DBConnection.getDbConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, roomName);
            preparedStatement.setDate(2, new java.sql.Date(arrivalDate.getTime()));
            preparedStatement.setDate(3, new java.sql.Date(departureDate.getTime()));

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Room booked successfully!");
                updateRoomStatusIfDeparted(roomName, departureDate);
            } else {
                System.out.println("Failed to book room!");
            }
        } catch (SQLException e) {
            // Handling SQL exception
            e.printStackTrace();
        }
    }

    // Method to update room status if departure date has passed
    public static void updateRoomStatusIfDeparted(String roomName, Date departureDate) {
        String checkBookingQuery = "SELECT COUNT(*) FROM command_db.Booking WHERE room_name = ? AND departure_date >= NOW()";
        String updateRoomStatusQuery = "UPDATE query_db.Room SET status = 'Free' WHERE room_name = ?";

        String url = "jdbc:mysql://localhost:3306/command_db";

        try (Connection connection = DBConnection.getDbConnection(url);
             PreparedStatement checkBookingStatement = connection.prepareStatement(checkBookingQuery);
             PreparedStatement updateRoomStatusStatement = connection.prepareStatement(updateRoomStatusQuery)) {

            // Check if there are any active bookings for the room with a departure date that hasn't passed
            checkBookingStatement.setString(1, roomName);
            ResultSet resultSet = checkBookingStatement.executeQuery();
            resultSet.next();
            int activeBookings = resultSet.getInt(1);

            // If there are no active bookings, update the room status to 'Free'
            if (activeBookings == 0) {
                updateRoomStatusStatement.setString(1, roomName);
                int rowsUpdated = updateRoomStatusStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Room status updated to 'Free'!");
                } else {
                    System.out.println("Room status not updated!");
                }
            }

        } catch (SQLException e) {
            // Handling SQL exception
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        showMenu();
    }
}
