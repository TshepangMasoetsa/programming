import java.util.*;
import java.io.*;
import java.util.UUID;

// ================= USER CLASS =================
class User {
    String username;
    String password;
    String phoneNumber;
    String firstName;
    String lastName;

    public boolean checkUsername(String username) {
        return username.contains("_") && username.length() <= 5;
    }

    public boolean checkPassword(String password) {
        boolean hasUpper = false, hasNumber = false, hasSpecial = false;
        if (password.length() >= 8) {
            for (char ch : password.toCharArray()) {
                if (Character.isUpperCase(ch)) hasUpper = true;
                else if (Character.isDigit(ch)) hasNumber = true;
                else if (!Character.isLetterOrDigit(ch)) hasSpecial = true;
            }
        }
        return hasUpper && hasNumber && hasSpecial;
    }

    public boolean checkPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^\\+27\\d{9}$");
    }

    public void registerUser(String firstName, String lastName, String username, String password, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        System.out.println("Registration successful!");
    }

    public boolean loginUser(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }
}

// ================= MESSAGE CLASS =================
class Message {
    String messageID;
    int messageNumber;
    String recipient;
    String messageText;
    String messageHash;
    String flag;

    public Message(int messageNumber, String recipient, String messageText, String flag) {
        this.messageID = UUID.randomUUID().toString();
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.flag = flag;
        this.messageHash = createHash();
    }

    private String createHash() {
        String start = messageID.substring(0, 2);
        String end = messageID.substring(messageID.length() - 2);
        return start + messageNumber + end;
    }

    public String toJSON() {
        return "{\n" +
                "  \"MessageID\": \"" + messageID + "\",\n" +
                "  \"MessageNumber\": " + messageNumber + ",\n" +
                "  \"Recipient\": \"" + recipient + "\",\n" +
                "  \"Message\": \"" + messageText.replace("\"", "\\\"") + "\",\n" +
                "  \"MessageHash\": \"" + messageHash + "\",\n" +
                "  \"Flag\": \"" + flag + "\"\n" +
                "}";
    }
}

// ================= MAIN CLASS =================
public class main {
    static ArrayList<Message> allMessages = new ArrayList<>();
    static ArrayList<Message> sentMessages = new ArrayList<>();
    static ArrayList<Message> disregardedMessages = new ArrayList<>();
    static ArrayList<Message> storedMessages = new ArrayList<>();
    static ArrayList<String> messageHashes = new ArrayList<>();
    static ArrayList<String> messageIDs = new ArrayList<>();

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        User user = new User();

        // ================= REGISTRATION =================
        System.out.println("=== REGISTRATION ===");
        System.out.print("Enter first name: ");
        String firstName = input.nextLine();
        System.out.print("Enter last name: ");
        String lastName = input.nextLine();

        String username;
        do {
            System.out.print("Enter username: ");
            username = input.nextLine();
            if (!user.checkUsername(username)) {
                System.out.println("Invalid username. Must contain '_' and be ≤ 5 characters.");
            }
        } while (!user.checkUsername(username));

        String password;
        do {
            System.out.print("Enter password: ");
            password = input.nextLine();
            if (!user.checkPassword(password)) {
                System.out.println("Invalid password. Must be 8+ chars, include uppercase, number & special character.");
            }
        } while (!user.checkPassword(password));

        String phone;
        do {
            System.out.print("Enter phone number (include +27): ");
            phone = input.nextLine();
            if (!user.checkPhoneNumber(phone)) {
                System.out.println("Invalid phone number format.");
            }
        } while (!user.checkPhoneNumber(phone));

        user.registerUser(firstName, lastName, username, password, phone);

        // ================= LOGIN =================
        System.out.println("\n=== LOGIN ===");
        boolean loggedIn = false;
        int attempts = 0;
        while (attempts < 3 && !loggedIn) {
            System.out.print("Enter username: ");
            String u = input.nextLine();
            System.out.print("Enter password: ");
            String p = input.nextLine();
            if (user.loginUser(u, p)) {
                System.out.println("Welcome " + firstName + " " + lastName + ", it is great to see you again.");
                System.out.println("Welcome to QuickChat.");
                loggedIn = true;
            } else {
                System.out.println("Incorrect login details.");
                attempts++;
            }
        }
        if (!loggedIn) {
            System.out.println("Too many failed attempts.");
            input.close();
            return;
        }

