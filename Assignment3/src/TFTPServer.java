import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class TFTPServer {
    public static final int TFTPPORT = 4970;
    public static final int BUFSIZE = 516;
    public static final String READDIR = "src/read/"; //custom address at your PC
    public static final String WRITEDIR = "src/write/"; //custom address at your PC
    // OP codes
    public static final int OP_RRQ = 1;
    public static final int OP_WRQ = 2;
    public static final int OP_DAT = 3;
    public static final int OP_ACK = 4;
    public static final int OP_ERR = 5;

    public static void main(String[] args) {
        if (args.length > 0) {
            System.err.printf("usage: java %s\n", TFTPServer.class.getCanonicalName());
            System.exit(1);
        }
        //Starting the server
        try {
            TFTPServer server = new TFTPServer();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start() throws SocketException {
        byte[] buf = new byte[BUFSIZE];

        // Create socket
        DatagramSocket socket = new DatagramSocket(null);

        // Create local bind point
        SocketAddress localBindPoint = new InetSocketAddress(TFTPPORT);
        socket.bind(localBindPoint);

        System.out.printf("Listening at port %d for new requests\n", TFTPPORT);

        // Loop to handle client requests
        try {
            while (true) {
                final InetSocketAddress clientAddress = receiveFrom(socket, buf);
                // If clientAddress is null, an error occurred in receiveFrom()
                if (clientAddress == null)
                    continue;

                final StringBuffer requestedFile = new StringBuffer();
                final int reqtype = ParseRQ(buf, requestedFile);

                new Thread() {
                    public void run() {
                        try {
                            DatagramSocket sendSocket = new DatagramSocket(0);

                            // Connect to client
                            sendSocket.connect(clientAddress);

                            System.out.printf("%s request from %s using port %d\n",
                                    (reqtype == OP_RRQ) ? "Read" : "Write",
                                    clientAddress.getHostName(), clientAddress.getPort());

                            // Read request
                            if (reqtype == OP_RRQ) {
                                requestedFile.insert(0, READDIR);
                                HandleRQ(sendSocket, requestedFile.toString(), OP_RRQ, clientAddress.getPort());
                            }
                            // Write request
                            else {
                                requestedFile.insert(0, WRITEDIR);
                                HandleRQ(sendSocket, requestedFile.toString(), OP_WRQ, clientAddress.getPort());
                            }
                            sendSocket.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Reads the first block of data, i.e., the request for an action (read or write).
     *
     * @param socket (socket to read from)
     * @param buf    (where to store the read data)
     * @return socketAddress (the socket address of the client)
     */
    private InetSocketAddress receiveFrom(DatagramSocket socket, byte[] buf) throws Exception {

        // Create datagram packet
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        // Receive packet
        socket.receive(packet);
        // Get client address and port from the packet
        return new InetSocketAddress(packet.getAddress(), packet.getPort());
    }

    /**
     * Parses the request in buf to retrieve the type of request and requestedFile
     *
     * @param buf           (received request)
     * @param requestedFile (name of file to read/write)
     * @return opcode (request type: RRQ or WRQ)
     */
    private int ParseRQ(byte[] buf, StringBuffer requestedFile) {
        // See "TFTP Formats" in TFTP specification for the RRQ/WRQ request contents
        int opcode = (buf[0] << 8) | (buf[1] & 0x00ff);
        if (opcode == 1 || opcode == 2) {
            int position = fillTheGaspWithInformation(2, buf, requestedFile);
            StringBuffer mode = new StringBuffer();
            fillTheGaspWithInformation(position, buf, mode);
        }
        return opcode;
    }

    private int fillTheGaspWithInformation(int position, byte[] buf, StringBuffer requestedFile) {
        char i;
        while (true) {
            if (0 == (i = (char) buf[position])) break;
            requestedFile.append(i);
            position++;
        }
        return ++position;
    }

    /**
     * Handles RRQ and WRQ requests
     *
     * @param sendSocket    (socket used to send/receive packets)
     * @param requestedFile (name of file to read/write)
     * @param opcode        (RRQ or WRQ)
     */

    private void HandleRQ(DatagramSocket sendSocket, String requestedFile, int opcode, int port) throws IOException {
        // See "TFTP Formats" in TFTP specification for the DATA and ACK packet contents
        File file = new File(requestedFile);
        if (opcode == OP_RRQ) {

            if (!(file.exists() && file.isFile())) {
                System.out.println("The File was not found");

            }

            byte[] bytes = Files.readAllBytes(Paths.get(requestedFile));

            int block = 1;
            int calculate = (bytes.length / 512) + 1;
            int theProblem = 0;
            byte[] buffer = obtainingTheInformation(block, bytes);

            while (block <= calculate) {
                int consideringTheBlock = send_DATA_receive_ACK(sendSocket, buffer, port);

                if (consideringTheBlock > 0) {
                    if (consideringTheBlock == block) {
                        block++;
                        theProblem = 0;
                        if (block <= calculate)
                            buffer = obtainingTheInformation(block, bytes);
                    } else if (consideringTheBlock + 1 <= calculate) {
                        buffer = obtainingTheInformation(consideringTheBlock + 1, bytes);
                        theProblem++;
                    }
                } else theProblem++;
            }
        } else if (opcode == OP_WRQ) {
            if (file.exists() && file.isFile()) {
                System.err.println("The file exists already");
            }

            FileOutputStream fileOutputStream = new FileOutputStream(requestedFile);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int block = 0;
            int problem = 0;

            ByteBuffer allocate = ByteBuffer.allocate(4);
            allocate.putShort((short) OP_ACK);
            allocate.putShort((short) block);
            byte[] acknowledgement = (allocate.array());
            int allInAll = 0;

            while (true) {

                int analyzing = 0;
                if ((analyzing = receive_DATA_send_ACK(sendSocket, acknowledgement, byteArrayOutputStream, port)) < 0) {
                    problem++;
                } else {
                    problem = 0;
                    allInAll += analyzing;

                }
                if (analyzing >= 512 || analyzing == -1) {
                    continue;
                }
                break;
            }

            if (problem < 3) {
                fileOutputStream.write(byteArrayOutputStream.toByteArray());
                fileOutputStream.flush();
                fileOutputStream.close();
                byteArrayOutputStream.close();
                sendSocket.send(new DatagramPacket(acknowledgement, acknowledgement.length));
                return;
            }
        } else {
            System.err.println("Invalid request. Sending an error packet.");
            // See "TFTP Formats" in TFTP specification for the ERROR packet contents
            // send_ERR(params);
            return;
        }
    }

    private byte[] obtainingTheInformation(int block, byte[] information) {
        int capacity = 512;
        if (capacity * block > information.length) {
            capacity = information.length - capacity * (block - 1);
        }
        ByteBuffer allocate = ByteBuffer.allocate(capacity + 4);
        allocate.putShort((short) OP_DAT);
        allocate.putShort((short) block);
        byte[] bytes = allocate.array();
        if (capacity >= 0) System.arraycopy(information, (block - 1) * 512, bytes, 4, capacity);
        return bytes;
    }

    /**
     * To be implemented
     */
    private int send_DATA_receive_ACK(DatagramSocket socket, byte[] bytes, int port) {
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
        try {
            socket.send(packet);
            byte[] obtain = new byte[BUFSIZE];

            try {
                DatagramPacket packet1 = new DatagramPacket(obtain, obtain.length);
                socket.receive(packet1);

                if (packet1.getPort() == port) {
                    int op_code = ParseRQ(obtain, null);
                    if (op_code == OP_ACK) return ((obtain[2] << 8) | (obtain[3] & 0x00ff));
                    else return -1;
                } else {
                    return -2;
                }

            } catch (SocketTimeoutException te) {
                return -1;
            }
        } catch (IOException e) {
            return -1;
        }
    }

    private int receive_DATA_send_ACK(DatagramSocket socket, byte[] acknowledge, ByteArrayOutputStream takeNote, int port) {
        try {
            socket.send(new DatagramPacket(acknowledge, acknowledge.length));
            byte[] bytes = new byte[512 + 4];
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
            socket.receive(packet);

            if (packet.getPort() == port) {
                if (ParseRQ(bytes, new StringBuffer()) == OP_DAT) {
                    byte[] information = Arrays.copyOfRange(packet.getData(), 4, packet.getLength());
                    takeNote.write(information);
                    takeNote.flush();
                    if (information.length < 512) socket.close();
                    return information.length;
                } else return -1;
            } else {
                return -2;
            }
        } catch (IOException e) {
            return -1;
        }
    }
    //  private void send_ERR(int errorCode, String error_msg) {
    //  }
}