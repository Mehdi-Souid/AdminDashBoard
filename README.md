# Admin Dashboard: Travel Agency Back Office

A JavaFX desktop dashboard for the administrators of a travel agency. It manages flights, tickets, users and customer reviews stored in MySQL. Companion app to [Travel-agency-App](https://github.com/Mehdi-Souid/Travel-agency-App). Built as a school project.

## Features

- **Main dashboard:** key figures and charts (users, travellers, popular routes, tickets per day)
- **Flights:** add, update and delete flights
- **Tickets:** add, update and delete tickets
- **Users:** view, update and delete user accounts
- **Reviews:** read customer feedback

## Tech Stack

Java · JavaFX (FXML, CSS, charts) · MySQL (JDBC) · Maven

## Getting Started

1. Create the MySQL database `tirflayen` (same schema as Travel-agency-App).
2. Check the connection settings in `src/main/java/com/example/admindashboard/DataBaseConnection.java`.
3. Run:

```bash
./mvnw clean javafx:run         # Windows: mvnw.cmd clean javafx:run
```

## Structure

```
src/main/java/com/example/admindashboard/       Controllers (Main, Flights, Tickets, Users, Reviews), models, DB connection
src/main/resources/com/example/admindashboard/  FXML views, styles and chart styles
```
