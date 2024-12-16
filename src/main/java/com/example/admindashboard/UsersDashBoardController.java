package com.example.admindashboard;

import com.example.admindashboard.User;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;


public class UsersDashBoardController implements Initializable  {
    @FXML
    private ImageView homeImage,adminImage,planeImage,usersImage,ticketImage,logo,reviewsImage;

    @FXML
    private Button logoutButton,btn_workbench,btn_workbench1,btn_workbench11 ;
    private Stage stage;
    private Scene scene;
    @FXML
    private Parent root;
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User,Number> ageCol;
    @FXML
    private TableColumn<User,String> idCol;
    @FXML
    private TableColumn<User,String> nameCol;
    @FXML
    private TableColumn<User,String> lastnameCol;
    @FXML
    private TableColumn<User,String> passwordCol;
    String query =null;
    Connection connection =null;
    ResultSet resultSet = null;
    @FXML
    TextField idField,nameField,lastnameField,ageField,passwordField;
    @FXML
    ImageView profileImage;


    ObservableList<User>  UsersList= FXCollections.observableArrayList();

    public void switchToFlights(javafx.event.ActionEvent event) throws IOException {
        root= FXMLLoader.load(getClass().getResource("FlightsDashBoard.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchToHome(javafx.event.ActionEvent event) throws IOException {
        root= FXMLLoader.load(getClass().getResource("MainDashBoard.fxml"));
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
            usersTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                handleUserSelection(newValue);
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

            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        lastnameCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
    }
    private void refreshTable() {
        try {
            UsersList.clear();
            query= " SELECT * FROM users";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            resultSet=preparedStatement.executeQuery();
            while(resultSet.next()){
                UsersList.add(new User(
                        resultSet.getString("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        resultSet.getString("password"),
                        resultSet.getInt("age")));
                usersTable.setItems(UsersList);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void handleUserSelection(User selectedUser) {
        if (selectedUser != null) {
            String numero = selectedUser.getId();
            String nom = selectedUser.getNom();
            String prenom = selectedUser.getPrenom();
            Number age = selectedUser.getAge();
            idField.setText(numero);
            nameField.setText(nom);
            lastnameField.setText(prenom);
            ageField.setText(String.valueOf(age));
            show(numero);
        }
    }

    public void updateUser(ActionEvent actionEvent) {
        String numero=idField.getText();
        String name=nameField.getText();
        String lastname= lastnameField.getText();
        int age;
        // age mich number
        try {
            age = Integer.parseInt(ageField.getText());
        } catch (NumberFormatException e) {
            Alert alert =new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Missing Values");
            alert.setContentText("The age is not a valid value");
            alert.showAndWait();

            return;
        }

        if(numero.isEmpty()||name.isEmpty()||lastname.isEmpty()||age<=0){
            Alert alert =new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Missing Values");
            alert.setContentText("Please fill all the DATA");
            alert.showAndWait();
        }else{
            try {
                String query = "UPDATE users SET age=?, nom=?, prenom=? WHERE id=?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, age);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, lastname);
                preparedStatement.setString(4, numero);

                int rowsUpdated = preparedStatement.executeUpdate();

                if (rowsUpdated > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Update Successful");
                    alert.setContentText("User details updated successfully.");
                    alert.showAndWait();
                    refreshTable();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Update Failed");
                    alert.setContentText("There is no User with the id: "+numero);
                    alert.showAndWait();
                }
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Database Error");
                alert.setContentText("Error while updating user details: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void deleteUser(ActionEvent event) {
        String userId = idField.getText();
        if (userId.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Missing User ID");
            alert.setContentText("Please enter the ID of the User to delete.");
            alert.showAndWait();
        } else {
            try {
                String checkQuery = "SELECT * FROM users WHERE id=?";
                PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, userId);
                ResultSet resultSet = checkStatement.executeQuery();

                if (!resultSet.next()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Flight Not Found");
                    alert.setContentText("No flight found with ID " + userId + ".");
                    alert.showAndWait();
                    return;
                }

                // If the flight exists, show confirmation dialog
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setHeaderText("Confirm Deletion");
                confirmation.setContentText("Are you sure you want to delete the user with ID " + userId + "?");
                Optional<ButtonType> result = confirmation.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Perform deletion
                    String deleteQuery = "DELETE FROM users WHERE id=?";
                    PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
                    deleteStatement.setString(1, userId);
                    int rowsDeleted = deleteStatement.executeUpdate();

                    if (rowsDeleted > 0) {
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setHeaderText("Delete Successful");
                        successAlert.setContentText("User with ID " + userId + " has been deleted.");
                        successAlert.showAndWait();
                        refreshTable(); // Refresh the table after deletion
                    } else {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setHeaderText("Delete Failed");
                        errorAlert.setContentText("Failed to delete user with ID " + userId + ".");
                        errorAlert.showAndWait();
                    }
                }
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Database Error");
                alert.setContentText("Error while deleting user: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
    
    ///////////////////////////// IMAGE UPLOAD  w mb3ed SHOW //////////////////////////////
    @FXML
    private void handleUploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());

            profileImage.setImage(image);

            // Call a method to save the image to the database
            saveImageToDatabase(selectedFile);
        }
    }
    private void saveImageToDatabase(File imageFile) {
        try {
            byte[] imageData = Files.readAllBytes(imageFile.toPath());


            String userId=idField.getText();
            if(userId.isEmpty()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Load Failed");
                alert.setContentText("NONE user selected ");
                alert.showAndWait();
            }
            else{

                String query = "UPDATE users SET profileImage = ? WHERE id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(2, userId);
                preparedStatement.setBytes(1, imageData);
                preparedStatement.executeUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Image Upload");
                alert.setHeaderText("Image uploaded successfully.");
                alert.showAndWait();
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Image Upload Error");
            alert.setHeaderText("Error uploading image.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    // show l image houni, fil user dashboard

    @FXML
    public void show(String idUser) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            DataBaseConnection dbConnection = new DataBaseConnection();
            connection = dbConnection.getConnection();

            String query = "SELECT profileImage FROM users WHERE id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, idUser);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Blob blob = resultSet.getBlob("profileImage");
                if (blob != null) {
                    try (InputStream inputStream = blob.getBinaryStream()) {
                        Image image = new Image(inputStream);
                        profileImage.setImage(image);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    File file = new File("images/Image_not_available.png");
                    Image image = new Image(file.toURI().toString());
                    profileImage.setImage(image);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Image Upload Error");
                alert.setHeaderText("User Not Found.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}

