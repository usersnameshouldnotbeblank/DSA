package control;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;

import DAO.LoyaltyDAO;
import adt.DoublyLinkedList;
import adt.ListInterface;
import entity.Member;
import entity.Member.LoyaltyTier;
import entity.PointsTransaction;
import entity.RewardItem;

/**
 * Control class for the Loyalty & Reward Service module.
 * Handles: earning points, redeeming rewards, automatic tier
 * upgrade/downgrade, member search (by ID/name/tier) and generating a
 * ranked loyalty report.
 *
 * Sorting and searching algorithms are self-implemented (insertion sort,
 * binary search, linear search) - no java.util.Collections / Arrays.sort
 * is used, per assignment requirements.
 */
public class LoyaltyControl {

    private ListInterface<Member> memberList;
    private ListInterface<RewardItem> rewardCatalog;
    private ListInterface<PointsTransaction> transactionList;
    private LoyaltyDAO loyaltyDAO = new LoyaltyDAO();

    // Points thresholds that trigger an automatic tier change
    private static final int PLATINUM_THRESHOLD = 1000;
    private static final int DIAMOND_THRESHOLD = 3000;
    private static final int ELITE_THRESHOLD = 6000;

    // Points earned expire this many months after the date they were earned
    private static final int POINTS_VALIDITY_MONTHS = 12;
    // default look-ahead window (in days) for the expiry alert
    public static final int DEFAULT_EXPIRY_ALERT_DAYS = 30;

    public LoyaltyControl() {
        memberList = loyaltyDAO.initializeMemberData();
        rewardCatalog = loyaltyDAO.initializeRewardCatalog();
        transactionList = loyaltyDAO.initializeTransactionData();
    }

    // =========================================================
    // Use Case 1: Earn Loyalty Points
    // =========================================================
    public String earnPoints(int memberID, int pointsEarned) {
        if (pointsEarned <= 0) {
            return "Points earned must be a positive value.";
        }
        Member member = findMemberByID(memberID);
        if (member == null) {
            return "Member with ID " + memberID + " not found.";
        }

        member.setLoyaltyPoints(member.getLoyaltyPoints() + pointsEarned);
        String tierMessage = updateTier(member);
        syncMember(member);

        // record this batch of points as its own transaction, with its own expiry date
        LocalDate earnedDate = LocalDate.now();
        LocalDate expiryDate = earnedDate.plusMonths(POINTS_VALIDITY_MONTHS);
        transactionList.add(new PointsTransaction(member.getMemberID(), member.getMemberName(),
                pointsEarned, earnedDate, expiryDate));

        StringBuilder sb = new StringBuilder();
        sb.append(member.getMemberName()).append(" earned ").append(pointsEarned)
          .append(" points. New balance: ").append(member.getLoyaltyPoints()).append(" points.");
        if (!tierMessage.isEmpty()) {
            sb.append("\n").append(tierMessage);
        }
        return sb.toString();
    }

    // =========================================================
    // Use Case 2: Redeem Reward
    // =========================================================
    public String redeemReward(int memberID, int rewardID) {
        Member member = findMemberByID(memberID);
        if (member == null) {
            return "Member with ID " + memberID + " not found.";
        }
        RewardItem reward = findRewardByID(rewardID);
        if (reward == null) {
            return "Reward with ID " + rewardID + " not found in catalog.";
        }
        if (member.getLoyaltyPoints() < reward.getPointsRequired()) {
            return member.getMemberName() + " has insufficient points to redeem \"" +
                    reward.getRewardName() + "\" (needs " + reward.getPointsRequired() +
                    ", has " + member.getLoyaltyPoints() + ").";
        }

        member.setLoyaltyPoints(member.getLoyaltyPoints() - reward.getPointsRequired());
        String tierMessage = updateTier(member);
        syncMember(member);

        StringBuilder sb = new StringBuilder();
        sb.append(member.getMemberName()).append(" redeemed \"").append(reward.getRewardName())
          .append("\" for ").append(reward.getPointsRequired()).append(" points. Remaining balance: ")
          .append(member.getLoyaltyPoints()).append(" points.");
        if (!tierMessage.isEmpty()) {
            sb.append("\n").append(tierMessage);
        }
        return sb.toString();
    }

