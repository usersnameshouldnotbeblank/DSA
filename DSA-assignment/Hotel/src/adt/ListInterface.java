/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package adt;

import java.util.Iterator;

/**
 *
 * @author jlohz， KaoYongFeng
 *
 * NOTE (Loyalty & Reward module contribution):
 * Two new operations were added to the team's shared List ADT so that every
 * module can search and traverse the list more effectively without relying
 * on any java.util.Collections classes:
 *   - indexOf(T anEntry): returns the 1-based position of an entry (used by
 *     the Loyalty module to update a member's record after modifying it).
 *   - getIterator(): returns a java.util.Iterator so client code can safely
 *     traverse the list without exposing the internal Node structure
 *     (java.util.Iterator is permitted per the assignment Q&A, section 1.1.7).
 * All existing operations/signatures used by other modules are unchanged.
 */
public interface ListInterface<T> {

    boolean add(T newEntry);

    boolean add(int position, T newEntry);

    T remove(int position);

    T getEntry(int position);

    boolean replace(int position, T newEntry);

    boolean contains(T anEntry);

    boolean isEmpty();

    int getNumberOfEntries();

    void clear();

    void printList();

    // ---------- New operations (Loyalty & Reward module contribution) ----------

    /**
     * Returns the 1-based position of the first occurrence of anEntry, or -1
     * if the entry is not found. Relies on T.equals(), same as contains().
     */
    int indexOf(T anEntry);

    /**
     * Returns a java.util.Iterator over the entries in this list, from first
     * to last, allowing client code to traverse the list without needing
     * positional getEntry() calls.
     */
    Iterator<T> getIterator();

}
