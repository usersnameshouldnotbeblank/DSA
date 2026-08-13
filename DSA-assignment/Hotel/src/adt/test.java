// package adt;



// public class test {

//     public static void main(String[] args) {
//         System.out.println("==========================================");
//         System.out.println("   DOUBLY LINKED LIST COMPREHENSIVE TEST  ");
//         System.out.println("==========================================\n");

//         testBackHalfAddBug(); // Run the specific test for the back half add bug
//         // testIsEmptyAndSize();
//         // testAddEnd();
//         // testAddByPosition();
//         // testRemove();
//         // testGetEntry();
//         // testReplace();
//         // testContains();
//         // testClear();
//     }

//     private static void testIsEmptyAndSize() {
//         System.out.println("--- 1. Testing Initial State (isEmpty / getNumberOfEntries) ---");
//         ListInterface<String> list = new DoublyLinkedList<>();
        
//         System.out.println("Is empty (Expected: true): " + list.isEmpty());
//         System.out.println("Size (Expected: 0): " + list.getNumberOfEntries());
//         System.out.println();
//     }

//     private static void testAddEnd() {
//         System.out.println("--- 2. Testing add(T newEntry) ---");
//         ListInterface<String> list = new DoublyLinkedList<>();

//         // Test appending items
//         list.add("Apple");
//         list.add("Banana");
//         list.add("Cherry");

//         System.out.print("List content (Expected: Apple Banana Cherry): ");
//         list.printList();
//         System.out.println("Size (Expected: 3): " + list.getNumberOfEntries());
//         System.out.println("Is empty (Expected: false): " + list.isEmpty());
//         System.out.println();
//     }

//     private static void testAddByPosition() {
//         System.out.println("--- 3. Testing add(int position, T newEntry) ---");
//         ListInterface<String> list = new DoublyLinkedList<>();

//         // Edge Case: Add to empty list at position 1
//         System.out.println("Add 'B' at pos 1 (empty list): " + list.add(1, "B"));
        
//         // Edge Case: Add at position 1 (front)
//         System.out.println("Add 'A' at pos 1 (front): " + list.add(1, "A"));

//         // Edge Case: Add at end (position = size + 1)
//         System.out.println("Add 'E' at pos 3 (end): " + list.add(3, "E"));

//         // Normal Cases: Middle insertion (Traverse from front vs back)
//         System.out.println("Add 'C' at pos 3 (middle-front): " + list.add(3, "C"));
//         System.out.println("Add 'D' at pos 4 (middle-back): " + list.add(4, "D"));

//         System.out.print("Current List (Expected: A B C D E): ");
//         list.printList();

//         // Invalid Cases
//         System.out.println("Add at pos 0 (Expected: false): " + list.add(0, "Invalid"));
//         System.out.println("Add at pos -1 (Expected: false): " + list.add(-1, "Invalid"));
//         System.out.println("Add at pos 10 (Expected: false): " + list.add(10, "Invalid"));
//         System.out.println();
//     }

//     private static void testRemove() {
//         System.out.println("--- 4. Testing remove(int position) ---");
//         ListInterface<String> list = createSampleList(); // A B C D E F G
//         System.out.print("Initial List: ");
//         list.printList();

//         // Invalid Cases
//         System.out.println("Remove pos 0 (Expected: null): " + list.remove(0));
//         System.out.println("Remove pos 10 (Expected: null): " + list.remove(10));

//         // Edge Case: Remove First Node
//         System.out.println("Removed pos 1 (Expected: A): " + list.remove(1)); // B C D E F G

//         // Edge Case: Remove Last Node
//         System.out.println("Removed last pos 6 (Expected: G): " + list.remove(6)); // B C D E F

//         // Normal Case: Remove Middle (closer to front)
//         System.out.println("Removed pos 2 (Expected: C): " + list.remove(2)); // B D E F

//         // Normal Case: Remove Middle (closer to back)
//         System.out.println("Removed pos 3 (Expected: E): " + list.remove(3)); // B D F

//         System.out.print("Current List (Expected: B D F): ");
//         list.printList();

//         // Edge Case: Single Element Removal
//         ListInterface<String> singleList = new DoublyLinkedList<>();
//         singleList.add("OnlyOne");
//         System.out.println("Removed from single-node list (Expected: OnlyOne): " + singleList.remove(1));
//         System.out.println("Single list is now empty (Expected: true): " + singleList.isEmpty());
//         System.out.println();
//     }