    // =========================================================
    // Use Case 3: Automatic Tier Upgrade / Downgrade
    // =========================================================
    private String updateTier(Member member) {
        LoyaltyTier oldTier = member.getLoyaltyTier();
        LoyaltyTier newTier;
        int points = member.getLoyaltyPoints();

        if (points >= ELITE_THRESHOLD) {
            newTier = LoyaltyTier.Elite;
        } else if (points >= DIAMOND_THRESHOLD) {
            newTier = LoyaltyTier.Diamond;
        } else if (points >= PLATINUM_THRESHOLD) {
            newTier = LoyaltyTier.Platinum;
        } else {
            newTier = LoyaltyTier.Regular;
        }

        if (newTier != oldTier) {
            member.setLoyaltyTier(newTier);
            if (newTier.ordinal() > oldTier.ordinal()) {
                return "Congratulations! " + member.getMemberName() + " has been upgraded from "
                        + oldTier + " to " + newTier + "!";
            } else {
                return member.getMemberName() + " has been moved down from "
                        + oldTier + " to " + newTier + ".";
            }
        }
        return "";
    }

    // writes the (mutated) member object back into memberList at its position
    private void syncMember(Member member) {
        int pos = memberList.indexOf(member);
        if (pos != -1) {
            memberList.replace(pos, member);
        }
    }

    // =========================================================
    // Use Case 4: Search Member (by ID / Name / Tier)
    // =========================================================

    public Member searchMemberByID(int memberID) {
        return findMemberByID(memberID);
    }

    // linear search, partial case-insensitive match on name
    public ListInterface<Member> searchMemberByName(String nameKeyword) {
        ListInterface<Member> results = new DoublyLinkedList<>();
        Iterator<Member> it = memberList.getIterator();
        while (it.hasNext()) {
            Member m = it.next();
            if (m.getMemberName().toLowerCase().contains(nameKeyword.toLowerCase())) {
                results.add(m);
            }
        }
        return results;
    }

    // linear search, exact match on loyalty tier
    public ListInterface<Member> searchMemberByTier(LoyaltyTier tier) {
        ListInterface<Member> results = new DoublyLinkedList<>();
        Iterator<Member> it = memberList.getIterator();
        while (it.hasNext()) {
            Member m = it.next();
            if (m.getLoyaltyTier() == tier) {
                results.add(m);
            }
        }
        return results;
    }

