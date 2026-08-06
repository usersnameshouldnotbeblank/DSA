package control;

import java.util.Objects;

import DAO.RegistrationDAO;
import adt.DoublyLinkedList;
import adt.ListInterface;
import entity.Member;
import entity.Room;

public class RegistrationControl {
    // Control classes implement the business logic for use cases.
    // They orchestrate the execution of commands coming from boundary objects 
    // by interacting with entity and boundary objects.

    // this is control class for registration module

    // for RegistrationControl, main use is to handle registration process
    // main tasks is registration of new customers, and check-in/check-out of existing customers

    private ListInterface<Member> memberList = new DoublyLinkedList<>(); // list of known members
    private ListInterface<Room> roomList = new DoublyLinkedList<>(); // list of known rooms

    private ListInterface<Member> checkInWaitlist = new DoublyLinkedList<>(); // list of customers waiting to be checked in
    private ListInterface<Member> checkOutWaitlist = new DoublyLinkedList<>(); // list of customers waiting to be checked out

    











    // methods methods methods methods methods
    // methods methods methods methods methods
    // methods methods methods methods methods
    // methods methods methods methods methods
    // methods methods methods methods methods
    // methods methods methods methods methods

    // customer registration into a waitlist
    public Member registerNewCustomer(String memberName, String phoneNumber, String email) {
        // logic to register new customer

        // step 1 make sure customer is not already registered as member
        // catch cases where member exists already, and return 2 if so
        if (!memberList.isEmpty()) { // list is not empty, check if customer is already registered
            for (int i = 1; i <= memberList.getNumberOfEntries(); i++) {
                Member existingMember = memberList.getEntry(i);
                if (Objects.equals(existingMember.getMemberName(), memberName) &&
                    Objects.equals(existingMember.getPhoneNumber(), phoneNumber) &&
                    Objects.equals(existingMember.getEmail(), email)) {
                        // customer exists inside memberList
                    
                        System.out.println("Welcome back, " + memberName + "!");
                        return existingMember; // return existing member object
                }
            }
        }
        // empty list or not found will arrive here, register new customer
        Member newMember = new Member(memberName, phoneNumber, email);
        memberList.add(newMember);
        System.out.println("New customer registered: " + memberName);
        // customer is registered into member list
        return newMember; // return new member object
    }

    // add existing customer to check-in waitlist
    public void enqueueCheckin(Member member) {
        // logic to add existing customer to check-in waitlist
        // need to traverse waitlist so that higher tier member recieves priority in check-in

        if (checkInWaitlist.isEmpty() || member.getLoyaltyTier() == Member.LoyaltyTier.Regular) {
            checkInWaitlist.add(member); // adt add
            System.out.println("Member added to queue at position " + memberList.getNumberOfEntries());
        } else {
            simulateEnqueue(checkInWaitlist, member);
        }
    }

    // add existing customer to check-out waitlist
    public void enqueueCheckout(Member member) {
        // logic to add existing customer to check-out waitlist
        // need to traverse waitlist so that higher tier member recieves priority in check-out

        if (checkOutWaitlist.isEmpty() || member.getLoyaltyTier() == Member.LoyaltyTier.Regular) {
            checkOutWaitlist.add(member);
            System.out.println("Member added to queue at position " + memberList.getNumberOfEntries());
        } else {
            simulateEnqueue(checkOutWaitlist, member);
        }
    }

    public Member dequeueCheckin(){
        return simulateDequeue(checkInWaitlist);
    }































    // helper function helper function helper function helper function helper function helper function
    // helper function helper function helper function helper function helper function helper function
    // helper function helper function helper function helper function helper function helper function
    // helper function helper function helper function helper function helper function helper function
    // helper function helper function helper function helper function helper function helper function



    private void simulateEnqueue(ListInterface<Member> waitlist, Member member) {
        // logic to add member to waitlist based on loyalty tier
        // higher tier members are added to the front of the list
        // lower tier members are added to the back of the list
        int newMemberTier = member.getLoyaltyTier().ordinal(); // get the ordinal value of the new member's loyalty tier
        int position = 1; // default position to add new member
        int totalEntries = waitlist.getNumberOfEntries(); // get the total number of entries in the waitlist
        
        while (position <= totalEntries) {
            Member currentMember = waitlist.getEntry(position);
            int existingMemberTier = currentMember.getLoyaltyTier().ordinal();
            if (newMemberTier > existingMemberTier) {
                break;
            }
            position++;
        }
        waitlist.add(position, member); // adt add at position
        System.out.println("Member added to queue at position: " + position);
    }

    
    private Member simulateDequeue(ListInterface<Member>waitlist){
        // logic for removing member from queue
        if (!waitlist.isEmpty()){
            return waitlist.remove(1); // adt remove
        }
        return null;
    }    
}

