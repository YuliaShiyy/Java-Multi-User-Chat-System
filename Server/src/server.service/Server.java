package server.service;

import common.Message;
import common.MessageType;
import common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Yulia
 * @version 2.0
 * This is the server, listening on 9999, waiting for client connections, and maintaining communication. (Final revised version)
 * *: Modification
 */
public class Server {

    private ServerSocket ss = null;
    private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();

    static {
        validUsers.put("100", new User("100", "123456"));
        validUsers.put("200", new User("200", "123456"));
        validUsers.put("300", new User("300", "123456"));
        validUsers.put("400", new User("400", "234567"));
        validUsers.put("500", new User("500", "234567"));
        validUsers.put("600", new User("600", "234567"));
    }

    private boolean checkUser(String userId, String passwd) {
        User user = validUsers.get(userId);
        if (user == null) {
            return false;
        }
        return user.getPasswd().equals(passwd);
    }

    public Server() {
        try {
            System.out.println("The server is listening on port 9999...");
            new Thread(new SendNewsToAllService()).start();
            ss = new ServerSocket(9999);

            while (true) {
                Socket socket = ss.accept();

                new Thread(() -> {
                    ObjectInputStream ois = null;
                    ObjectOutputStream oos = null;
                    try {
                        ois = new ObjectInputStream(socket.getInputStream());
                        oos = new ObjectOutputStream(socket.getOutputStream());
                        User u = (User) ois.readObject();
                        Message message = new Message();

                        if (checkUser(u.getUserId(), u.getPasswd())) {
                            System.out.println("User id=" + u.getUserId() + " Login successful");
                            message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                            oos.writeObject(message);

                            // * Pass the socket and the two already created streams together to the new thread.
                            ServerConnectClientThread serverConnectClientThread =
                                    new ServerConnectClientThread(socket, u.getUserId(), ois, oos);
                            serverConnectClientThread.start();
                            ManageClientThreads.addClientThread(u.getUserId(), serverConnectClientThread);

                        } else {
                            System.out.println("User id=" + u.getUserId() + " pwd=" + u.getPasswd() + " Login fails");
                            message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                            oos.writeObject(message);

                            socket.close();
                            oos.close();
                            ois.close();
                        }
                    } catch (Exception e) {
                        System.out.println("Client connection or login processing error:" + e.getMessage());
                        try {
                            if (socket != null) socket.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ss != null) ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}