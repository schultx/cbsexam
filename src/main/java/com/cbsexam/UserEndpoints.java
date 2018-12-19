package com.cbsexam;

import cache.UserCache;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import controllers.UserController;

import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.jws.soap.SOAPBinding;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.User;
import utils.Encryption;
import utils.Log;

@Path("user")
public class UserEndpoints {

    private static UserCache userCache = new UserCache();


    /**
     * @param idUser
     * @return Responses
     */
    @GET
    @Path("/{idUser}")
    public Response getUser(@PathParam("idUser") int idUser) {

        // Use the ID to get the user from the controller.
        User user = UserController.getUser(idUser);
        // TODO: Add Encryption to JSON : FIX
        // Convert the user object to json in order to return the object
        String json = new Gson().toJson(user);
        // Encryption of json with XOR
        json = Encryption.encryptDecryptXOR(json);

        // Return the user with the status code 200
        // TODO: What should happen if something breaks down? :FIX
        // if statement der tjekke at user ikke er null og smider fejl besked ved noget der er galt.
        if (user != null) {
            // Return a response with status 200 and JSON as type
            return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
        } else {
            // Laver en fejl 400, hvis den ikke kan henter brugeren
            return Response.status(400).entity("Could not get user").build();
        }
    }

    /**
     * @return Responses
     */
    @GET
    @Path("/")
    public Response getUsers() {

        // Write to log that we are here
        Log.writeLog(this.getClass().getName(), this, "Get all users", 0);

        // Get a list of users
        // mangler
        ArrayList<User> users = userCache.getUsers(false);

        // TODO: Add Encryption to JSON :fix
        // Transfer users to json in order to return it to the user
        String json = new Gson().toJson(users);
        // Encryption of json with XOR
        json = Encryption.encryptDecryptXOR(json);

        // Return the users with the status code 200
        return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(String body) {

        // Read the json from body and transfer it to a user class
        User newUser = new Gson().fromJson(body, User.class);

        // Use the controller to add the user
        User createUser = UserController.createUser(newUser);

        // Get the user back with the added ID and return it to the user
        String json = new Gson().toJson(createUser);

        // Return the data to the user
        if (createUser != null) {
            // force opdatere user cachen
            userCache.getUsers(true);
            // Return a response with status 200 and JSON as type
            return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
        } else {
            return Response.status(400).entity("Could not create user").build();
        }
    }

    // TODO: Make the system able to login users and assign them a token to use throughout the system. :FIX
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginUser(String userLogin) {

        // user converts json to gson
        User userlogin = new Gson().fromJson(userLogin, User.class);

        // Kører login metode i UserControlleren og sætter token ind i token under.
        String token = UserController.login(userlogin);

        // Return a response with status 200 and JSON as type
        if (token != null) {
            // Return a response with status 200 and JSON as type
            return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("You have login \n You have the token:" + token).build();
        } else {
            return Response.status(400).entity("Could not login").build();
        }
    }

    // TODO: Make the system able to delete users :FIX
    @DELETE
    @Path("/delete/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("userId") int id, String body) {

        // henter token fra verifyToken metode
        DecodedJWT token = UserController.verifyToken(body);
        // Kører delete metode og deleteUser bliver true, hvis den er kørt
        if (token.getClaim("Jwt_test").asInt() == id) {
            Boolean deleteUser = UserController.delete(token.getClaim("Jwt_test").asInt());

            // if deleteUser er true opdaterer cachen
            if (deleteUser) {
                userCache.getUsers(true);
                // Return a response with status 200 and JSON as type
                // besked om hvilken bruger der er slettet.
                return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("Delete user with id " + id).build();
            } else {
                return Response.status(400).entity("Could not delete user").build();
            }
        } else {
            return Response.status(400).entity("It's not possible to delete other users").build();

        }
    }


    // TODO: Make the system able to update users :FIX
    @POST
    @Path("/update/{userId}/{token}")
    @Consumes(MediaType.APPLICATION_JSON)
    // bruger to params for at update
    public Response updateUser(@PathParam("userId") int id, @PathParam("token") String token, String body) {

        // converts user from gson to Json
        User user = new Gson().fromJson(body, User.class);

        // henter token fra verifyToken metoden
        DecodedJWT jwt = UserController.verifyToken(token);

        // opdatere user ud fra user og token. Giver en true eller false tilbage
        if (jwt.getClaim("Jwt_test").asInt() == id) {
            Boolean updateUser = UserController.update(user, jwt.getClaim("Jwt_test").asInt());

            // tjekker at brugeren blev opdateret ellers fejlbesked
            if (updateUser) {
                // Update cashe because we update user
                userCache.getUsers(true);
                // Return a response with status 200 and JSON as type
                return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("Update user with id " + id).build();
            } else {
                return Response.status(400).entity("Could not update user").build();
            }
        } else {
            return Response.status(400).entity("It's not possible to update other users").build();
        }

    }
}
