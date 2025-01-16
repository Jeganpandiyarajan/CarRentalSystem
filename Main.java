package org.example;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int userOption = 1;
        Car.displayCarDetails(true);
        System.out.println("---Welcome to Car Rental System---");
        while(userOption!=0){
            System.out.println("Choose Your Option...\n1. Rental \n2. Return car \n3. Cancel Rental \n0. Exit");
            System.out.print("Enter your Option:");
            userOption = sc.nextInt();
            switch (userOption){
                case 1:
                    Rental.rentalCar();
                    break;
                case 2:
                    Rental.returnCar();
                    break;
                case 3:
                    Rental.cancelRentalCar();
                    break;
                case 0:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Please Enter Valid Option");
            }
        }
    }
}
