---

# Java Multi-User Chat System

This is a Client/Server (C/S) instant messaging system built from scratch using Java Socket programming and multithreading. It supports core features like multiple concurrent users, private (1-to-1) messaging, group chats, file transfers, and server-wide broadcasts.

The primary goal of this project is to demonstrate a deep understanding and practical application of core Java technologies, including networking, concurrency, I/O streams, and robust error handling.

---

## Demo
### 1. Mass Messaging
![MassMessaging-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/6af3933b-99ad-4f5e-aeb6-921f5eb2dc21)

### 2. Private Messaging
![PrivateMessaging-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/ea8f1b5a-1f86-4157-8b36-51ce0f4cff4e)


## Features

* **User Authentication:** Secure C/S validation for user login.
* **Fetch Online User List:** Clients can request and display a real-time list of all online users.
* **Private Messaging (1-to-1):** Send private messages to a specific user.
* **Group Chat (Broadcast):** Send messages to all currently online users.
* **Server-side Broadcasts:** The server console can push system-wide announcements or news to all clients.
* **File Transfer:** Send files (images, documents, etc.) to another user.
* **Graceful Logout:** Clients can safely notify the server of their departure, which updates the online list for all other users.

## Tech Stack

* **Core Language:** Java
* **Networking:** `java.net.Socket`, `ServerSocket` (TCP/IP)
* **Concurrency:** `java.lang.Thread`, `java.util.concurrent.ConcurrentHashMap`
* **Object Serialization:** `ObjectInputStream`, `ObjectOutputStream` (for transmitting custom `Message` objects over the network)
* **Architecture:** Client/Server (C/S), Multithreaded Server Model
* **Development:** IntelliJ IDEA

## Architecture

This project uses a classic C/S architecture, heavily optimized for concurrency and network stream management.
<img width="1532" height="684" alt="未命名绘图 drawio" src="https://github.com/user-attachments/assets/b36c3432-2a0e-4702-9a86-870e9bfeaef2" />

### 1. Server-Side

* **Main Thread (`Server.java`):** Its sole responsibility is to listen for new client connections via `ServerSocket.accept()` in a loop.
* **Login Handler Thread:** Each new `Socket` is immediately **handed off** to a **new, temporary** thread. This thread handles I/O-blocking tasks like login validation, preventing the main thread from stalling and thus improving server throughput.
* **Communication Thread (`ServerConnectClientThread`):**
    * Upon successful login, the login handler thread creates a **complete set** of `ObjectInputStream/ObjectOutputStream` streams.
    * It then starts a dedicated `ServerConnectClientThread` and **transfers full ownership** of the `Socket` and these **pre-created streams** to it.
    * This thread runs in a `while(true)` loop, continuously listening for messages from its assigned client and handling all business logic (forwarding, broadcasting, etc.).
* **Thread Management (`ManageClientThreads`):**
    * Uses a `ConcurrentHashMap` to safely store all online users' `userId`s and their corresponding `ServerConnectClientThread` instances.
    * Provides thread-safe `add`, `remove`, and `get` methods for server operations.

### 2. Client-Side

* **Main Thread (`View.java`):** Renders the UI menu and accepts user keyboard input in a `while` loop.
* **Service Layer (`UserClientService`, etc.):** Responsible for encapsulating user actions (e.g., "send message") into `Message` objects.
* **Communication Thread (`ClientConnectServerThread`):**
    * Created **once** upon successful user login (`checkUser`).
    * This thread **owns** the **single** `ObjectInputStream` and `ObjectOutputStream` created during login.
    * It acts as the client's "ear," running in a `while(true)` loop (independent of the main UI thread) to **receive** incoming messages from the server (e.g., private messages, group chats) and display them to the console.
* **Message Protocol:**
    * Uses a `MessageType` (Enum) to define all possible message types (e.g., `MESSAGE_COMM_MES`, `MESSAGE_GET_ONLINE_FRIEND`), ensuring type-safety and high readability.
    * All communication is encapsulated within a `Message` object that implements `Serializable`.

## Key Technical Challenges & Optimizations

This project goes beyond just "making it work"—it solves several core challenges in Java network and concurrent programming.

### 1. Concurrency Safety: `HashMap` vs `ConcurrentHashMap`
* **Problem:** The server needs a collection to manage all client threads. A standard `HashMap` would cause `ConcurrentModificationException` or infinite loops during concurrent operations (e.g., a user logging in while another is logging out), crashing the server.
* **Solution:** Adopted `java.util.concurrent.ConcurrentHashMap` as the thread management container. It provides atomic `put`/`remove` operations and a thread-safe iterator, completely eliminating concurrency conflicts while maintaining high performance.

### 2. The Fatal Stream Bug: `StreamCorruptedException`
* **Problem:** This was the most critical bug. Repeatedly creating a new `ObjectInputStream` on the same `Socket` throws a `java.io.StreamCorruptedException: invalid stream header`. This is because the `ObjectInputStream` constructor expects to read a stream header, which is only sent *once* when the connection is established.
* **Solution:** Implemented a strict **"Create Once, Pass Always"** policy. On both the client and server, the `ObjectInputStream` and `ObjectOutputStream` are created **only one time** during the initial connection and validation. They are then passed to the dedicated communication thread (`ClientConnectClientThread` / `ServerConnectClientThread`) as member variables and are **reused** for the entire lifecycle of the socket.

### 3. Server Robustness: `SocketException` & 100% CPU
* **Problem:** If a client disconnects abnormally (e.g., network loss, force quit), the server's `ois.readObject()` throws a `SocketException` or `EOFException`. If the `catch` block is improper (e.g., just printing the error without `break`), the `while(true)` loop enters a busy-wait state, infinitely throwing and catching exceptions, and instantly spiking the server's CPU usage to 100%.
* **Solution:** The `ServerConnectClientThread`'s `run` method includes a specific `catch` block for `SocketException` and `EOFException`. Upon catching these "disconnection" exceptions, it immediately executes `break` to exit the loop, calls `ManageClientThreads.remove...` to clean up resources, and gracefully terminates the thread, ensuring server stability.

## Getting Started

### 1. Start the Server
Run the `main` method in `server.frame.Frame.java`.
You will see `Server listening on port 9999...`  in the console.

### 2. Run the Client (Multiple Instances)
1.  Run the `main` method in `client.view.View.java` to launch the first client.
2.  **To launch a second client:**
    * In IntelliJ IDEA, go to `Run` -> `Edit Configurations...`.
    * Find your `View` configuration.
    * Check the **"Allow parallel run"** box.
    * Click `Apply` and `OK`.
3.  Now you can click the green 'Run' button multiple times. Each click will open a new, independent client instance in a new tab.

### 3. Test Accounts
Use any of the following accounts to log in:

| Username (userId) | Password (passwd) |
| :--- | :--- |
| `100` | `123456` |
| `200` | `123456` |
| `300` | `123456` |
| `400` | `234567` |
| `500` | `234567` |
| `600` | `234567` |

## License

This project is licensed under the MIT License
