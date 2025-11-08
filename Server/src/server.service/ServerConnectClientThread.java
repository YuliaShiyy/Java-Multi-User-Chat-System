package server.service;

import common.Message;
import common.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Yulia
 * @version 2.0
 * *: Modification
 */
public class ServerConnectClientThread extends Thread {

    private final Socket socket;
    private final String userId;
    // * This thread directly holds the input/output streams passed in from the outside.
    private final ObjectInputStream ois;
    private final ObjectOutputStream oos;

    // * The constructor now accepts a stream that has already been created.
    public ServerConnectClientThread(Socket socket, String userId, ObjectInputStream ois, ObjectOutputStream oos) {
        this.socket = socket;
        this.userId = userId;
        this.ois = ois;
        this.oos = oos;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // * Instead of creating a new stream, the member variable ois is used directly.
                Message message = (Message) ois.readObject();
                System.out.println("Received " + userId + " 's message: type is " + message.getMesType());

                switch (message.getMesType()) {
                    case MESSAGE_GET_ONLINE_FRIEND:
                        String onlineUser = ManageClientThreads.getOnlineUser();
                        Message onlineUserMsg = new Message();
                        onlineUserMsg.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                        onlineUserMsg.setContent(onlineUser);
                        onlineUserMsg.setGetter(message.getSender());
                        oos.writeObject(onlineUserMsg);
                        break;
                    case MESSAGE_COMM_MES:
                        ServerConnectClientThread targetThread = ManageClientThreads.getServerConnectClientThread(message.getGetter());
                        if (targetThread != null) {
                            targetThread.getOos().writeObject(message);
                        } else {
                            System.out.println("User " + message.getGetter() + " is not online，failed to send a message.");
                        }
                        break;
                    case MESSAGE_TO_ALL_MES:
                        ConcurrentHashMap<String, ServerConnectClientThread> allThreads = ManageClientThreads.getHm();
                        for (String onlineUserId : allThreads.keySet()) {
                            if (!onlineUserId.equals(message.getSender())) {
                                allThreads.get(onlineUserId).getOos().writeObject(message);
                            }
                        }
                        break;
                    case MESSAGE_FILE_MES:
                        ServerConnectClientThread fileTargetThread = ManageClientThreads.getServerConnectClientThread(message.getGetter());
                        if (fileTargetThread != null) {
                            fileTargetThread.getOos().writeObject(message);
                        }
                        break;
                    case MESSAGE_CLIENT_EXIT:
                        System.out.println(userId + " The client requests to exit.");
                        socket.close();
                        return;
                    default:
                        System.out.println("Messages of unknown type will not be processed for the time being.");
                }
            } catch (SocketException | java.io.EOFException e) {
                System.out.println("Client " + userId + " disconnects");
                break;
            } catch (Exception e) {
                System.out.println("Communication with " + userId + " caused an error: " + e.getMessage());
                break;
            }
        }

        ManageClientThreads.removeServerConnectClientThread(userId);
        System.out.println("The thread connecting the server and client " + userId + " has been closed.");
    }
}