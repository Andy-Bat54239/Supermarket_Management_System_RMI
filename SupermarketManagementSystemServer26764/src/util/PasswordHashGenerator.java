package util;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.Scanner;

public class PasswordHashGenerator {
    
    public static String hashPassword(String password){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("===========================================");
        System.out.println("   PASSWORD HASH GENERATOR");
        System.out.println("===========================================");
        System.out.println();
        
        while(true) {
            System.out.print("Enter password (or 'exit' to quit): ");
            String password = scanner.nextLine();
            
            if(password.equalsIgnoreCase("exit")) {
                break;
            }
            
            String hash = hashPassword(password);
            
            System.out.println("\n--- RESULT ---");
            System.out.println("Password: " + password);
            System.out.println("Hash:     " + hash);
            System.out.println();
            System.out.println("SQL UPDATE:");
            System.out.println("UPDATE employees SET password_hash = '" + hash + "' WHERE username = 'YOUR_USERNAME';");
            System.out.println("--------------\n");
        }
        
        scanner.close();
        System.out.println("Goodbye!");
    }
}
