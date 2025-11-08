package client.service;

import common.Message;
import common.MessageType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author Yulia Shi
 * @version 2.0
 * *: Modification
 */
public class ClientConnectServerThread extends Thread {
    private final Socket socket;
    // * This thread now holds input/output streams passed in from outside,
    //      instead of creating its own.
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;

    // * The constructor now accepts a stream that has already been created.
    public ClientConnectServerThread(Socket socket, ObjectOutputStream oos, ObjectInputStream ois) {
        this.socket = socket;
        this.oos = oos;
        this.ois = ois;
    }

    @Override
    public void run() {
        // * The entire while loop is wrapped in a try-catch block,
        //      and no new streams are created.
        while (true) {
            try {
                //System.out.println("The client thread waits to read messages from the server...");
                Message message = (Message) ois.readObject(); // Use the existing ois directly

                switch (message.getMesType()) {
                    case MESSAGE_RET_ONLINE_FRIEND:
                        String[] onlineUsers = message.getContent().split(" ");
                        System.out.println("\n======= Current online user list =======");
                        for (String user : onlineUsers) {
                            System.out.println("User: " + user);
                        }
                        break;

                    case MESSAGE_COMM_MES:
                        System.out.println("\n" + message.getSender()
                                + " to " + message.getGetter() + " sends: " + message.getContent());
                        break;

                    case MESSAGE_TO_ALL_MES:
                        System.out.println("\n" + message.getSender() + " to everyone sends: " + message.getContent());
                        break;

                    case MESSAGE_FILE_MES:
                        System.out.println("\n" + message.getSender() + " to " + message.getGetter()
                                + " sends file: " + message.getSrc() + " to my computer directory: " + message.getDest());

                        try (FileOutputStream fileOutputStream = new FileOutputStream(message.getDest(), false)) {
                            fileOutputStream.write(message.getFileBytes());
                            System.out.println("\n File saved successfully~");
                        } catch (IOException e) {
                            System.out.println("File saving failed: " + e.getMessage());
                        }
                        break;

                    default:
                        System.out.println("Messages of unknown type will not be processed for the time being....");
                }
            } catch (SocketException | java.io.EOFException e) {
                System.out.println("\nThe connection to the server has been lost, and the program will exit.");
                System.exit(0);
                break;
            } catch (Exception e) {
                System.out.println("\nAn exception occurred in the client thread: " + e.getMessage());
                e.printStackTrace();
                break;
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }

    // Other service classes obtain a unique, reusable output stream through this method.
    public ObjectOutputStream getOos() {
        return oos;
    }
}