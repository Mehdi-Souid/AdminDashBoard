package com.example.admindashboard;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;




public class ReviewsDashBoardController implements Initializable {

    @FXML
    private ImageView homeImage, adminImage, planeImage, usersImage,  ticketImage, logo, chat,reviewsImage;

    @FXML
    private Button logoutButton, btn_workbench, btn_workbench1, btn_workbench11;
    private Stage stage;
    private Scene scene;
    @FXML
    private Parent root;
    @FXML
    private TableView<User> UsersTable;
    @FXML
    private TableColumn<User, String> idCol;
    @FXML
    private TableColumn<User, String> userfullnameCol;

    String query = null;
    Connection connection = null;
    ResultSet resultSet = null;
    @FXML
    private TextField useridField;
    @FXML
    private TextArea chatTextArea;
    @FXML
    private TextField messageTextField;
    @FXML
    VBox vBoxMessages;


    private ServerSocket serverSocket;

    private List<ClientHandler> clients = new ArrayList<>();

    ObservableList<User> UsersList = FXCollections.observableArrayList();


    public void switchToFlights(javafx.event.ActionEvent event) throws IOException {
        onClose();
        root = FXMLLoader.load(getClass().getResource("FlightsDashBoard.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToHome(javafx.event.ActionEvent event) throws IOException {
        onClose();
        root = FXMLLoader.load(getClass().getResource("MainDashBoard.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchToUsers(javafx.event.ActionEvent event) throws IOException {
        onClose();
        root= FXMLLoader.load(getClass().getResource("UsersDashBoard.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }


    public void switchToTickets(javafx.event.ActionEvent event) throws IOException {
        onClose();
        root = FXMLLoader.load(getClass().getResource("TicketsDashBoard.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void onClose() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void onLogOutClick() {
        onClose();
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
            serverSocket = new ServerSocket(6005);
            System.out.println("Server initialized and listening...");
            new Thread(this::acceptClientConnections).start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error creating ServerSocket...");
        }



        try {
            loadData();
            UsersTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
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
        loadImage("images/group.png",usersImage);
        loadImage("images/msg.png",chat);


    }

    private void loadData() throws SQLException {
        DataBaseConnection dbConnection = new DataBaseConnection();
        connection = dbConnection.getConnection();
        refreshTable();

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        userfullnameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNom() + " " + cellData.getValue().getPrenom()));

    }
    private void refreshTable() {
        try {
            UsersList.clear();
            query = " SELECT * FROM users";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                UsersList.add(new User(
                        resultSet.getString("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom")));
                UsersTable.setItems(UsersList);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleUserSelection(User selectedUser) {
        if (selectedUser != null) {
            String numero = selectedUser.getId();
            useridField.setText(numero);
        }
    }


    //////////////// SOCKET w THREADS implements \\\\\\\\\\\
    public class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader reader;
        private OutputStreamWriter writer;

        public ClientHandler(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new OutputStreamWriter(clientSocket.getOutputStream());
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    String finalMessage = message;
                    Platform.runLater(() -> addMessage(finalMessage));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                clients.remove(this);
            }
        }
        public void sendMessage(String message) {

            try {
                writer.write(message + "\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    writer.close();
                    reader.close();
                    clientSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    private void addMessage(String message) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new javafx.geometry.Insets(5, 5, 5, 10));

        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);

        textFlow.setStyle(
                "-fx-background-color: rgb(233, 233, 235);" +
                        "-fx-background-radius: 20px;");

        textFlow.setPadding(new Insets(5, 10, 5, 10));
        hBox.getChildren().add(textFlow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vBoxMessages.getChildren().add(hBox);
            }
        });
    }


    //lenna bedelt serverSocket != null && !serverSocket.isClosed() fi 3o4 null
    private void acceptClientConnections() {
        while (serverSocket != null && !serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error accepting client connection...");
            }
        }
    }
    @FXML
    public void sendMessage() {
        String message = messageTextField.getText().trim();

        ///////lenna msg missing userID

        if(useridField.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Missing values");
            alert.setContentText("Select user ID");
            alert.showAndWait();
        }
        else {

        if (!message.isEmpty()) {
            addMessageFromServer(message);
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
            messageTextField.clear();
        }
        }
    }
    private void addMessageFromServer(String message) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);

        hBox.setPadding(new Insets(5, 5, 5, 10));
        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle(
                "-fx-color: rgb(239, 242, 255);" +
                        "-fx-background-color: rgb(15, 125, 242);" +
                        "-fx-background-radius: 20px;");

        textFlow.setPadding(new Insets(5, 10, 5, 10));
        text.setFill(Color.color(0.934, 0.925, 0.996));

        hBox.getChildren().add(textFlow);
        vBoxMessages.getChildren().add(hBox);
    }

}




