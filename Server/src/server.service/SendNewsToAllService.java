package server.service;

import common.Message;
import common.MessageType;
import utils.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Yulia
 * @version 2.0
 * *: Modification
 */
public class SendNewsToAllService implements Runnable {

    @Override
    public void run() {
        while (true) {
            System.out.println("Please enter the news/message that the server wants to push [Enter 'exit' to exit the push service thread]");
            String news = Utility.readString(100);

            if ("exit".equalsIgnoreCase(news)) {
                System.out.println("Exit server news push service......");
                break;
            }

            Message message = new Message();
            message.setSender("Server");
            message.setMesType(MessageType.MESSAGE_TO_ALL_MES);
            message.setContent(news);
            message.setSendTime(new Date().toString());
            System.out.println("The server pushes a message to everyone: " + news);

            // * The type of the variable hm is now ConcurrentHashMap.
            ConcurrentHashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();

            for (ServerConnectClientThread thread : hm.values()) {
                try {
                    ObjectOutputStream oos = thread.getOos();
                    if (oos != null) {
                        oos.writeObject(message);
                    }
                } catch (IOException e) {
                    System.out.println("To user " + thread.getName() + " failed to push a message: " + e.getMessage());
                }
            }
        }
    }
}