    // binary search on a member-ID-sorted copy of the list
    private Member findMemberByID(int memberID) {
        ListInterface<Member> sortedCopy = copyList(memberList);
        insertionSortByID(sortedCopy);

        int low = 1;
        int high = sortedCopy.getNumberOfEntries();
        while (low <= high) {
            int mid = (low + high) / 2;
            Member midMember = sortedCopy.getEntry(mid);
            if (midMember.getMemberID() == memberID) {
                // return the actual object reference from the live list, not the copy
                int livePos = memberList.indexOf(midMember);
                return livePos != -1 ? memberList.getEntry(livePos) : midMember;
            } else if (midMember.getMemberID() < memberID) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }

    // catalog is small - linear search is sufficient
    private RewardItem findRewardByID(int rewardID) {
        int position = findRewardPosition(rewardID);
        return position == -1 ? null : rewardCatalog.getEntry(position);
    }

    // =========================================================
    // Self-implemented sorting algorithms (no Collections.sort / Arrays.sort)
    // =========================================================

    private void insertionSortByID(ListInterface<Member> list) {
        int n = list.getNumberOfEntries();
        for (int i = 2; i <= n; i++) {
            Member key = list.getEntry(i);
            int j = i - 1;
            while (j >= 1 && list.getEntry(j).getMemberID() > key.getMemberID()) {
                list.replace(j + 1, list.getEntry(j));
                j--;
            }
            list.replace(j + 1, key);
        }
    }

    private void insertionSortByPointsDescending(ListInterface<Member> list) {
        int n = list.getNumberOfEntries();
        for (int i = 2; i <= n; i++) {
            Member key = list.getEntry(i);
            int j = i - 1;
            while (j >= 1 && list.getEntry(j).getLoyaltyPoints() < key.getLoyaltyPoints()) {
                list.replace(j + 1, list.getEntry(j));
                j--;
            }
            list.replace(j + 1, key);
        }
    }

    private ListInterface<Member> copyList(ListInterface<Member> source) {
        ListInterface<Member> copy = new DoublyLinkedList<>();
        Iterator<Member> it = source.getIterator();
        while (it.hasNext()) {
            copy.add(it.next());
        }
        return copy;
    }

    // =========================================================
    // Use Case 5: Generate Loyalty Report (ranked by points)
    // =========================================================
    public String generateLoyaltyReport() {
        ListInterface<Member> sortedList = copyList(memberList);
        insertionSortByPointsDescending(sortedList);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy, hh:mm a");
        String generatedAt = LocalDateTime.now().format(formatter);

        StringBuilder sb = new StringBuilder();
        sb.append("=============================================================\n");
        sb.append("          TARUMT RESORTS - LOYALTY & REWARD PROGRAM\n");
        sb.append("               MEMBER RANKING REPORT (BY POINTS)\n");
        sb.append("-------------------------------------------------------------\n");
        sb.append("Generated at: ").append(generatedAt).append("\n");
        sb.append("=============================================================\n");
        sb.append(String.format("%-5s %-10s %-20s %-12s %-10s%n", "Rank", "Member ID", "Member Name", "Tier", "Points"));
        sb.append("-------------------------------------------------------------\n");

        int total = sortedList.getNumberOfEntries();
        for (int i = 1; i <= total; i++) {
            Member m = sortedList.getEntry(i);
            sb.append(String.format("%-5d %-10d %-20s %-12s %-10d%n",
                    i, m.getMemberID(), m.getMemberName(), m.getLoyaltyTier(), m.getLoyaltyPoints()));
        }
        sb.append("-------------------------------------------------------------\n");
        sb.append("Total members displayed: ").append(total).append("\n");
        sb.append("=============================================================\n");
        return sb.toString();
    }

    // =========================================================
    // Use Case 6: Generate Tier Distribution Report
    // =========================================================
    public String generateTierDistributionReport() {
        // count members, and accumulate total points, per loyalty tier
        LoyaltyTier[] tiers = LoyaltyTier.values();
        int[] memberCount = new int[tiers.length];
        int[] totalPoints = new int[tiers.length];

        Iterator<Member> it = memberList.getIterator();
        while (it.hasNext()) {
            Member m = it.next();
            int tierIndex = m.getLoyaltyTier().ordinal();
            memberCount[tierIndex]++;
            totalPoints[tierIndex] += m.getLoyaltyPoints();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy, hh:mm a");
        String generatedAt = LocalDateTime.now().format(formatter);
        int totalMembers = memberList.getNumberOfEntries();

        StringBuilder sb = new StringBuilder();
        sb.append("=============================================================\n");
        sb.append("          TARUMT RESORTS - LOYALTY & REWARD PROGRAM\n");
        sb.append("              TIER DISTRIBUTION SUMMARY REPORT\n");
        sb.append("-------------------------------------------------------------\n");
        sb.append("Generated at: ").append(generatedAt).append("\n");
        sb.append("=============================================================\n");
        sb.append(String.format("%-12s %-14s %-16s %-12s%n", "Tier", "No. Members", "Total Points", "Avg Points"));
        sb.append("-------------------------------------------------------------\n");

        for (int i = tiers.length - 1; i >= 0; i--) { // display highest tier (Elite) first
            int count = memberCount[i];
            int avg = count == 0 ? 0 : totalPoints[i] / count;
            sb.append(String.format("%-12s %-14d %-16d %-12d%n", tiers[i], count, totalPoints[i], avg));
        }

        sb.append("-------------------------------------------------------------\n");
        sb.append("Total members in program: ").append(totalMembers).append("\n");
        sb.append("=============================================================\n");
        return sb.toString();
    }

    // =========================================================
    // Use Case 7: Points Expiry Notifications / Alert
    // =========================================================

    /**
     * Returns the number of points transactions expiring within the given
     * number of days from today. Used to show a quick alert banner when the
     * module starts, without needing to build the full report.
     */
    public int getExpiringTransactionCount(int daysThreshold) {
        int count = 0;
        LocalDate today = LocalDate.now();
        Iterator<PointsTransaction> it = transactionList.getIterator();
        while (it.hasNext()) {
            PointsTransaction t = it.next();
            long daysLeft = ChronoUnit.DAYS.between(today, t.getExpiryDate());
            if (daysLeft >= 0 && daysLeft <= daysThreshold) {
                count++;
            }
        }
        return count;
    }

    // insertion sort on transactions by soonest expiry date first
    private void insertionSortByExpiryDate(ListInterface<PointsTransaction> list) {
        int n = list.getNumberOfEntries();
        for (int i = 2; i <= n; i++) {
            PointsTransaction key = list.getEntry(i);
            int j = i - 1;
            while (j >= 1 && list.getEntry(j).getExpiryDate().isAfter(key.getExpiryDate())) {
                list.replace(j + 1, list.getEntry(j));
                j--;
            }
            list.replace(j + 1, key);
        }
    }

    public String generateExpiryAlertReport(int daysThreshold) {
        LocalDate today = LocalDate.now();

        // collect only the transactions expiring within the threshold
        ListInterface<PointsTransaction> expiringList = new DoublyLinkedList<>();
        Iterator<PointsTransaction> it = transactionList.getIterator();
        while (it.hasNext()) {
            PointsTransaction t = it.next();
            long daysLeft = ChronoUnit.DAYS.between(today, t.getExpiryDate());
            if (daysLeft >= 0 && daysLeft <= daysThreshold) {
                expiringList.add(t);
            }
        }
        insertionSortByExpiryDate(expiringList); // soonest-expiring first

        return buildTransactionReport(expiringList,
                "POINTS EXPIRY ALERT (Next " + daysThreshold + " Days)",
                "No points are expiring within the next " + daysThreshold + " days.");
    }

    /**
     * Returns every recorded points transaction, regardless of expiry date,
     * sorted with the soonest-expiring transaction first. Useful to verify
     * that all earned-points batches (including ones far from expiring) are
     * being tracked correctly.
     */
    public String generateAllTransactionsReport() {
        ListInterface<PointsTransaction> allTransactions = copyTransactionList(transactionList);
        insertionSortByExpiryDate(allTransactions);

        return buildTransactionReport(allTransactions,
                "ALL POINTS TRANSACTIONS (Full History)",
                "No points transactions have been recorded yet.");
    }

    private ListInterface<PointsTransaction> copyTransactionList(ListInterface<PointsTransaction> source) {
        ListInterface<PointsTransaction> copy = new DoublyLinkedList<>();
        Iterator<PointsTransaction> it = source.getIterator();
        while (it.hasNext()) {
            copy.add(it.next());
        }
        return copy;
    }

    // shared formatting logic for both the expiry-alert report and the full-history report
    private String buildTransactionReport(ListInterface<PointsTransaction> list, String subtitle, String emptyMessage) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter headerFormatter = DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy, hh:mm a");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String generatedAt = LocalDateTime.now().format(headerFormatter);

        StringBuilder sb = new StringBuilder();
        sb.append("=============================================================\n");
        sb.append("          TARUMT RESORTS - LOYALTY & REWARD PROGRAM\n");
        sb.append("        ").append(subtitle).append("\n");
        sb.append("-------------------------------------------------------------\n");
        sb.append("Generated at: ").append(generatedAt).append("\n");
        sb.append("=============================================================\n");
        sb.append(String.format("%-10s %-18s %-8s %-13s %-13s %-10s%n",
                "MemberID", "Member Name", "Points", "Earned Date", "Expires On", "Days Left"));
        sb.append("-------------------------------------------------------------\n");

        int total = list.getNumberOfEntries();
        for (int i = 1; i <= total; i++) {
            PointsTransaction t = list.getEntry(i);
            long daysLeft = ChronoUnit.DAYS.between(today, t.getExpiryDate());
            sb.append(String.format("%-10d %-18s %-8d %-13s %-13s %-10d%n",
                    t.getMemberID(), t.getMemberName(), t.getPointsEarned(),
                    t.getEarnedDate().format(dateFormatter), t.getExpiryDate().format(dateFormatter), daysLeft));
        }

        sb.append("-------------------------------------------------------------\n");
        if (total == 0) {
            sb.append(emptyMessage).append("\n");
        } else {
            sb.append("Total transactions: ").append(total).append("\n");
        }
        sb.append("=============================================================\n");
        return sb.toString();
    }

    // =========================================================
    // Reward Catalog Management (CRUD)
    // =========================================================

    // ---- Create ----
    public String addRewardItem(String rewardName, String description, int pointsRequired) {
        if (rewardName == null || rewardName.trim().isEmpty()) {
            return "Reward name cannot be empty.";
        }
        if (pointsRequired <= 0) {
            return "Points required must be a positive value.";
        }
        RewardItem newReward = new RewardItem(rewardName, description, pointsRequired);
        rewardCatalog.add(newReward);
        return "New reward added: " + newReward.getRewardName() +
                " (ID " + newReward.getRewardID() + ", " + newReward.getPointsRequired() + " points).";
    }

    // ---- Update ----
    public String updateRewardItem(int rewardID, String newName, String newDescription, int newPointsRequired) {
        int position = findRewardPosition(rewardID);
        if (position == -1) {
            return "Reward with ID " + rewardID + " not found in catalog.";
        }
        if (newPointsRequired <= 0) {
            return "Points required must be a positive value.";
        }
        RewardItem existingReward = rewardCatalog.getEntry(position);
        existingReward.setRewardName(newName);
        existingReward.setDescription(newDescription);
        existingReward.setPointsRequired(newPointsRequired);
        rewardCatalog.replace(position, existingReward);
        return "Reward ID " + rewardID + " updated successfully.";
    }

    // ---- Delete ----
    public String deleteRewardItem(int rewardID) {
        int position = findRewardPosition(rewardID);
        if (position == -1) {
            return "Reward with ID " + rewardID + " not found in catalog.";
        }
        RewardItem removed = rewardCatalog.remove(position);
        return "Reward removed: " + removed.getRewardName() + " (ID " + removed.getRewardID() + ").";
    }

    // helper: linear search for a reward's ADT position by ID
    private int findRewardPosition(int rewardID) {
        for (int i = 1; i <= rewardCatalog.getNumberOfEntries(); i++) {
            if (rewardCatalog.getEntry(i).getRewardID() == rewardID) {
                return i;
            }
        }
        return -1;
    }

    // =========================================================
    // Reward Catalog Display (Read)
    // =========================================================
    public String displayRewardCatalog() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================\n");
        sb.append("                    REWARD CATALOG\n");
        sb.append("=========================================================\n");
        sb.append(String.format("%-4s %-25s %-15s%n", "ID", "Reward Name", "Points Needed"));
        sb.append("---------------------------------------------------------\n");

        Iterator<RewardItem> it = rewardCatalog.getIterator();
        while (it.hasNext()) {
            RewardItem r = it.next();
            sb.append(String.format("%-4d %-25s %-15d%n", r.getRewardID(), r.getRewardName(), r.getPointsRequired()));
        }
        sb.append("=========================================================\n");
        return sb.toString();
    }

    public ListInterface<Member> getMemberList() {
        return memberList;
    }
}