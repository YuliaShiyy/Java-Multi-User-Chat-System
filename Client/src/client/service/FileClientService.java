package client.service;

import common.Message;
import common.MessageType;

import java.io.*;

/**
 * @author Yulia Shi
 * @version 2.0
 * This class/object completes the file transfer service (optimized version).
 * *: Modification
 */
public class FileClientService {
    /**
     *
     * @param src Source file
     * @param dest To which directory on the other end of the network should I transfer this file?
     * @param senderId Sender ID
     * @param getterId Receiver ID
     */
    public void sendFileToOne(String src, String dest, String senderId, String getterId) {

        File file = new File(src);
        // * Added robustness check
        if (!file.exists()) {
            System.out.println("Error: Source file: " + src + " does not exist.");
            return;
        }
        if (file.isDirectory()) {
            System.out.println("Error: Cannot send folder, please select a file.");
            return;
        }

        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setSrc(src);
        message.setDest(dest);

        byte[] fileBytes = new byte[(int)file.length()];

        // 2. * Use try-with-resources to automatically manage FileInputStream.
        try (FileInputStream fileInputStream = new FileInputStream(src)) {
            fileInputStream.read(fileBytes);
            message.setFileBytes(fileBytes);
        } catch (IOException e) {
            System.out.println("File reading failed: " + e.getMessage());
            e.printStackTrace();
            return; // If the read fails, do not continue sending.
        }

        System.out.println("\n" + senderId + " to " + getterId + " sends file: " + src +
                 "to the recipient's computer directory: " + dest);

        // 3. * Use the correct, reusable stream to send messages.
        try {
            ClientConnectServerThread clientThread = ManageClientConnectServerThread.getClientConnectServerThread(senderId);
            if (clientThread != null) {
                ObjectOutputStream oos = clientThread.getOos();
                oos.writeObject(message);
            } else {
                System.out.println("Error: Client thread not found, file send failed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}