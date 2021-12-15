
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class UDPEchoClient {

    public int BUFSIZE =1024;
    public final int MYPORT = 0;
    public final String MSG = "An Echo Message!";
    public byte[] buf;
    public int transferRate;
    public String ipAddress;
    public int portNumber;
    public SocketAddress remoteBindPoint;
    public String receivedMSG;
    public DatagramSocket socket;

    public UDPEchoClient(String[] args) {
        /* look for the 4 arguments*/
        analyzeTheConditions(args);
        /* check the message which has been sent*/
        analyzeTheMessage(); //
        /* create local and remote bind points*/
        remoteBindPoint = new InetSocketAddress(ipAddress, portNumber);
    }

    /* It will cause a delay after each iteration for sending 5 messages*/
    public void slowDown() {
        try {
            Thread.sleep(1000 / transferRate);
        } catch (Exception e) {
            return;
        }
    }

    public void begin() {
        try {
            for (int i = 0; i < transferRate; i++) {
                deliverAndObtain();
                slowDown();
                /* It will perform a comparison*/
                analyzeMessages();
            }
            closeSocket();
            displayError("=====");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void analyzeMessages() {

        if (MSG.compareTo(receivedMSG) != 0) {
            displayError("An error has occurred (Mismatch results!!!!\n)");
            System.out.printf(" Delivered -> %d bytes , Obtained -> %d bytes , The size of array is -> %d bytes\n", MSG.length(), receivedMSG.length(), BUFSIZE);
        } else
            System.out.printf("\"The Number of bytes which has been sent and received is\" -> %d ,\"The size of the buffer is\" -> %d bytes\n", receivedMSG.length(), BUFSIZE);
    }


    public void loading() {
        try {
            /*Make a socket*/
            socket = new DatagramSocket(null);
            /* Create local endpoint using bind() */
            SocketAddress localBindPoint = new InetSocketAddress(MYPORT);
            socket.bind(localBindPoint);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private void deliverAndObtain() {
        try {
            /* create datagram packet for sending message*/
            DatagramPacket sendPacket = new DatagramPacket(MSG.getBytes(), MSG.length(), remoteBindPoint);

            /* Create datagram packet for receiving echoed message */
            DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);

            /* Send and receive message*/
            socket.send(sendPacket);
            socket.receive(receivePacket);
            receivedMSG = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void closeSocket() {
        socket.close();
    }

    private void analyzeTheConditions(String[] conditions) {

        if (conditions.length != 4) {
            displayError("Not all the arguments have been entered correctly!!!");
            System.exit(1);
        }

        analyzeIp(conditions[0]);
        analyzePortNumber(convertToNumber(conditions[1], "port"));
        analyzeBuffer(convertToNumber(conditions[2], "Buffer Size"));
        analyzeTransferRate(convertToNumber(conditions[3], "Message Transfer Rate"));
    }

    private void analyzeIp(String ip) {

        String[] IpSeparator = ip.split("\\.");

        if (IpSeparator.length != 4) {
            displayError("The provided IP address is not valid!!!!");
            System.exit(1);

        }
        for (int i = 0; i < IpSeparator.length; i++) {
            int converter = Integer.parseInt(IpSeparator[i]);
            if (converter > 255 || converter < 0) {
                displayError("The provided IP address is not valid!!!!");
                System.exit(1);
            } else {
                ipAddress = ip;
            }
        }
    }

    private void analyzePortNumber(int numberOfPort) {

        if (numberOfPort > 65535 || numberOfPort < 1) {
            displayError("The provided port number is not valid!!!!");
            System.exit(1);
        } else {
            this.portNumber = numberOfPort;
        }
    }

    private void analyzeBuffer(int bufferSize) {
        if (bufferSize < 1) {
            displayError("The provided buffer size is not considered valid!!!!");
            System.exit(1);
        } else {
            BUFSIZE = bufferSize;
        }

        try {
            buf = new byte[BUFSIZE];
        } catch (OutOfMemoryError e) {
            displayError("The provided buffer size is exceeding the limit!!!!");
            System.exit(1);
        }
    }

    private void analyzeTransferRate(int rate) {
        if (rate == 0) {
            transferRate = rate;
        } else if (rate < 0) {
            displayError("The provided message rate is not considered valid!!!!");
            System.exit(1);
        } else {
            transferRate = rate;
        }
    }


    private int convertToNumber(String str, String arg) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            System.err.printf("%s is not considered valid!", arg);
            System.exit(1);
            return -1;
        }
    }

    private void analyzeTheMessage() {
        if (MSG.length() > 65507 || MSG.isEmpty()) {
            displayError("The provided message is not considered valid!!!!");
            System.exit(1);
        }
    }

    private void displayError(String str) {
        System.err.println(str);
    }

}