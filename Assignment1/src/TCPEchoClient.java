

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class TCPEchoClient {

    public Socket socket;
    public DataInputStream inputStream;
    public DataOutputStream outputStream;
    public int BUFSIZE = 1024;
    public final int MYPORT = 0;
    public final String MSG = "An Echo Message!";
    public byte[] buf;
    public int transferRate;
    public String ipAddress;
    public int portNumber;
    public SocketAddress remoteBindPoint;
    public String receivedMSG;


    public TCPEchoClient(String[] args) {
        /* look for the 4 arguments*/
        analyzeTheConditions(args);
        /* check the message which has been sent*/
        analyzeTheMessage();//
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

    protected void begin() {
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

    protected void loading() {
        try {

            socket = new Socket();
            SocketAddress localBindPoint = new InetSocketAddress(MYPORT);
            /* Bind the socket */
            socket.bind(localBindPoint);
            socket.connect(remoteBindPoint);

            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    protected void deliverAndObtain() {
        try {
            /* try to make connection with the server*/
            outputStream.write(MSG.getBytes());

            StringBuilder stringBuilder = new StringBuilder();

            do {
                int reader = inputStream.read(buf);
                receivedMSG = new String(buf, 0, reader);
                stringBuilder.append(receivedMSG);
            } while (inputStream.available() > 0);

            receivedMSG = stringBuilder.toString();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    protected void analyzeMessages() {

        if (MSG.compareTo(receivedMSG) != 0) {
            displayError("An error has occurred (Mismatch results!!!!\n)");
            System.out.printf(" Delivered -> %d bytes , Obtained -> %d bytes , The size of array is -> %d bytes\n", MSG.length(), receivedMSG.length(), BUFSIZE);
        } else
            System.out.printf("\"The Number of bytes which has been sent and received is\" -> %d ,\"The size of the buffer is\" -> %d bytes\n", receivedMSG.length(), BUFSIZE);
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

    public void analyzeIp(String ip) {

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

    public void analyzePortNumber(int numberOfPort) {

        if (numberOfPort > 65535 || numberOfPort < 1) {
            displayError("The provided port number is not valid!!!!");
            System.exit(1);
        } else {
            this.portNumber = numberOfPort;
        }
    }

    public void analyzeBuffer(int bufferSize) {
        if (bufferSize < 1) {
            displayError("The provided buffer size is not considered valid!!!!");
            System.exit(1);
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


    protected void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected void analyzeTheMessage() {
        if (MSG.length() > 65507 || MSG.isEmpty()) {
            displayError("The provided message is not considered valid!!!!");
            System.exit(1);
        }
    }

    public void displayError(String str) {
        System.err.println(str);
    }

}