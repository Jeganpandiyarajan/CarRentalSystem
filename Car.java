package org.example;

import java.sql.*;

public class Car {
    private int carId;
    private String brand;
    private String model;
    private int year;
    private double pricePerDay;
    private boolean availability;

    public Car(String brand, String model, int year,double price) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        pricePerDay = price;
    }

    public String getBrand(){
        return brand;
    }
    public String getModel(){
        return model;
    }
    public int getYear(){
        return year;
    }
    public double getPricePerDay(){
        return pricePerDay;
    }
    public boolean getAvailability(){
        return availability;
    }

    //DataAccess
    public static void displayCarDetails(boolean getStatus){
        String query = "select * from cars where availability = " + getStatus;
        Connection con = DbConnection.getConnection();
        try {
            Statement st = con.createStatement();
            ResultSet r = st.executeQuery(query);
            System.out.println("---------Available Cars----------");
            while(r.next()){
                System.out.println("CarId:" + r.getInt(1) +
                        " | Brand:" + r.getString(2) +
                        " | Model:" + r.getString(3) +
                        " | Year:" + r.getInt(4) +
                        " | PricePerDay: " + r.getDouble(6)
                );
            }
            System.out.println("_________________________________________________");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isCarIdExist(int id){
        String query = "select * from cars where car_id = " + id;
        Connection con = DbConnection.getConnection();
        try {
            Statement st = con.createStatement();
            ResultSet r = st.executeQuery(query);
            return r.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isCarAvailable(int id){
        String query = "select availability from cars where car_id = " + id;
        Connection con = DbConnection.getConnection();
        try {
            Statement st = con.createStatement();
            ResultSet r = st.executeQuery(query);
            r.next();
            return r.getBoolean(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static double getCarPricePerDay(int id){
        String query = "select price_per_day from cars where car_id = " + id;
        Connection con = DbConnection.getConnection();
        try {
            Statement st = con.createStatement();
            ResultSet r = st.executeQuery(query);
            if(r.next()) {
                return r.getDouble(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0.0;
    }

    public static void updateCarAvailability(int carId, boolean isAvailable) {
        String query = "UPDATE cars SET availability = ? WHERE car_id = ?";
        Connection con = DbConnection.getConnection();
        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setBoolean(1, isAvailable);
            pst.setInt(2, carId);
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

