package boundary;

import java.util.Scanner;

import control.RegistrationControl;
import entity.Member;

public class RegistrationUI {
    // boundrary class, used for user interface, to interact with the user and get input for registration process
    // UI used for registration module is here
    private RegistrationControl registrationControl = new RegistrationControl();


    // display registration menu
    public void displayRegistrationMenu() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("==========================================");
        System.out.println("   HOTEL REGISTRATION MODULE  ");
        System.out.println("==========================================\n");
        System.out.println("1. Register New Customer to queue");        // register new customer into a waitlist
        System.out.println("2. Customer Check-in");                     // tend to check in waitlist
        System.out.println("3. View checkin Waitlist");                     // view to check in waitlist
        System.out.println("4. Customer Check-out");                    // tend to check out waitlist
        System.out.println("5. View checkout Waitlist");                    // view to check out waitlist
        System.out.println("6. Exit Registration Module");
        System.out.println("==========================================\n");
        System.out.print("Please select an option (1-6): ");

        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                // register new customer into a waitlist, and check if customer is already registered as member
                System.out.println("Registering new customer...");
                registerNewCustomer();
                break;
            case 2:
                // tend to check in waitlist
                checkinProcess();
                break;
            case 3:
                // tend to check out waitlist
                checkoutProcess();
                break;
            case 4:
                // Exiting Registration Module
                System.out.println("Exiting Registration Module...");
                // Exit the registration module
                break;
            default:
                System.out.println("Invalid option. Please select a valid option (1-5).");
        }   
        scanner.close();
    } //menu

    private void registerNewCustomer() {
        // ui to register new customer
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter customer name: ");
        String name = scanner.nextLine();
        System.out.println("Enter customer phone number: ");
        String phoneNumber = scanner.nextLine();
        System.out.println("Enter customer email: ");
        String email = scanner.nextLine();
        // Call the control method to register the new customer
        Member registeredMember = registrationControl.registerNewCustomer(name, phoneNumber, email);
        // customer is now registered as member

        // ask if user wants to check in or check out
        System.out.println("1. Check-In");
        System.out.println("2. Check-Out");
        System.out.println("3. Exit");
        System.out.print("Please select an option (1-3): ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                // Check-in existing customer
                System.out.println("Queueing for check-in...");
                registrationControl.enqueueCheckin(registeredMember);
                break;
            case 2:
                // Check-out existing customer
                System.out.println("Queueing for check-out...");
                registrationControl.enqueueCheckout(registeredMember);
                break;
            case 3:
                // Exiting Registration Module
                System.out.println("Exiting Registration Module...");
                // Exit the registration module
                break;
            default:
                System.out.println("Invalid option. Please select a valid option (1-3).");
        }

        scanner.close();
    }

    // tend to check checkin waitlist
    private void checkinProcess(){
        // get member in turn of wailist
        Member currentMember = registrationControl.dequeueCheckin();
        // prompt action
        System.out.println();
    }


    private void checkoutProcess(){
        
    }

}
