package controllers;

import java.sql.*;
import java.util.ArrayList;

import cache.UserCache;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import model.User;
import org.apache.solr.common.util.Hash;
import utils.Hashing;
import utils.Log;

public class UserController {

    private static DatabaseController dbCon;

    public UserController() {
        dbCon = new DatabaseController();
    }

    public static User getUser(int id) {

        // Check for connection
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }

        // Build the query for DB
        String sql = "SELECT * FROM user where id=" + id;

        // Actually do the query
        ResultSet rs = dbCon.query(sql);
        User user = null;

        try {
            // Get first object, since we only have one
            if (rs.next()) {
                user =
                        new User(
                                rs.getInt("id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("password"),
                                rs.getString("email"));

                // return the  object
                return user;
            } else {
                System.out.println("No user found");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        // Return null
        return user;
    }

    /**
     * Get all users in database
     *
     * @return
     */
    public static ArrayList<User> getUsers() {

        // Check for DB connection
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }

        // Build SQL
        String sql = "SELECT * FROM user";

        // Do the query and initialyze an empty list for use if we don't get results
        ResultSet rs = dbCon.query(sql);
        ArrayList<User> users = new ArrayList<User>();

        try {
            // Loop through DB Data
            while (rs.next()) {
                User user =
                        new User(
                                rs.getInt("id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("password"),
                                rs.getString("email"));

                // Add element to list
                users.add(user);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        // Return the list of users
        return users;
    }


    public static User updateUser(User user) {

        // Check for DB connection
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }


        // Build SQL prepared statement
        String sql = "UPDATE * FROM user WHERE id = ?";

        int userID = dbCon.insert(
                "UPDATE user WHERE id =? (first_name, last_name, password, email, created_at) VALUES('"
                        + user.getFirstname()
                        + "', '"
                        + user.getLastname()
                        + "', '"
                        //Hasher password vha. MD5 metoden i Hashing klassen for at sikre password.
                        + Hashing.shaHashWithSalt(user.getPassword())
                        + "', '"
                        + user.getEmail()
                        + "', "
                        + user.getCreatedTime()
                        + ")");


        return user;
    }

    public static User createUser(User user) {

        // Write in log that we've reach this step
        Log.writeLog(UserController.class.getName(), user, "Actually creating a user in DB", 0);

        // Set creation time for user.
        user.setCreatedTime(System.currentTimeMillis() / 1000L);

        // Check for DB Connection
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }

        // Insert the user in the DB
        // TODO: Hash the user password before saving it. :FIX

        int userID = dbCon.insert(
                "INSERT INTO user(first_name, last_name, password, email, created_at) VALUES('"
                        + user.getFirstname()
                        + "', '"
                        + user.getLastname()
                        + "', '"
                        //Hasher password vha. MD5 metoden i Hashing klassen for at sikre password.
                        + Hashing.shaHashWithSalt(user.getPassword())
                        + "', '"
                        + user.getEmail()
                        + "', "
                        + user.getCreatedTime()
                        + ")");

        if (userID != 0) {
            //Update the userid of the user before returning
            user.setId(userID);
        } else {
            // Return null if user has not been inserted into database
            return null;
        }

        // Return user
        return user;
    }

    // delete metode
    public static boolean delete(int id) {

        // write to log
        Log.writeLog(UserController.class.getName(), id, "Deleting a user", 0);

        // Check for DB Connection
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }

        User user = UserController.getUser(id);

        if (user != null) {
            // kører updateDelete i DatabaseCon. sql statement for deleting user with id
            dbCon.updateDelete("DELETE from user WHERE id =" + id);
            return true;
        } else {
            return false;
        }

    }

    public static boolean update(User user, int id) {

        //write to log
        Log.writeLog(UserController.class.getName(), id, "Updating user", 0);

        // Check for DB Connection
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }

        if (user != null) {
            // prøve = ?  og slet "
            dbCon.updateDelete("UPDATE user SET first_name '" + user.getFirstname() +
                    "', last_name = '" + user.getLastname() +
                    "', email ='" + user.getEmail() +
                    "', password ='" + user.getPassword() +
                    "'where id=" + id);
            return true;

        } else {
            return false;
        }
    }

    public static String login(User userLogin) {

        //write to log
        Log.writeLog(UserController.class.getName(), userLogin, "Logging in a user", 0);

        // Check for DB Connection
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }

        UserCache userCache = new UserCache();
        ArrayList<User> users = userCache.getUsers(false);

        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());

        for (User user : users) {
            if (
                    user.getEmail().equals(userLogin.getEmail()) && user.getPassword().equals(Hashing.shaHashWithSalt(userLogin.getPassword()))) {

                try {
                    Algorithm algorithm = Algorithm.HMAC256("secret_key");
                    String token = JWT.create().withIssuer("auth0").withClaim("hejduder",timeStamp).withClaim("Jwt_test", user.getId()).sign(algorithm);
                    return token;
                } catch (JWTCreationException ex) {
                    System.out.println(ex.getMessage());

                }


            }
        }
        return null;

    }

    public static DecodedJWT vertifyToken (String userToken) {

        //write to log
        Log.writeLog(UserController.class.getName(), userToken, "Vertifying a token", 0);


        String token = userToken;
        try {
            Algorithm algorithm = Algorithm.HMAC256("tester");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
            return jwt;

        } catch (JWTVerificationException ex){
           ex.getMessage();

        }
        return null;
    }

}