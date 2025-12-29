/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.security.MessageDigest;
import java.util.Base64;

/**
 *
 * @author andyb
 */
public class PasswordUtil {
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
    
    public static boolean verifyPassword(String inputPassword, String storedHash){
        String inputHash = hashPassword(inputPassword);
        return inputHash.equals(storedHash);
    }
}
