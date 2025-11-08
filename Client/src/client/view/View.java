package client.view;

import client.service.FileClientService;
import client.service.MessageClientService;
import client.service.UserClientService;
import client.utils.Utility;

/**
 * @author Yulia Shi
 * @version 2.0
 * Client menu interface
 */
public class View {

    private boolean loop = true;
    private String key = "";
    private UserClientService userClientService = new UserClientService();
    private MessageClientService messageClientService = new MessageClientService();
    private FileClientService fileClientService = new FileClientService();
    public static void main(String[] args) {
        new View().mainMenu();
        System.out.println("Client exits system...");
    }

    private void mainMenu() {

        boolean mainLoop = true;

        while (mainLoop) {
            System.out.println("===========Welcome to the network communication system===========");
            System.out.println("\t\t 1 Login System ");
            System.out.println("\t\t 9 Exit System");
            System.out.print("Please input your choice: ");
            key = Utility.readString(1);

            switch (key) {
                case "1":
                    System.out.print("Please enter your user ID: ");
                    String userId = Utility.readString(50);
                    System.out.print("Please enter your password:");
                    String pwd = Utility.readString(50);

                    if (userClientService.checkUser(userId, pwd)) {
                        System.out.println("===========Welcome " + userId + " !===========");

                        boolean secondaryLoop = true;
                        while (secondaryLoop) {
                            System.out.println("\n=========Second-level menu of network communication system (User " + userId + " )=======");
                            System.out.println("\t\t 1 Display list of online users");
                            System.out.println("\t\t 2 Mass messaging");
                            System.out.println("\t\t 3 Private messaging");
                            System.out.println("\t\t 4 Send files");
                            System.out.println("\t\t 9 Log out");
                            System.out.print("Please input your choice:");
                            key = Utility.readString(1);

                            switch (key) {
                                case "1":
                                    userClientService.onlineFriendList();
                                    break;
                                case "2":
                                    System.out.print("Please enter what you want to say to everyone: ");
                                    String groupContent = Utility.readString(100);
                                    messageClientService.sendMessageToAll(groupContent, userId);
                                    break;
                                case "3":
                                    System.out.print("Please enter the username you wish to chat with (online): ");
                                    String getterId = Utility.readString(50);
                                    System.out.print("Please enter what you want to say: ");
                                    String privateContent = Utility.readString(100);
                                    messageClientService.sendMessageToOne(privateContent, userId, getterId);
                                    break;
                                case "4":
                                    System.out.print("Please enter the user (online user) to whom you want to send the file: ");
                                    String fileGetterId = Utility.readString(50);
                                    System.out.print("Please enter the path to the file you want to send(e.g.,D:\\MyPhoto.jpg): ");
                                    String src = Utility.readString(100);
                                    System.out.print("Please enter the path where the recipient will receive the file(e.g., D:\\HerPhoto.jpg): ");
                                    String dest = Utility.readString(100);
                                    fileClientService.sendFileToOne(src, dest, userId, fileGetterId);
                                    break;
                                case "9":
                                    userClientService.logout();
                                    secondaryLoop = false;
                                    break;
                                default:
                                    System.out.println("Invalid input, please re-enter.");
                                    break;
                            }
                        }
                    } else {
                        System.out.println("=========Login failed=========");
                    }
                    break;
                case "9":
                    System.out.println("Thank you for using this system. Logging out....");
                    mainLoop = false;
                    break;
                default:
                    System.out.println("Invalid input, please re-enter.");
                    break;
            }
        }
    }
}

