package server.service;

import java.util.concurrent.ConcurrentHashMap; // 引入并发包

/**
 * @author Yulia Shi
 * @version 2.0
 * This class is used for managing threads that communicate with clients (thread-safe optimized version).
 * *:Modification
 */
public class ManageClientThreads {
    // * Replacing HashMap with ConcurrentHashMap fundamentally solves the thread safety issue.
    private static ConcurrentHashMap<String, ServerConnectClientThread> hm = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, ServerConnectClientThread> getHm() {
        return hm;
    }

    // * Add thread objects to the hm collection
    public static void addClientThread(String userId, ServerConnectClientThread serverConnectClientThread) {
        hm.put(userId, serverConnectClientThread);
    }

    // * Returns the ServerConnectClientThread thread based on userId.
    public static ServerConnectClientThread getServerConnectClientThread(String userId) {
        return hm.get(userId);
    }

    // * Add a method to remove a specific thread object from the collection.
    public static void removeServerConnectClientThread(String userId) {
        hm.remove(userId);
    }

    /**
     * Return to online user list
     * @return Online user ID string separated by spaces
     */
    public static String getOnlineUser() {
        // * The keySet() method of ConcurrentHashMap returns a thread-safe view.
        // * No exceptions will be thrown during iteration, even if other users log in or log out. (ConcurrentModificationException)
        return String.join(" ", hm.keySet());
    }
}