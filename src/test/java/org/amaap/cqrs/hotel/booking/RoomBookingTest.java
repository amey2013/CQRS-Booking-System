package org.amaap.cqrs.hotel.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class RoomBookingTest {

    RoomBooking roomBooking = new RoomBooking();


    @Test
    void bookRoom_Successfully() {
        // Arrange
        provideInput("1\nRoom 101\n2024-02-20\n2024-02-22\n3\n");

        // Act
        RoomBooking.main(new String[]{});

        // Assert
        assertTrue(isRoomBooked("Room 101", "2024-02-20", "2024-02-22"));
        assertTrue(isRoomStatusUpdatedBeforeBooking("Room 101", "Full"));
        assertTrue(isRoomStatusUpdatedAfterBooking("Room 101", "Free"));
    }






    private void provideInput(String data) {
        InputStream inputStream = new ByteArrayInputStream(data.getBytes());
        System.setIn(inputStream);
    }



    private boolean isRoomBooked(String roomName, String arrivalDate, String departureDate) {
        String query = "SELECT COUNT(*) FROM command_db.Booking WHERE room_name = ? AND arrival_date = ? AND departure_date = ?";
        String url = "jdbc:mysql://localhost:3306/command_db";

        try (Connection connection = DriverManager.getConnection(url, "your_username", "your_password");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, roomName);
            preparedStatement.setDate(2, java.sql.Date.valueOf(arrivalDate));
            preparedStatement.setDate(3, java.sql.Date.valueOf(departureDate));

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            return count > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private boolean isRoomStatusUpdatedBeforeBooking(String roomName, String status) {
        String query = "SELECT status FROM query_db.Room WHERE room_name = ?";
        String url = "jdbc:mysql://localhost:3306/query_db";

        try (Connection connection = DriverManager.getConnection(url, "your_username", "your_password");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, roomName);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String currentStatus = resultSet.getString("status");
                return currentStatus.equals(status);
            } else {

                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isRoomStatusUpdatedAfterBooking(String roomName, String status) {
        String query = "SELECT status FROM query_db.Room WHERE room_name = ?";
        String url = "jdbc:mysql://localhost:3306/query_db";

        try (Connection connection = DriverManager.getConnection(url, "your_username", "your_password");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, roomName);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String currentStatus = resultSet.getString("status");
                return currentStatus.equals(status);
            } else {

                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