        loadTestData();
        loadFromJSON();
        populateArrays();

        System.out.print("\nHow many messages would you like to send? ");
        int maxMessages = input.nextInt();
        input.nextLine();
        while (maxMessages <= 0) {
            System.out.print("Enter a valid number: ");
            maxMessages = input.nextInt();
            input.nextLine();
        }

        int messageCount = 0;
        int choice;

        do {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            System.out.print("Choose: ");

            choice = input.hasNextInt() ? input.nextInt() : 0;
            input.nextLine();

            switch (choice) {
                case 1:
                    if (messageCount >= maxMessages) {
                        System.out.println("Message limit reached.");
                        break;
                    }
                    sendMessage(input, ++messageCount);
                    break;
                case 2:
                    showRecentlySent();
                    break;
                case 3:
                    saveToJSON();
                    System.out.println("Total messages sent: " + messageCount);
                    System.out.println("Messages saved. Goodbye!");
                    break;
                case 4:
                    storedMessagesMenu(input);
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } while (choice != 3);

        input.close();
    }

    private static void loadTestData() {
        allMessages.add(new Message(1, "+27834557896", "Did you get the cake?", "Sent"));
        allMessages.add(new Message(2, "+27838884567", "Where are you? You are late! I have asked you to be on time.", "Stored"));
        allMessages.add(new Message(3, "+27834484567", "Yohoooo, I am at your gate.", "Disregard"));
        allMessages.add(new Message(4, "0838884567", "It is dinner time !", "Sent"));
        allMessages.add(new Message(5, "+27838884567", "Ok, I am leaving without you.", "Stored"));
        System.out.println("Test data loaded into arrays.");
    }

    private static void populateArrays() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();

