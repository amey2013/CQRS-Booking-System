package org.amaap.cqrs.hotel.booking;



import java.sql.*;

public class ShowRoomsAvailable {

    public static void showAvailableRooms(){
        String query = "SELECT * FROM room WHERE status = 'Available'";
        String url = "jdbc:mysql://localhost:3306/query_db";
        try (

                Connection connection = DBConnection.getDbConnection(url);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()) {


            if (!resultSet.next()) {
                // ResultSet is empty
                System.out.println("No available rooms found.");
                return;
            }

            // ResultSet is not empty, process the rows
            System.out.println("Room Name ");
            do {
                String roomName = resultSet.getString("room_name");
                String status = resultSet.getString("status");
                System.out.println(roomName);
            } while (resultSet.next());
        } catch (SQLException e) {
            // Handling SQL exception
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        showAvailableRooms();
    }

}
