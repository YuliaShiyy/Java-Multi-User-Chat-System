package client.service;

import common.Message;
import common.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * @author Yulia Shi
 * @version 2.0
 * *: Modification
 */
public class MessageClientService {

    /**
     * Mass messaging
     * @param content content
     * @param senderId sender
     */
    public void sendMessageToAll(String content, String senderId) {
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_TO_ALL_MES);
        message.setSender(senderId);
        message.setContent(content);
        message.setSendTime(new Date().toString());
        System.out.println(senderId + " to everyone sends: " + content);

        try {
            // * Get the thread, and then get the reusable output stream held by that thread.
            ClientConnectServerThread clientThread = ManageClientConnectServerThread.getClientConnectServerThread(senderId);
            if (clientThread != null) {
                ObjectOutputStream oos = clientThread.getOos();
                oos.writeObject(message);
            } else {
                System.out.println("Error: Client thread not found, message sending failed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Private messaging
     * @param content  content
     * @param senderId sender
     * @param getterId receiver
     */
    public void sendMessageToOne(String content, String senderId, String getterId) {
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_COMM_MES);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setContent(content);
        message.setSendTime(new Date().toString());
        System.out.println(senderId + " to " + getterId + " sends: " + content);

        try {
            // Exactly the same correct pattern
            ClientConnectServerThread clientThread = ManageClientConnectServerThread.getClientConnectServerThread(senderId);
            if (clientThread != null) {
                ObjectOutputStream oos = clientThread.getOos();
                oos.writeObject(message);
            } else {
                System.out.println("Error: Client thread not found, message sending failed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}