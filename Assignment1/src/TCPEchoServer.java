

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPEchoServer {

    public static final int MYPORT = 4950;
    public static int threadId = 0;

    public static void main(String[] args) {
        try {

            /* make a  server socket*/
            ServerSocket serverSocket = new ServerSocket(MYPORT);
            confirmation();

            while (true) {

                Socket socket = serverSocket.accept();
                /*making a a thread for client */
                TCPClientThread client = new TCPClientThread(socket, threadId++);
                client.run();

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void confirmation() {
        System.err.println("                         " +
                "                                  " +
                "                      " +
                "===== The server has started successfully =====");

    }
}

class TCPClientThread implements Runnable {

    public final int BUFSIZE =1024;
    public Socket socket;
    public InputStream inputStream;
    public OutputStream outputStream;
    public String receivedMessage;
    public int threadIdentification;

    public TCPClientThread(Socket socket, int threadIdentifier) {
        try {
            this.socket = socket;
            this.threadIdentification = threadIdentifier;

            inputStream = new DataInputStream(this.socket.getInputStream());
            outputStream = new DataOutputStream(this.socket.getOutputStream());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.printf("The connection has been started with the following address -> %s The port Number is -> %d\n\n\n\n", socket.getInetAddress().getHostAddress(),
                socket.getPort());
        byte[] buffer = new byte[BUFSIZE];
        try {
            do {
                StringBuilder stringBuilder = new StringBuilder();
                do {
                    receivedMessage = "";

                    /* it has the responsibility of reading and collecting data.*/
                    int reader = inputStream.read(buffer);

                    /* it will read look at the buffer as a string*/
                    if (reader > -1)
                        receivedMessage = new String(buffer, 0, reader);
					/* If the size of the buffer was smaller compare to the message which has been sent,
					 it will append the obtained message to a string builder*/
                    stringBuilder.append(receivedMessage);
                } while (inputStream.available() > 0);

                receivedMessage = stringBuilder.toString();

                if (!receivedMessage.isEmpty()) {
                    outputStream.write(receivedMessage.getBytes());
                    System.out.printf("\"Client identification\" -> %d , \"TCP echo request from\" -> %s ,", threadIdentification, socket.getInetAddress().getHostAddress());
                    System.out.printf("\"using port\" -> %d\n", socket.getPort());
                    System.out.print("\"Received Message length\" -> " + receivedMessage.length() + " byte , \"Delivered Message length\" -> " + receivedMessage.length()
                            + " bytes , \"The Size of the Buffer is\" -> " + BUFSIZE);
                    System.err.println("\n-----");

                }

            } while (!receivedMessage.isEmpty());
            closeSocket();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeSocket() throws Exception {
        socket.close();
    }
}
