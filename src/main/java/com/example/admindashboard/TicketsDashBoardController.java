package com.example.admindashboard;

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


public class TicketsDashBoardController implements Initializable  {
    @FXML
    private ImageView homeImage,adminImage,planeImage,usersImage,ticketImage,logo,reviewsImage;

    @FXML
    private Button logoutButton,btn_workbench,btn_workbench1,btn_workbench11 ;
    private Stage stage;
    private Scene scene;
    @FXML
    private Parent root;

    @FXML
    private TableView<Ticket> ticketsTable;
    @FXML
    private TableColumn<Ticket, Date> dateCol;
    @FXML
    private TableColumn<Ticket, String> idCol;
    @FXML
    private TableColumn<Ticket, String> userIdCol;
    @FXML
    private TableColumn<Ticket, String> flightIdCol;
    @FXML
    private TableColumn<Ticket, String> departCol;
    @FXML
    private TableColumn<Ticket, String> destinationCol;
    @FXML
    private TableColumn<Ticket, String> classCol;
    @FXML
    private TableColumn<Ticket, Double> priceCol;

    String query =null;
    Connection connection =null;
    ResultSet resultSet = null;
    @FXML
    TextField idField,destinationField,departField,priceField,userIdField,classField,flightIdField;
    @FXML
    DatePicker dateField;

    ObservableList<Ticket> TicketList= FXCollections.observableArrayList();

