package com.example.admindashboard;

import com.example.admindashboard.Flight;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.sql.Date.valueOf;


public class FlightsDashBoardController implements Initializable  {
    @FXML
    private ImageView homeImage,adminImage,planeImage,usersImage,ticketImage,logo,reviewsImage;

    @FXML
    private Button logoutButton,btn_workbench,btn_workbench1,btn_workbench11 ;
    private Stage stage;
    private Scene scene;
    @FXML
    private Parent root;
    @FXML
    private TableView<Flight> flightsTable;
    @FXML
    private TableColumn<Flight, Date> dateCol;
    @FXML
    private TableColumn<Flight,String> idCol;
    @FXML
    private TableColumn<Flight,String> departCol;
    @FXML
    private TableColumn<Flight,String> destinationCol;
    @FXML
    private TableColumn<Flight,Double> priceCol;
    String query =null;
    Connection connection =null;
    ResultSet resultSet = null;
    @FXML
    TextField idField,destinationField,departField,priceField;
    @FXML
    DatePicker dateField;

    ObservableList<Flight>  FlightList= FXCollections.observableArrayList();

    public void switchToHome(javafx.event.ActionEvent event) throws IOException {
        root= FXMLLoader.load(getClass().getResource("MainDashBoard.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchToReviews(javafx.event.ActionEvent event) throws IOException {
        root= FXMLLoader.load(getClass().getResource("ReviewsDashBoard.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchToUsers(javafx.event.ActionEvent event) throws IOException {
        root= FXMLLoader.load(getClass().getResource("UsersDashBoard.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToTickets(javafx.event.ActionEvent event) throws IOException {
        root= FXMLLoader.load(getClass().getResource("TicketsDashBoard.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void onLogOutClick() {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadData();
            flightsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                handleFlightSelection(newValue);
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        loadImage("images/home.png",homeImage);
        loadImage("images/Logo.png",logo);
        loadImage("images/airplane.png",planeImage);
        loadImage("images/group.png",usersImage);
        loadImage("images/feedback.png",reviewsImage);
        loadImage("images/ticket-flight.png",ticketImage);
        loadImage("images/software-engineer.png",adminImage);


    }
    public void loadImage(String path,ImageView img){
        File ticketsFile = new File(path);
        Image ticket = new Image(ticketsFile.toURI().toString());
        img.setImage(ticket);
    }

    private void loadData() throws SQLException {
        DataBaseConnection dbConnection = new DataBaseConnection();
        connection = dbConnection.getConnection();
        refreshTable();

        idCol.setCellValueFactory(new PropertyValueFactory<>("Numero"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("Date"));
        destinationCol.setCellValueFactory(new PropertyValueFactory<>("Destination"));
        departCol.setCellValueFactory(new PropertyValueFactory<>("Depart"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("Price"));
    }
    private void refreshTable() {
        try {
        FlightList.clear();
        query= " SELECT * FROM flights";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            resultSet=preparedStatement.executeQuery();
            while(resultSet.next()){
                FlightList.add(new Flight(
                        resultSet.getString("Numero"),
                        resultSet.getDate("Date"),
                        resultSet.getString("Destination"),
                        resultSet.getString("Depart"),
                        resultSet.getDouble("Price")));
                flightsTable.setItems(FlightList);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleFlightSelection(Flight selectedFlight) {
        if (selectedFlight != null) {
            String numero = selectedFlight.getNumero();
            String destination = selectedFlight.getDestination();
            String departure = selectedFlight.getDepart();
            java.sql.Date sqlDate = (java.sql.Date) selectedFlight.getDate();
            double price = selectedFlight.getPrice();
            idField.setText(numero);
            destinationField.setText(destination);
            departField.setText(departure);
            priceField.setText(String.valueOf(price));
            LocalDate localDate = sqlDate.toLocalDate();

            dateField.setValue(localDate);
        }
    }

//CRUD lenna /////////////||\\\\\\\\\\\\
    public void updateFlight(ActionEvent actionEvent) {

        String numero=idField.getText();
        String depart=departField.getText();
        String destination=destinationField.getText();
        LocalDate selectedDate = dateField.getValue();
        double price;
        try {
            price = Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            Alert alert =new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Price not valide ");
            alert.setContentText("Enter a valide price");
            alert.showAndWait();

            return;
        }

        if(numero.isEmpty()||depart.isEmpty()||destination.isEmpty()||selectedDate==null||price<=0){
            Alert alert =new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Missing Values");
            alert.setContentText("Please fill all the DATA");
            alert.showAndWait();
        }else{
            try {
            String query = "UPDATE flights SET Depart=?, Destination=?, Date=? ,Price=? WHERE Numero=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, depart);
            preparedStatement.setString(2, destination);
            preparedStatement.setDate(3, valueOf(selectedDate));
            preparedStatement.setDouble(4, price);
                preparedStatement.setString(5, numero);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Update Successful");
                alert.setContentText("Flight details updated successfully.");
                alert.showAndWait();
                refreshTable();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Update Failed");
                alert.setContentText("There is no Flight with the id: "+numero);
                alert.showAndWait();
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Database Error");
            alert.setContentText("Error while updating flight details: " + e.getMessage());
            alert.showAndWait();
        }
        }
    }
        public void addFlight(ActionEvent actionEvent) {
            String numero=idField.getText();
            String depart = departField.getText();
            String destination = destinationField.getText();
            LocalDate selectedDate = dateField.getValue();
            double price;
            try {
                price = Double.parseDouble(priceField.getText());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Price not valid");
                alert.setContentText("Enter a valid price");
                alert.showAndWait();
                return;
            }

            if (depart.isEmpty() || destination.isEmpty() || selectedDate == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Missing Values");
                alert.setContentText("Please fill all the data");
                alert.showAndWait();
            }else if(price<=0){
                   Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Incorrect Value");
                    alert.setContentText("Please enter a positif price");
                    alert.showAndWait();

            } else {
                try {
                    String query = "INSERT INTO flights (Depart, Destination, Date, Price,Numero) VALUES (?, ?,?, ?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, depart);
                    preparedStatement.setString(2, destination);
                    preparedStatement.setDate(3, valueOf(selectedDate));
                    preparedStatement.setDouble(4, price);
                    preparedStatement.setString(5, numero);

                    int rowsInserted = preparedStatement.executeUpdate();

                    if (rowsInserted > 0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Insert Successful");
                        alert.setContentText("Flight details inserted successfully.");
                        alert.showAndWait();
                        refreshTable();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Insert Failed");
                        alert.setContentText("Failed to insert flight details.");
                        alert.showAndWait();
                    }
                } catch (SQLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Database Error");
                    alert.setContentText("Error while inserting flight details: " + e.getMessage());
                    alert.showAndWait();
                }
            }
        }

    @FXML
    public void deleteFlight(ActionEvent event) {
        String flightId = idField.getText();
        if (flightId.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Missing Flight ID");
            alert.setContentText("Please enter the ID of the flight to delete.");
            alert.showAndWait();
        } else {
            try {
                String checkQuery = "SELECT * FROM flights WHERE Numero=?";
                PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, flightId);
                ResultSet resultSet = checkStatement.executeQuery();

                if (!resultSet.next()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Flight Not Found");
                    alert.setContentText("No flight found with ID " + flightId + ".");
                    alert.showAndWait();
                    return;
                }

                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setHeaderText("Confirm Deletion");
                confirmation.setContentText("Are you sure you want to delete the flight with ID " + flightId + "?");
                Optional<ButtonType> result = confirmation.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {

                    String deleteQuery = "DELETE FROM flights WHERE Numero=?";
                    PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
                    deleteStatement.setString(1, flightId);
                    int rowsDeleted = deleteStatement.executeUpdate();

                    if (rowsDeleted > 0) {
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setHeaderText("Delete Successful");
                        successAlert.setContentText("Flight with ID " + flightId + " has been deleted.");
                        successAlert.showAndWait();
                        refreshTable(); // Refresh the table after deletion
                    } else {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setHeaderText("Delete Failed");
                        errorAlert.setContentText("Failed to delete flight with ID " + flightId + ".");
                        errorAlert.showAndWait();
                    }
                }
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Database Error");
                alert.setContentText("Error while deleting flight: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }


}
