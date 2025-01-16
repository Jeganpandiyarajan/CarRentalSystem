package org.example;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Rental {
    static Date rentalDate;
    static Date returnDate;
    static int carId;
    static Customer customer;


    public static void rentalCar(){


        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Your Phone Number: ");
        String phone = sc.next();

        if(Customer.isCustomerExist(phone)) {
            customer = new Customer(phone);
            System.out.println("Your Details Already Exists");
        }
        else {
            Customer.setCustomerDetails(phone);
            customer = new Customer(phone);
        }

        String ph = customer.getPhone();
        int customerId = Customer.getCustomerId(ph);

        System.out.println("Enter Car ID to Rental:");
        carId = sc.nextInt();
        boolean flag = true;
        while(flag) {
            if (!Car.isCarIdExist(carId)){
                System.out.println("Please Enter Valid Car Id...");
                Car.displayCarDetails(true);
                carId = sc.nextInt();
            }
            else if (!Car.isCarAvailable(carId)) {
                System.out.println("Car is Already Rented.. Please Enter Available Car");
                Car.displayCarDetails(true);
                carId = sc.nextInt();
            }
            else
                flag = false;
        }

        System.out.println("Enter Rental Date (dd-MM-yyyy)");
        String rental_date = sc.next();

        System.out.println("Enter Return Date (dd-MM-yyyy)");
        String return_date = sc.next();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            rentalDate = dateFormat.parse(rental_date);
            returnDate = dateFormat.parse(return_date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        long numberOfDays = calculateDays(rentalDate,returnDate);
        double pricePerDay = Car.getCarPricePerDay(carId);
        double totalAmount = numberOfDays * pricePerDay;

        System.out.println("__________________________________________");
        System.out.println("Rental Duration: " + numberOfDays + " days");
        System.out.println("Price Per Day: ₹" + pricePerDay);
        System.out.println("Total Rental Amount: ₹" + totalAmount);
        System.out.println("__________________________________________");

        // Payment process
        System.out.println("Enter Payment Amount to Confirm Rental (₹" + totalAmount + "): ");
        double payment = sc.nextDouble();
        if(payment >= totalAmount) {
            System.out.println("Payment Successful!. \nNote:If Return Date  Delayed You Should Pay Extra Charge..");
        }
        else {
            System.out.println("Insufficient Payment! Car Rental Failed.");
        }

        if(getConfirmation()) {
            System.out.println("-------Car has been Successfully Rented-------");
            Car.updateCarAvailability(carId, false);
            addRentalDetails(customerId, carId, rentalDate, returnDate);
        }
        else{
            System.out.println("Car Rental process Cancelled.");
        }


    }


    public static void returnCar() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your Phone Number:");
        String phone = sc.next();

        int customerId = Customer.getCustomerId(phone);
        int carId = Rental.getCarId(customerId);
        int rentalId = Rental.getRentalId(customerId);

        if (rentalId == -1) {
            System.out.println("No Rental found for this Number.");
            return;
        }

        double lateFee = calculateLateFee(rentalId);
        if (lateFee > 0) {
            System.out.println("Late Fee: ₹" + lateFee);
        }

        if(getConfirmation()){
            updateRentalStatus(rentalId,"Returned");
            Car.updateCarAvailability(carId, true);
            System.out.println("-------Car Has Been Successfully Return-------");
        }
        else{
            System.out.println("Car Return Process Cancelled.");
        }
    }

    public static long calculateDays(Date rentalDate, Date returnDate) {
        long diffInMilliSec = Math.abs(returnDate.getTime() - rentalDate.getTime());
        return TimeUnit.DAYS.convert(diffInMilliSec, TimeUnit.MILLISECONDS);
    }

    public static void cancelRentalCar() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Your Registered Phone Number:");
        String phone = sc.next();
        int customerId = Customer.getCustomerId(phone);
        int carId = Rental.getCarId(customerId);
        int rentalId = Rental.getRentalId(customerId);
        if (rentalId == -1) {
            System.out.println("No Rental found for this Number.");
            return;
        }
        if(getConfirmation()) {
            updateRentalStatus(rentalId,"Cancelled");
            Car.updateCarAvailability(carId, true);
            System.out.println("Your Refund Will Be Proceed Soon..");
            System.out.println("-------Car has been Successfully Cancelled-------");
        }
        else{
            System.out.println("Cancel Process is not able to proceed. Try Again");
        }
    }


    public static boolean getConfirmation(){
        Scanner sc=new Scanner(System.in);
        System.out.println("Proceed to return the car (Enter 'yes' to confirm): ");
        String confirmation = sc.next();
        return confirmation.equalsIgnoreCase("yes");
    }

    //Data Access
    public static void addRentalDetails(int customerId, int carId, Date rentalDate, Date returnDate) {
        String query = "insert into Rentals (car_id, customer_id, rental_date, return_date, status) values (?,?,?,?,?)";
        Connection con = DbConnection.getConnection();
        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1,carId);
            pst.setInt(2,customerId);
            java.sql.Date rentalSqlDate = new java.sql.Date(rentalDate.getTime());
            pst.setDate(3,rentalSqlDate);
            java.sql.Date returnSqlDate = new java.sql.Date(returnDate.getTime());
            pst.setDate(4,returnSqlDate);
            pst.setBoolean(5,true);
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static int getRentalId(int customerId) {
        String query = "SELECT rental_id FROM Rentals WHERE customer_id = ? AND status = 'Rented'";
        Connection con = DbConnection.getConnection();
        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, customerId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("rental_id");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }


    private static int getCarId(int customerId) {
        String query = "SELECT car_id FROM Rentals WHERE customer_id = ? AND status = 'Rented'";
        Connection con = DbConnection.getConnection();
        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, customerId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("car_id");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }


    private static double calculateLateFee(int rentalId) {
        String query = "SELECT return_date, CURRENT_DATE FROM Rentals WHERE rental_id = ?";
        Connection con = DbConnection.getConnection();
        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, rentalId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Date returnDate = rs.getDate("return_date");
                Date currentDate = rs.getDate("CURRENT_DATE");

                long diffInMilliSec = currentDate.getTime() - returnDate.getTime();
                long overdueDays = TimeUnit.DAYS.convert(diffInMilliSec, TimeUnit.MILLISECONDS);

                if (overdueDays > 0) {
                    double lateFeePerDay = 500;
                    return overdueDays * lateFeePerDay;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0.0;
    }


    private static void updateRentalStatus(int rentalId,String status) {
        String query = "UPDATE Rentals SET status = ? WHERE rental_id = ?";
        Connection con = DbConnection.getConnection();
        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1,status);
            pst.setInt(2, rentalId);
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