    public void switchToHome(javafx.event.ActionEvent event) throws IOException {
        root= FXMLLoader.load(getClass().getResource("MainDashBoard.fxml"));
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
    public void switchToFlights(javafx.event.ActionEvent event) throws IOException {
        root= FXMLLoader.load(getClass().getResource("FlightsDashBoard.fxml"));
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

    @FXML
    protected void onLogOutClick() {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();
    }
    public void loadImage(String path,ImageView img){
        File ticketsFile = new File(path);
        Image ticket = new Image(ticketsFile.toURI().toString());
        img.setImage(ticket);
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadData();
            ticketsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                handleticketelection(newValue);
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

    private void loadData() throws SQLException {
        DataBaseConnection dbConnection = new DataBaseConnection();
        connection = dbConnection.getConnection();
        refreshTable();

        idCol.setCellValueFactory(new PropertyValueFactory<>("idt"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        departCol.setCellValueFactory(new PropertyValueFactory<>("depart"));
        destinationCol.setCellValueFactory(new PropertyValueFactory<>("destination"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        classCol.setCellValueFactory(new PropertyValueFactory<>("classe"));
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("iduser"));
        flightIdCol.setCellValueFactory(new PropertyValueFactory<>("idflight"));
    }
    private void refreshTable() {
        try {
            TicketList.clear();
            query= " SELECT * FROM ticket";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            resultSet=preparedStatement.executeQuery();
            while(resultSet.next()){
                TicketList.add(new Ticket(
                        resultSet.getString("idt"),
                        resultSet.getString("iduser"),
                        resultSet.getString("idflight"),
                        resultSet.getString("destination"),
                        resultSet.getString("depart"),
                        resultSet.getString("classe"),
                        resultSet.getDate("date"),
                        resultSet.getDouble("price")));
                ticketsTable.setItems(TicketList);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleticketelection(Ticket selectedTicket) {
        if (selectedTicket != null) {
            String numero = selectedTicket.getIdt();
            String destination = selectedTicket.getDestination();
            String departure = selectedTicket.getDepart();
            java.sql.Date sqlDate = (java.sql.Date) selectedTicket.getDate();
            double price = selectedTicket.getPrice();
            String iduser = selectedTicket.getIduser();
            String idflight = selectedTicket.getIdflight();
            String classe = selectedTicket.getClasse();

            idField.setText(numero);
            destinationField.setText(destination);
            departField.setText(departure);
            priceField.setText(String.valueOf(price));
            userIdField.setText(iduser);
            flightIdField.setText(idflight);
            classField.setText(classe);

            LocalDate localDate = sqlDate.toLocalDate();

            dateField.setValue(localDate);
        }
    }

    //////// na3mel l CRUD operations kol -------->>>>> \\\\\\\\\\\\

    public void updateTicket(ActionEvent actionEvent) {

        String numero=idField.getText();
        String depart=departField.getText();
        String destination=destinationField.getText();
        LocalDate selectedDate = dateField.getValue();
        String userid=userIdField.getText();
        String flightid=flightIdField.getText();
        String classe=classField.getText();
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

        if(numero.isEmpty()||depart.isEmpty()||destination.isEmpty()||selectedDate==null||price<=0||userid.isEmpty()||flightid.isEmpty()||classe.isEmpty()){
            Alert alert =new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Missing Values");
            alert.setContentText("Please fill all the DATA");
            alert.showAndWait();
        }else{
            try {
                String query = "UPDATE ticket SET Depart=?, destination=?, date=? ,price=?,idflight=?,iduser=?,classe=? WHERE idt=?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, depart);
                preparedStatement.setString(2, destination);
                preparedStatement.setDate(3, valueOf(selectedDate));
                preparedStatement.setDouble(4, price);
                preparedStatement.setString(5, flightid);
                preparedStatement.setString(6, userid);
                preparedStatement.setString(7, classe);
                preparedStatement.setString(8, numero);

                int rowsUpdated = preparedStatement.executeUpdate();

                if (rowsUpdated > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Update Successful");
                    alert.setContentText("Ticket details updated successfully.");
                    alert.showAndWait();
                    refreshTable();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Update Failed");
                    alert.setContentText("There is no Ticket with the id: "+numero);
                    alert.showAndWait();
                }
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Database Error");
                alert.setContentText("Error while updating Ticket details: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    public void addTicket(ActionEvent actionEvent) {
        String numero=idField.getText();
        String depart=departField.getText();
        String destination=destinationField.getText();
        LocalDate selectedDate = dateField.getValue();
        String userid=userIdField.getText();
        String flightid=flightIdField.getText();
        String classe=classField.getText();
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

        if (depart.isEmpty() || destination.isEmpty() || selectedDate == null  || userid.isEmpty() || flightid.isEmpty() || classe.isEmpty() ) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Missing Values");
            alert.setContentText("Please fill all the data");
            alert.showAndWait();
        }
        else if(price<=0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Missing Values");
            alert.setContentText("Price cant be 0 ");
            alert.showAndWait();

        }else {
            try {
                String query = "INSERT INTO ticket (Depart, destination, date ,price,idflight,iduser,classe ,idt) VALUES (?, ?,?, ?, ?,?,?,?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, depart);
                preparedStatement.setString(2, destination);
                preparedStatement.setDate(3, valueOf(selectedDate));
                preparedStatement.setDouble(4, price);
                preparedStatement.setString(5, flightid);
                preparedStatement.setString(6, userid);
                preparedStatement.setString(7, classe);
                preparedStatement.setString(8, numero);
                int rowsInserted = preparedStatement.executeUpdate();

                if (rowsInserted > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Insert Successful");
                    alert.setContentText("Ticket details inserted successfully.");
                    alert.showAndWait();
                    refreshTable();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Insert Failed");
                    alert.setContentText("Failed to insert Ticket details.");
                    alert.showAndWait();
                }
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Database Error");
                alert.setContentText("Error while inserting Ticket details: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void deleteTicket(ActionEvent event) {
        String TicketId = idField.getText();
        if (TicketId.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Missing Ticket ID");
            alert.setContentText("Please enter the ID of the Ticket to delete.");
            alert.showAndWait();
        } else {
            try {
                // Check  Ticket with the provided ID exists or not
                String checkQuery = "SELECT * FROM ticket WHERE idt=?";
                PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, TicketId);
                ResultSet resultSet = checkStatement.executeQuery();

                if (!resultSet.next()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Ticket Not Found");
                    alert.setContentText("No Ticket found with ID " + TicketId + ".");
                    alert.showAndWait();
                    return;
                }

                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setHeaderText("Confirm Deletion");
                confirmation.setContentText("Are you sure you want to delete the Ticket with ID " + TicketId + "?");
                Optional<ButtonType> result = confirmation.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    String deleteQuery = "DELETE FROM ticket WHERE idt=?";
                    PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
                    deleteStatement.setString(1, TicketId);
                    int rowsDeleted = deleteStatement.executeUpdate();

                    if (rowsDeleted > 0) {
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setHeaderText("Delete Successful");
                        successAlert.setContentText("Ticket with ID " + TicketId + " has been deleted.");
                        successAlert.showAndWait();
                        refreshTable();
                    } else {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setHeaderText("Delete Failed");
                        errorAlert.setContentText("Failed to delete Ticket with ID " + TicketId + ".");
                        errorAlert.showAndWait();
                    }
                }
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Database Error");
                alert.setContentText("Error while deleting Ticket: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }


}
