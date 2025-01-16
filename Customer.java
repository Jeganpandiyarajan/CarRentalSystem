package org.example;
import java.sql.*;
import java.util.*;
public class Customer {
    private int customerId;
    private String phone;
    private String name;
    private String idProof;

    public Customer(String phone, String name, String idProof){
        this.phone = phone;
        this.name = name;
        this.idProof = idProof;
    }
    Customer(String phone){
        this.phone = phone;
    }

    public String getPhone(){
        return phone;
    }

    public static void setCustomerDetails(String phone){
        Scanner sc = new Scanner(System.in);
        String name,idProof;
        System.out.println("Enter Your Name: ");
        name = sc.next();
        System.out.println("Enter Your ID Proof: (Aadhaar Number)");
        idProof = sc.next();
        addCustomerDetails(phone,name,idProof);
        new Customer(phone,name,idProof);
    }

    //Data Access
    public static int getCustomerId(String ph){
        int id;
        String query = "select customer_id from Customers where phone =" + ph ;
        Connection con = DbConnection.getConnection();
        try {
            Statement st = con.createStatement();
            ResultSet r = st.executeQuery(query);
            r.next();
            id = r.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public static boolean isCustomerExist(String ph){
        String query = "select * from Customers where phone =" +ph;
        Connection con = DbConnection.getConnection();
        try {
            Statement st = con.createStatement();
            ResultSet r = st.executeQuery(query);
            return r.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addCustomerDetails(String ph, String name, String idProof) {
        String query = "insert into customers(customer_name,phone,id_proof) values(?,?,?)";
        Connection con = DbConnection.getConnection();
        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1,name);
            pst.setString(2,ph);
            pst.setString(3,idProof);
            pst.executeUpdate();
            System.out.println("Your Details is Recorded");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
