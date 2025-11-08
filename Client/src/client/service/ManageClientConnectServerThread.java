package client.service;

// import java.util.HashMap; // No longer needed
import java.util.concurrent.ConcurrentHashMap; // Use the thread-safe version

/**
 * @author Yulia Shi
 * @version 2.0
 * *: Modification
 */
public class ManageClientConnectServerThread {
    // * Use ConcurrentHashMap
    private static ConcurrentHashMap<String, ClientConnectServerThread> hm = new ConcurrentHashMap<>();

    // * Add a thread to the collection
    public static void addClientConnectServerThread(String userId, ClientConnectServerThread clientConnectServerThread) {
        hm.put(userId, clientConnectServerThread);
    }

    // * The corresponding thread can be obtained through userId.
    public static ClientConnectServerThread getClientConnectServerThread(String userId) {
        return hm.get(userId);
    }

    // * Add a method to remove a specific thread object from the collection,
    //      which is called when the user logs out.
    public static void removeClientConnectServerThread(String userId) {
        hm.remove(userId);
    }
}