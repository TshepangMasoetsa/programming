import java.util.Scanner;

class User {
    String username;
    String password;
    String phoneNumber;
    String firstName;
    String lastName;

    // Username check
    public boolean checkUsername(String username) {
        return username.contains("_") && username.length() <= 5;
    }

    // Password check
    public boolean checkPassword(String password) {
        boolean hasUpper = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;

        if (password.length() >= 8) {
            for (int i = 0; i < password.length(); i++) {
                char ch = password.charAt(i);

                if (Character.isUpperCase(ch)) hasUpper = true;
                else if (Character.isDigit(ch)) hasNumber = true;
                else if (!Character.isLetterOrDigit(ch)) hasSpecial = true;
            }
        }

        return hasUpper && hasNumber && hasSpecial;
    }

    // Phone regex
    public boolean checkPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^\\+27\\d{9}$");
    }

    // Register user
    public void registerUser(String firstName, String lastName, String username, String password, String phoneNumber) {

        if (checkUsername(username) && checkPassword(password) && checkPhoneNumber(phoneNumber)) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.username = username;
            this.password = password;
            this.phoneNumber = phoneNumber;

            System.out.println("Registration successful!");
        } else {
            System.out.println("Registration failed. Please fix errors.");
        }
    }

    // Login check
    public boolean loginUser(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }
}

public class main {
    public static void main(String[] args) {

        Scanner Input = new Scanner(System.in);
        User user = new User();

        // ================= REGISTRATION =================
        System.out.println("=== REGISTRATION ===");

        System.out.print("Enter first name: ");
        String firstName = Input.nextLine();

        System.out.print("Enter last name: ");
        String lastName = Input.nextLine();

        // Username loop
        String username;
        do {
            System.out.print("Enter username: ");
            username = Input.nextLine();

            if (!user.checkUsername(username)) {
                System.out.println("Invalid username. Must contain '_' and be ≤ 5 characters.");
            }

        } while (!user.checkUsername(username));

        // Password loop
        String password;
        do {
            System.out.print("Enter password: ");
            password = Input.nextLine();

            if (!user.checkPassword(password)) {
                System.out.println("Invalid password. Must be 8+ chars, include capital, number & special character.");
            }

        } while (!user.checkPassword(password));

        // Phone loop
        String phone;
        do {
            System.out.print("Enter phone number +27 "); 
            phone = Input.nextLine();

            if (!user.checkPhoneNumber(phone)) {
                System.out.println("Cell phone number incorrectly formatted or missing international code.");
            }

        } while (!user.checkPhoneNumber(phone));

        // Save user
        user.registerUser(firstName, lastName, username, password, phone);

        // ================= LOGIN =================
        System.out.println("\n=== LOGIN ===");

        int attempts = 0;
        boolean loggedIn = false;

        while (attempts < 3 && !loggedIn) {
            System.out.print("Enter username: ");
            String loginUser = Input.nextLine();

            System.out.print("Enter password: ");
            String loginPass = Input.nextLine();

            if (user.loginUser(loginUser, loginPass)) {
                System.out.println("Welcome " + firstName + ", " + lastName + " it is great to see you again.");
                loggedIn = true;
            } else {
                System.out.println("Username or password incorrect, please try again.");
                attempts++;
            }
        }

        if (!loggedIn) {
            System.out.println("Too many failed attempts. Account locked.");
        }
    }
}