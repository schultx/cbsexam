package cache;

import controllers.OrderController;
import model.Order;
import utils.Config;

import java.util.ArrayList;

// Denne klasse bygger samme teknik som ProductCache.
//TODO: Build this cache and use it. : igang
public class OrderCache {


    // Liste for order
    private ArrayList<Order> orders;

    // Tiden time cache skal kører(leve)
    private long ttl;

    // Sææter, når cachen er lavet.
    private long created;

    public OrderCache() {
        this.ttl = Config.getOrderTtl();
    }

    public ArrayList<Order> getOrders(Boolean forceUpdate) {

        // Hvis vi ønsker at slette cache, kan vi bruge force update.
        // Ellers kigger man på alderen af cashen og find ud af om man vil opdaterer
        // Hvis listen er tom kan vi tjekke for ny order.
        if (forceUpdate
                || ((this.created + this.ttl) >= (System.currentTimeMillis() / 1000L))
                || this.orders == null) {

            // Get order fra controller, siden vi vil opdaterer.
            ArrayList<Order> orders = OrderController.getOrders();

            // Set order for instance og set et created timestamp.
            this.orders = orders;
            this.created = System.currentTimeMillis() / 1000L;
        }

        // Return orders
        return this.orders;
    }

}