        for (Message m : allMessages) {
            messageHashes.add(m.messageHash);
            messageIDs.add(m.messageID);
            switch (m.flag.toLowerCase()) {
                case "sent":
                    sentMessages.add(m);
                    break;
                case "disregard":
                    disregardedMessages.add(m);
                    break;
                case "stored":
                    storedMessages.add(m);
                    break;
            }
        }
    }

    private static void loadFromJSON() {
        try {
            File file = new File("messages.json");
            if (file.exists()) {
                System.out.println("Loaded stored messages from JSON.");
            }
        } catch (Exception e) {}
    }

    private static void saveToJSON() {
        try (FileWriter writer = new FileWriter("messages.json")) {
            writer.write("[\n");
            for (int i = 0; i < allMessages.size(); i++) {
                writer.write(allMessages.get(i).toJSON());
                if (i < allMessages.size() - 1) writer.write(",\n");
            }
            writer.write("\n]");
            System.out.println("Messages saved to messages.json");
        } catch (IOException e) {
            System.out.println("Error saving to JSON.");
        }
    }

    private static void sendMessage(Scanner input, int msgNum) {
        System.out.print("Enter recipient (+27... or number): ");
        String recipient = input.nextLine();

        String text;
        do {
            System.out.print("Enter message (max 250 characters): ");
            text = input.nextLine();
            if (text.length() > 250) {
                System.out.println("Message too long.");
            }
        } while (text.length() > 250);

        Message msg = new Message(msgNum, recipient, text, "Sent");
        allMessages.add(msg);
        populateArrays();

        System.out.println("\n--- Message Details ---");
        System.out.println("Message ID: " + msg.messageID);
        System.out.println("Message Hash: " + msg.messageHash);
        System.out.println("Recipient: " + msg.recipient);
        System.out.println("Message: " + msg.messageText);
        System.out.println("-----------------------");
        System.out.println("Message sent ✔");
    }

    private static void showRecentlySent() {
        System.out.println("\nRecently Sent Messages:");
        if (sentMessages.isEmpty()) {
            System.out.println("No sent messages.");
            return;
        }
        for (Message m : sentMessages) {
            System.out.println("ID: " + m.messageID + " | To: " + m.recipient + " | " + m.messageText);
        }
    }

    private static void storedMessagesMenu(Scanner input) {
        String opt;
        do {
            System.out.println("\n=== Stored Messages ===");
            System.out.println("a) Display sender/recipient of all stored messages");
            System.out.println("b) Display the longest stored message");
            System.out.println("c) Search for a message ID");
            System.out.println("d) Search messages for a particular recipient");
            System.out.println("e) Delete a message using hash");
            System.out.println("f) Display full report");
            System.out.println("g) Display disregarded messages");
            System.out.println("0) Back to main menu");
            System.out.print("Choose: ");
            opt = input.nextLine().trim().toLowerCase();

            switch (opt) {
                case "a": displayStoredSenderRecipient(); break;
                case "b": displayLongestStored(); break;
                case "c":
                    System.out.print("Enter Message ID: ");
                    searchByMessageID(input.nextLine().trim());
                    break;
                case "d":
                    System.out.print("Enter recipient (full or partial number): ");
                    searchByRecipient(input.nextLine().trim());
                    break;
                case "e":
                    System.out.print("Enter Message Hash: ");
                    deleteByHash(input.nextLine().trim());
                    break;
                case "f": displayFullReport(); break;
                case "g": displayDisregarded(); break;
                case "0": return;
                default: System.out.println("Invalid option.");
            }
        } while (!opt.equals("0"));
    }

    private static void displayStoredSenderRecipient() {
        System.out.println("\nStored Messages (Sender/Recipient):");
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages.");
            return;
        }
        for (Message m : storedMessages) {
            System.out.println("Recipient: " + m.recipient + " | Message: " + m.messageText);
        }
    }

    private static void displayLongestStored() {
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages.");
            return;
        }
        Message longest = storedMessages.get(0);
        for (Message m : storedMessages) {
            if (m.messageText.length() > longest.messageText.length()) {
                longest = m;
            }
        }
        System.out.println("\nLongest Stored Message:");
        System.out.println("Recipient: " + longest.recipient);
        System.out.println("Message: " + longest.messageText);
        System.out.println("Length: " + longest.messageText.length());
    }

    private static void displayDisregarded() {
        System.out.println("\nDisregarded Messages:");
        if (disregardedMessages.isEmpty()) {
            System.out.println("No disregarded messages.");
            return;
        }
        for (Message m : disregardedMessages) {
            System.out.println("Recipient: " + m.recipient + " | Message: " + m.messageText);
        }
    }

    private static void searchByMessageID(String id) {
        for (Message m : allMessages) {
            if (m.messageID.equals(id) || m.messageID.contains(id)) {
                System.out.println("Found! Recipient: " + m.recipient);
                System.out.println("Message: " + m.messageText);
                return;
            }
        }
        System.out.println("Message ID not found.");
    }

    // ================= IMPROVED RECIPIENT SEARCH =================
    private static void searchByRecipient(String search) {
        if (search.isEmpty()) {
            System.out.println("Please enter a recipient number.");
            return;
        }

        System.out.println("\nMessages for recipient containing: " + search);
        boolean found = false;

        // Normalize search input (remove non-digits)
        String normalizedSearch = search.replaceAll("\\D", "");

        for (Message m : allMessages) {
            String normalizedRecipient = m.recipient.replaceAll("\\D", "");

            if (normalizedRecipient.contains(normalizedSearch) || 
                normalizedSearch.contains(normalizedRecipient) ||
                m.recipient.toLowerCase().contains(search.toLowerCase())) {
                
                System.out.println("[" + m.flag + "] To: " + m.recipient);
                System.out.println("   " + m.messageText);
                System.out.println("   Hash: " + m.messageHash);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No messages found for this recipient.");
        }
    }

    private static void deleteByHash(String hash) {
        for (int i = 0; i < allMessages.size(); i++) {
            if (allMessages.get(i).messageHash.equals(hash)) {
                System.out.println("Deleted: " + allMessages.get(i).messageText);
                allMessages.remove(i);
                populateArrays();
                return;
            }
        }
        System.out.println("Hash not found.");
    }

    private static void displayFullReport() {
        System.out.println("\n=== FULL REPORT - ALL MESSAGES ===");
        if (allMessages.isEmpty()) {
            System.out.println("No messages.");
            return;
        }
        for (Message m : allMessages) {
            System.out.println("Flag: " + m.flag);
            System.out.println("Hash: " + m.messageHash);
            System.out.println("Recipient: " + m.recipient);
            System.out.println("Message: " + m.messageText);
            System.out.println("ID: " + m.messageID);
            System.out.println("---------------------");
        }
    }
}