package client.service;

import common.Message;
import common.MessageType;
import common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author Yulia Shi
 * @version 2.0
 * *: Modification
 */
public class UserClientService {

    private User u = new User();

    public boolean checkUser(String userId, String pwd) {
        boolean b = false;
        u.setUserId(userId);
        u.setPasswd(pwd);

        Socket socket = null;

        try {
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
            // * Create the input and output streams all at once here.
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            oos.writeObject(u);

            Message ms = (Message) ois.readObject();

            if (ms.getMesType() == MessageType.MESSAGE_LOGIN_SUCCEED) {
                // * Pass the socket and two streams together to the thread.
                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket, oos, ois);
                clientConnectServerThread.start();

                ManageClientConnectServerThread.addClientConnectServerThread(userId, clientConnectServerThread);
                b = true;
            } else {
                ois.close();
                oos.close();
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    public void onlineFriendList() {
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(u.getUserId());

        try {
            // * Get the thread, and then get the reusable output stream held by that thread.
            ObjectOutputStream oos = ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId()).getOos();
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserId());

        try {
            ObjectOutputStream oos = ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId()).getOos();
            oos.writeObject(message);
            System.out.println(u.getUserId() + " Exit System ");
            System.exit(0); // Terminate process
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}