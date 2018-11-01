package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.bouncycastle.util.encoders.Hex;

public final class Hashing {

  // TODO: You should add a salt and make this secure :FIX
  public static String md5(String rawString, byte[] salt) {
    try {

      // We load the hashing algoritm we wish to use. Ved videre udvikling vil man se på PBKDF2, BCrypt, and SCrypt i stedet for MD5.
      // Deres hashing er mere sikkert.
      MessageDigest md = MessageDigest.getInstance("MD5");

      // We convert to byte array
      byte[] byteArray = md.digest(rawString.getBytes());

      // Initialize a string buffer
      StringBuffer sb = new StringBuffer();

      // Run through byteArray one element at a time and append the value to our stringBuffer
      for (int i = 0; i < byteArray.length; ++i) {
        sb.append(Integer.toHexString((byteArray[i] & 0xFF) | 0x100).substring(1, 3));
      }

      //Convert back to a single string and return
      return sb.toString();

    } catch (java.security.NoSuchAlgorithmException e) {

      //If somethings breaks
      System.out.println("Could not hash string");
    }

    return null;
  }

  // Metode for at tilføje salt til password og hasher det med MD5 efter.
  public static String md5HashWithSalt(String hashsalt){
    //Et bestemt salt. Skal laves random ved videreudvikling.
    String salt = "gniu454iun";
    // Ligger password sammen med salt til en ny string "hashed"
    String hashed = hashsalt + salt;
    // returnere password med salt efter, som sha metode hasher.
    return (sha(hashed));

  }

// Metode for at tilføje salt til password og hasher det med sha efter.
  public static String shaHashWithSalt(String hashsalt){
    //Et bestemt salt. Skal laves random ved videreudvikling.
    String salt = "gniu454iun";
    // Ligger password sammen med salt til en ny string "hashed"
    String hashed = hashsalt + salt;
    // returnere password med salt efter, som sha metode hasher.
    return (sha(hashed));

  }
// START PÅ RANDOM SALT. DER SKAL ÆNDRES I DATABASESTUKTUREN, SÅ SALT TILGÅR HVER BRUGER.
//  public static byte[] setSalt() {
//
//    byte[] salt = new byte[20];
//    SecureRandom random = new SecureRandom();
//    random.nextBytes(salt);
//    return salt;
//  }

  // TODO: You should add a salt and make this secure :FIX
  public static String sha(String rawString) {
    try {
      // We load the hashing algoritm we wish to use.
      MessageDigest digest = MessageDigest.getInstance("SHA-256");

      // We convert to byte array
      byte[] hash = digest.digest(rawString.getBytes(StandardCharsets.UTF_8));

      // We create the hashed string
      String sha256hex = new String(Hex.encode(hash));

      // And return the string
      return sha256hex;

    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    return rawString;
  }
}