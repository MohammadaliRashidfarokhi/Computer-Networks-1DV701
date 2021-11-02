
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class UDPEchoServer {
    public static final int BUFSIZE = 1024;
    public static final int MYPORT = 4950;

    public static void main(String[] args) {

        try {
            byte[] buf = new byte[BUFSIZE];
            confirmation();


            /* Create socket */
            DatagramSocket socket = new DatagramSocket(null);

            /* Create local bind point */
            SocketAddress localBindPoint = new InetSocketAddress(MYPORT);
            socket.bind(localBindPoint);

            while (true) {
                /* Create datagram packet for receiving message */
                DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);

                /* Receiving message */
                socket.receive(receivePacket);

                String receivedMessages = new String(receivePacket.getData(), receivePacket.getOffset(),
                        receivePacket.getLength());

                /* Create datagram packet for sending message */
                DatagramPacket sendPacket = new DatagramPacket(receivePacket.getData(), receivePacket.getLength(),
                        receivePacket.getAddress(), receivePacket.getPort());

                /* Send message */
                socket.send(sendPacket);

                System.out.printf("\"UDP echo request from\" -> %s ,", receivePacket.getAddress().getHostAddress());
                System.out.printf(" \"using port\" -> %d\n", receivePacket.getPort());
                System.out.print("\"Received Message length\" -> " + receivedMessages.length() + " byte , \"Delivered Message length\" -> " + receivedMessages.length()
                        + " bytes , \"The Size of the Buffer is\" -> " + BUFSIZE);
                System.err.println("\n-----");


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