package controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Address;
import model.LineItem;
import model.Order;
import model.User;
import utils.Log;

public class OrderController {

    private static DatabaseController dbCon;

    public OrderController() {
        dbCon = new DatabaseController();
    }

    public static Order getOrder(int id) {

        // check for connection
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }

// A SQL String to a query
        String sql = "SELECT *, billing.street_address as billing, shipping.street_address as shipping \n " +
                "FROM orders \n " +
                "JOIN user on orders.user_id = user.id\n " +
                "LEFT JOIN address as billing \n " +
                "ON orders.billing_address_id = billing.id\n " +
                "LEFT JOIN address as shipping \n " +
                "ON orders.shipping_address_id = shipping.id\n " +
                "WHERE orders.id= " + id;

        // Making the query in the database
        ResultSet rs = dbCon.query(sql);
        // Creating an empty object for all the results
        Order order = null;

        // if statement for result set for bestemt id
        try {
            if (rs.next()) {
                //TODO: Perhaps we could optimize things a bit here and get rid of nested queries. :FIX

                // Arraylist for Line Item
                ArrayList<LineItem> lineItems = LineItemController.getLineItemsForOrder(rs.getInt("id"));

                // opsætter information til en order ud fra et objekt af user
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getLong("created_at"));

                // opsætter information til en order ud fra et objekt af address
                Address billingAddress =
                        new Address(
                                rs.getInt("billing_address_id"),
                                rs.getString("name"),
                                rs.getString("billing"),
                                rs.getString("city"),
                                rs.getString("zipcode"));

                // opsætter information til en order ud fra et objekt af address
                Address shippingAddress =
                        new Address(
                                rs.getInt("billing_address_id"),
                                rs.getString("name"),
                                rs.getString("billing"),
                                rs.getString("city"),
                                rs.getString("zipcode"));

                // opsætter information til en order ud fra et objekt af order
                order =
                        new Order(
                                rs.getInt("id"),
                                user,
                                lineItems,
                                billingAddress,
                                shippingAddress,
                                rs.getFloat("order_total"),
                                rs.getLong("created_at"),
                                rs.getLong("updated_at"));

                // Returns the order that have been build.
                return order;
                // ellers print at der ikke var nogle ordre
            } else {
                System.out.println("No order was found");
            }
            // catcher fejl
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        // return null
        return order;
    }


    // Gammel kode gemt i tilfælde af test i forhold til nyt
//    // Build SQL string to query
//    String sql = "SELECT * FROM orders where id=" + id;
//
//    // Do the query in the database and create an empty object for the results
//    ResultSet rs = dbCon.query(sql);
//    Order order = null;
//
//    try {
//      if (rs.next()) {
//
//        User user = UserController.getUser(rs.getInt("user_id"));
//        ArrayList<LineItem> lineItems = LineItemController.getLineItemsForOrder(rs.getInt("id"));
//        Address billingAddress = AddressController.getAddress(rs.getInt("billing_address_id"));
//        Address shippingAddress = AddressController.getAddress(rs.getInt("shipping_address_id"));
//
//        // Create an object instance of order from the database dataa
//        order =
//            new Order(
//                rs.getInt("id"),
//                user,
//                lineItems,
//                billingAddress,
//                shippingAddress,
//                rs.getFloat("order_total"),
//                rs.getLong("created_at"),
//                rs.getLong("updated_at"));
//
//        // Returns the build order
//        return order;
//      } else {
//        System.out.println("No order found");
//      }
//    } catch (SQLException ex) {
//      System.out.println(ex.getMessage());
//    }
//
//    // Returns null
//    return order;
//  }

    /**
     * Get all orders in database
     *
     * @return
     */
    public static ArrayList<Order> getOrders() {

        if (dbCon == null) {
            dbCon = new DatabaseController();
        }


        // nyt
        String sql = "SELECT*, billing.street_address as billing, shipping.street_address as shipping \n" +
                "FROM orders \n " +
                "JOIN user on orders.user_id = user.id\n " +
                "LEFT JOIN address as billing \n " +
                "ON orders.billing_address_id = billing.id\n " +
                "LEFT JOIN address as shipping \n " +
                "ON orders.shipping_address_id = shipping.id";


        ResultSet rs = dbCon.query(sql);
        ArrayList<Order> orders = new ArrayList<>();

        // while loop for hvert skridt i result set
        try {
            while (rs.next()) {
                //TODO: Perhaps we could optimize things a bit here and get rid of nested queries. :FIX

                // Arraylist for Line Item
                ArrayList<LineItem> lineItems = LineItemController.getLineItemsForOrder(rs.getInt("id"));

                // opsætter information til en bruger ud fra et objekt af user
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getLong("created_at"));

                // opsætter information til en billingAddress ud fra et objekt af address
                Address billingAddress =
                        new Address(
                                rs.getInt("billing_address_id"),
                                rs.getString("name"),
                                rs.getString("billing"),
                                rs.getString("city"),
                                rs.getString("zipcode"));

                // opsætter information til en shippingAddress ud fra et objekt af address
                Address shippingAddress =
                        new Address(
                                rs.getInt("billing_address_id"),
                                rs.getString("name"),
                                rs.getString("billing"),
                                rs.getString("city"),
                                rs.getString("zipcode"));

                // opsætter information til en order ud fra et objekt af address
                Order order =
                        new Order(
                                rs.getInt("id"),
                                user,
                                lineItems,
                                billingAddress,
                                shippingAddress,
                                rs.getFloat("order_total"),
                                rs.getLong("created_at"),
                                rs.getLong("updated_at"));
                // tilføjer order
                orders.add(order);
            }
            // catcher fejl
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        // return null
        return orders;
    }

