package cache;

import controllers.UserController;
import model.User;
import utils.Config;

import java.util.ArrayList;

// Denne klasse bygger på sammen teknik som OrderCache og ProductCache. Kommentar står der.
//TODO: Build this cache and use it. : igang
public class UserCache {

    private ArrayList<User> users;
    private long ttl;
    private long created;

    public UserCache() {
        this.ttl = Config.getUserTtl();
    }

    public ArrayList<User> getUsers(Boolean forceUpdate) {
        if (forceUpdate || ((this.created + this.ttl) >= (System.currentTimeMillis() / 1000L))
                || this.users.isEmpty())  {
            ArrayList<User> users = UserController.getUsers();

            this.users = users;
            this.created = System.currentTimeMillis() / 1000L;
        }
        return users;
    }
}