//     private static void testGetEntry() {
//         System.out.println("--- 5. Testing getEntry(int position) ---");
//         ListInterface<String> list = createSampleList(); // A B C D E F G (7 elements)

//         System.out.println("Get pos 1 (Front - Expected: A): " + list.getEntry(1));
//         System.out.println("Get pos 3 (Front-mid - Expected: C): " + list.getEntry(3));
//         System.out.println("Get pos 5 (Back-mid - Expected: E): " + list.getEntry(5));
//         System.out.println("Get pos 7 (Back - Expected: G): " + list.getEntry(7));

//         // Invalid cases
//         System.out.println("Get pos 0 (Expected: null): " + list.getEntry(0));
//         System.out.println("Get pos 8 (Expected: null): " + list.getEntry(8));
//         System.out.println();
//     }

//     private static void testReplace() {
//         System.out.println("--- 6. Testing replace(int position, T newEntry) ---");
//         ListInterface<String> list = createSampleList(); // A B C D E F G

//         System.out.println("Replace pos 1 with 'X' (Expected: true): " + list.replace(1, "X"));
//         System.out.println("Replace pos 4 with 'Y' (Expected: true): " + list.replace(4, "Y"));
//         System.out.println("Replace pos 7 with 'Z' (Expected: true): " + list.replace(7, "Z"));

//         System.out.print("Updated List (Expected: X B C Y E F Z): ");
//         list.printList();

//         // Invalid Cases
//         System.out.println("Replace pos 0 (Expected: false): " + list.replace(0, "Fail"));
//         System.out.println("Replace pos 10 (Expected: false): " + list.replace(10, "Fail"));
//         System.out.println();
//     }

//     private static void testContains() {
//         System.out.println("--- 7. Testing contains(T anEntry) ---");
//         ListInterface<String> list = createSampleList(); // A B C D E F G

//         System.out.println("Contains 'A' (Front, Expected: true): " + list.contains("A"));
//         System.out.println("Contains 'D' (Middle, Expected: true): " + list.contains("D"));
//         System.out.println("Contains 'G' (Back, Expected: true): " + list.contains("G"));
//         System.out.println("Contains 'Z' (Non-existent, Expected: false): " + list.contains("Z"));

//         // Empty list case
//         ListInterface<String> emptyList = new DoublyLinkedList<>();
//         System.out.println("Empty list contains 'A' (Expected: false): " + emptyList.contains("A"));
//         System.out.println();
//     }

//     private static void testClear() {
//         System.out.println("--- 8. Testing clear() ---");
//         ListInterface<String> list = createSampleList();

//         System.out.println("Size before clear: " + list.getNumberOfEntries());
//         list.clear();
//         System.out.println("Size after clear (Expected: 0): " + list.getNumberOfEntries());
//         System.out.println("Is empty after clear (Expected: true): " + list.isEmpty());
//         System.out.print("List print after clear (Expected: empty line): ");
//         list.printList();
//         System.out.println();
//     }

//     // Helper method to populate list
//     private static ListInterface<String> createSampleList() {
//         ListInterface<String> list = new DoublyLinkedList<>();
//         String[] items = {"A", "B", "C", "D", "E", "F", "G"};
//         for (String item : items) {
//             list.add(item);
//         }
//         return list;
//     }

//     public static void testBackHalfAddBug() {
//     System.out.println("--- Testing add(position, entry) in Back Half ---");
//     DoublyLinkedList<String> list = new DoublyLinkedList<>();

//     // 1. Create a list with 6 elements
//     list.add("A"); // Pos 1
//     list.add("B"); // Pos 2
//     list.add("C"); // Pos 3
//     list.add("D"); // Pos 4
//     list.add("E"); // Pos 5
//     list.add("F"); // Pos 6

//     System.out.print("Original List: ");
//     list.printList(); // Output: A B C D E F

//     // 2. Insert "X" at position 5 (back half of the list)
//     // Position 5 is > numberOfEntries / 2 (5 > 3), so it triggers the back-traversal branch!
//     list.add(5, "X");

//     System.out.print("After list.add(5, \"X\"): ");
//     list.printList();

//     // 3. Verify what actually landed at position 5
//     String elementAt5 = list.getEntry(5);
//     System.out.println("Element at position 5: " + elementAt5);

//     if ("X".equals(elementAt5)) {
//         System.out.println("RESULT: SUCCESS - 'X' was correctly inserted at position 5!");
//     } else {
//         System.out.println("RESULT: BUG DETECTED! Expected 'X' at position 5, but found '" + elementAt5 + "' instead.");
//     }
// }
// }