// Gammelt kode
//    // Rettet fejl. Der stod "order" og er rettet til "orders", som i SQL.
//    String sql = "SELECT * FROM orders";
//
//    ResultSet rs = dbCon.query(sql);
//    ArrayList<Order> orders = new ArrayList<Order>();
//
//        try
//
//    {
//        while (rs.next()) {
//
//            // Perhaps we could optimize things a bit here and get rid of nested queries.
//            User user = UserController.getUser(rs.getInt("user_id"));
//            ArrayList<LineItem> lineItems = LineItemController.getLineItemsForOrder(rs.getInt("id"));
//            Address billingAddress = AddressController.getAddress(rs.getInt("billing_address_id"));
//            Address shippingAddress = AddressController.getAddress(rs.getInt("shipping_address_id"));
//
//            // Create an order from the database data
//            Order order =
//                    new Order(
//                            rs.getInt("id"),
//                            user,
//                            lineItems,
//                            billingAddress,
//                            shippingAddress,
//                            rs.getFloat("order_total"),
//                            rs.getLong("created_at"),
//                            rs.getLong("updated_at"));
//
//            // Add order to our list
//            orders.add(order);
//
//        }
//    } catch(
//    SQLException ex)
//
//    {
//        System.out.println(ex.getMessage());
//    }
//
//    // return the orders
//        return orders;
//}

    public static Order createOrder(Order order) {

        // Write in log that we've reach this step
        Log.writeLog(OrderController.class.getName(), order, "Actually creating a order in DB", 0);

        // Set creation and updated time for order.
        order.setCreatedAt(System.currentTimeMillis() / 1000L);
        order.setUpdatedAt(System.currentTimeMillis() / 1000L);

        // Check for DB Connection
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }


        // TODO: Enable transactions in order for us to not save the order if somethings fails for some of the other inserts. :FIX

        Connection connection = DatabaseController.getConnection();


        // Har sat order ind i try, da det ikke hjælper at vi kun kører dem delvist.
        try {
            // Sætter auto commit til false, fordi vi vil have det hele og ikke kun noget af en order.
            connection.setAutoCommit(false);


            // Save addresses to database and save them back to initial order instance
            order.setBillingAddress(AddressController.createAddress(order.getBillingAddress()));
            order.setShippingAddress(AddressController.createAddress(order.getShippingAddress()));

            // Save the user to the database and save them back to initial order instance
            order.setCustomer(UserController.createUser(order.getCustomer()));

            // Insert the product in the DB
            int orderID = dbCon.insert(
                    "INSERT INTO orders(user_id, billing_address_id, shipping_address_id, order_total, created_at, updated_at) VALUES("
                            + order.getCustomer().getId()
                            + ", "
                            + order.getBillingAddress().getId()
                            + ", "
                            + order.getShippingAddress().getId()
                            + ", "
                            + order.calculateOrderTotal()
                            + ", "
                            + order.getCreatedAt()
                            + ", "
                            + order.getUpdatedAt()
                            + ")");

            if (orderID != 0) {
                //Update the productid of the product before returning
                order.setId(orderID);
            }

            // Create an empty list in order to go trough items and then save them back with ID
            ArrayList<LineItem> items = new ArrayList<LineItem>();

            // Save line items to database
            for (LineItem item : order.getLineItems()) {
                item = LineItemController.createLineItem(item, order.getId());
                items.add(item);
            }

            order.setLineItems(items);
            // committer hvis orderen er gået igennem
            connection.commit();
        } catch (SQLException ex) {

            try {
                // hvis det ikke lykkes, så laver den et rollback til det den var før.
                connection.rollback();
                System.out.println("Rollback");
            } catch (SQLException ex3) {
                System.out.println(ex3.getMessage());
            }

            //hvis det ikke går igennem
            System.out.println("No rollback" + ex.getMessage());
        } finally {
            // fordi autocomit ikke skal være
            try {
                // sætter autoComit til true, da det skulle have kørt det hele nu.
                connection.setAutoCommit(true);
            } catch (SQLException ex2) {
                ex2.getMessage();
            }
        }
        // Return order
        return order;
    }
}