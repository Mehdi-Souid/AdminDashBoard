package com.example.admindashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;


public class MainDashBoardController implements Initializable  {
    @FXML
    private ImageView homeImage,adminImage,planeImage,usersImage,planeImage2,ticketImage,logo,userImage,reviewsImage;
    @FXML
    private AreaChart<String, Integer> areaChart;
    @FXML
    private BarChart<String,Integer> dest_departChart;
    @FXML
    private BarChart<String,Double> chart;

    @FXML
    private Button logoutButton,btn_workbench,btn_workbench1,btn_workbench11 ;
    private Stage stage;
    private Scene scene;
    @FXML
    private Parent root;
    @FXML
    private  TextField ticketBuyers,totalUsers;
    String query =null;
    Connection connection =null;


    public void switchToFlights(javafx.event.ActionEvent event) throws IOException {
        root= FXMLLoader.load(getClass().getResource("FlightsDashBoard.fxml"));
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

    public void switchToReviews(javafx.event.ActionEvent event) throws IOException {
        root= FXMLLoader.load(getClass().getResource("ReviewsDashBoard.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void loadImage(String path,ImageView img){
        File ticketsFile = new File(path);
        Image ticket = new Image(ticketsFile.toURI().toString());
        img.setImage(ticket);
    }


    @FXML
    protected void onLogOutClick() {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            showAreaChart();
            showDepDestChart();
            showFlightPriceChart();
            showUserCard();
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
        loadImage("images/plane (1).png",planeImage2);
        loadImage("images/user.png",userImage);

    }

    public void showAreaChart() throws SQLException {
        DataBaseConnection dbconnection= new DataBaseConnection();
        try {
            connection=dbconnection.getConnection();

            String query = "SELECT DATE_FORMAT(Date, '%Y-%m-%d') AS day, COUNT(*) AS count \n" +
                    "FROM flights \n" +
                    "WHERE MONTH(Date) = MONTH(CURDATE()) AND YEAR(Date) = YEAR(CURDATE())\n" +
                    "GROUP BY DAY(Date)\n";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs=preparedStatement.executeQuery();

            Map <String, Integer> flightCountMap = new LinkedHashMap<>();

            while (rs.next()) {
                String day = String.valueOf((rs.getDate("day")).toLocalDate().getDayOfMonth());
                int count = rs.getInt("count");
                flightCountMap.put(day, count);
            }


            XYChart.Series<String,Integer> series = new XYChart.Series<>();

            for(Map.Entry<String, Integer> entry: flightCountMap.entrySet()) {
                String day=entry.getKey();
                int count=entry.getValue();
                series.getData().add(new XYChart.Data<>(day, count));

            }

            areaChart.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
// depart w destination chart houni
    public void showDepDestChart(){

        DataBaseConnection dbconnection=new DataBaseConnection();
       try {
            connection=dbconnection.getConnection();

            query="SELECT  Destination,COUNT(*) as count \n" +
                    "FROM flights \n" +
                    "GROUP BY Destination";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs=preparedStatement.executeQuery();

            XYChart.Series<String,Integer >series1 =new XYChart.Series<>();

           XYChart.Series<String,Integer >series2 =new XYChart.Series<>();

            while(rs.next()){
                String destination = rs.getString("Destination");
                int countDest=rs.getInt("count");
            series1.getData().add(new XYChart.Data<>(destination,countDest));
            }

           query="SELECT  Depart,COUNT(*) as count \n" +
                   "FROM flights \n" +
                   "GROUP BY Depart";

            preparedStatement = connection.prepareStatement(query);
           rs=preparedStatement.executeQuery();
           while(rs.next()){
               String destination = rs.getString("Depart");
               int countDest=rs.getInt("count");
               series2.getData().add(new XYChart.Data<>(destination,countDest));
           }

            dest_departChart.getData().addAll(series1,series2);

       } catch (SQLException  e) {
            throw new RuntimeException(e);
        }

    }

    public void showFlightPriceChart() {

        DataBaseConnection dbconnection = new DataBaseConnection();
        try {
            connection = dbconnection.getConnection();

            String query = "SELECT Depart, Destination, AVG(Price) AS AveragePrice " +
                    "FROM flights " +
                    "GROUP BY Depart, Destination";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();

            XYChart.Series<String, Double> series = new XYChart.Series<>();

            while (rs.next()) {
                String depart = rs.getString("Depart");
                String destination = rs.getString("Destination");
                double averagePrice = rs.getDouble("AveragePrice");
                if(averagePrice>0) {
                    String route = depart + "-" + destination;
                    series.getData().add(new XYChart.Data<>(route, averagePrice));
                }
            }

            chart.getData().add(series);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // user card lenna
    public void showUserCard(){
        DataBaseConnection dbconnection = new DataBaseConnection();
        try {
            connection = dbconnection.getConnection();

            String query = "SELECT COUNT(*) as count FROM users";
            String query2="SELECT COUNT(DISTINCT iduser) AS count FROM ticket";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();

            preparedStatement = connection.prepareStatement(query2);
            ResultSet rs2 = preparedStatement.executeQuery();

            String totalus="DB error";
            String ticketBuy="DB error";
            if(rs.next()){
                totalus=String.valueOf(rs.getInt("count"));
            }

            if(rs2.next()) {
                ticketBuy= String.valueOf(rs2.getInt("count"));
            }
            totalUsers.setText(totalus);
            ticketBuyers.setText(ticketBuy);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}




