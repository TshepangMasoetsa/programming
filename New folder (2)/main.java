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

    public Message(int messageNumber, String recipient, String messageText) {
        this.messageID = UUID.randomUUID().toString();
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
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
                "  \"Message\": \"" + messageText + "\",\n" +
                "  \"MessageHash\": \"" + messageHash + "\"\n" +
                "}";
    }
}

// ================= MAIN CLASS =================
public class main {
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        User user = new User();
        ArrayList<Message> messages = new ArrayList<>();

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
            return;
        }

        // ================= MESSAGE LIMIT =================
        System.out.print("\nHow many messages would you like to send? ");
        int maxMessages = input.nextInt();

        while (maxMessages <= 0) {
            System.out.print("Enter a valid number: ");
            maxMessages = input.nextInt();
        }
        input.nextLine();

        int messageCount = 0;
        int choice = 0;

        // ================= MENU =================
        do {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.print("Choose: ");

            if (input.hasNextInt()) {
                choice = input.nextInt();
                input.nextLine();
            } else {
                System.out.println("Enter a valid number.");
                input.nextLine();
                continue;
            }

            switch (choice) {

                case 1:

                    if (messageCount >= maxMessages) {
                        System.out.println("Message limit reached.");
                        System.out.println("Total messages sent: " + messageCount);
                        break;
                    }

                    System.out.print("Enter recipient (+27...): ");
                    String recipient = input.nextLine();

                    String text;

                    do {
                        System.out.print("Enter message (max 250 characters): ");
                        text = input.nextLine();

                        if (text.length() > 250) {
                            System.out.println("Message too long. Max 250 characters.");
                        }

                    } while (text.length() > 250);

                    messageCount++;

                    Message msg = new Message(messageCount, recipient, text);
                    messages.add(msg);

                    // REQUIRED DISPLAY ORDER
                    System.out.println("\n--- Message Details ---");
                    System.out.println("Message ID: " + msg.messageID);
                    System.out.println("Message Hash: " + msg.messageHash);
                    System.out.println("Recipient: " + msg.recipient);
                    System.out.println("Message: " + msg.messageText);
                    System.out.println("-----------------------");

                    System.out.println("Message sent ✔");

                    if (messageCount == maxMessages) {
                        System.out.println("\nAll messages sent!");
                        System.out.println("Total messages sent: " + messageCount);
                    }

                    break;

                case 2:
                    System.out.println("Coming Soon.");
                    break;

                case 3:
                    saveToJSON(messages);
                    System.out.println("Total messages sent: " + messageCount);
                    System.out.println("Messages saved. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid option.");
            }

        } while (choice != 3);

        input.close();
    }

    // ================= SAVE TO JSON =================
    public static void saveToJSON(ArrayList<Message> messages) {
        try {
            FileWriter writer = new FileWriter("messages.json");
            writer.write("[\n");

            for (int i = 0; i < messages.size(); i++) {
                writer.write(messages.get(i).toJSON());
                if (i < messages.size() - 1) {
                    writer.write(",\n");
                }
            }

            writer.write("\n]");
            writer.close();

        } catch (IOException e) {
            System.out.println("Error saving file.");
        }
    }
}