

public class ClientAssistant {
    public static void main(String[] args) {
/**
 *  it will be used to run for infinite until ctrl c has been used in the cmd to abort the TCP.
 *  Also, it should be commented for running UDP Client(p2).
 */
        while (true) {
        try {
                TCPEchoClient client = new TCPEchoClient(args);
//            UDPEchoClient client = new UDPEchoClient(args); // Should be commented out for running UDP client (p2).
            client.loading();
            client.begin();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        }
    }
}