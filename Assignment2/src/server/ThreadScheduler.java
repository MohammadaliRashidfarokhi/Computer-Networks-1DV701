package server;

import response.HTTPMethods;
import response.ResponseInvoker;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ThreadScheduler extends Thread {

    private final Socket socket;
    private ResponseInvoker responseInvoker;
    private RequestValidator requestValidator;
    private byte[] buffer;
    private final int clientId;
    private final int timeOutInMs = 10000;

    public ThreadScheduler(Socket socket, int clientId) { //constructor for ThreadScheduler
        this.socket = socket;
        requestValidator = new RequestValidator();
        buffer = new byte[9000];
        this.clientId = clientId;
        responseInvoker = new ResponseInvoker(this);
        System.out.println("The Client with following id has been connected " + ", ID : "+ clientId );
    }

    @Override
    public void run() {

        while (true) {
            try {

                socket.setSoTimeout(timeOutInMs);
                requestValidator = requestValidator.parse(new BufferedReader(new InputStreamReader(socket.getInputStream())));
                responseInvoker.getResponse(requestValidator).write();

            } catch (FileNotFoundException e) {

                try {
                    responseInvoker.displayError404NotFound();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;

            }catch (SecurityException e) {
                try {
                    responseInvoker.displayError403Forbidden();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;

            }catch (IOException|NullPointerException e) {
                try {
                    responseInvoker.Display500InternalServerError();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;

            }catch (NoSuchFieldException|RuntimeException e){
                    try {
                        responseInvoker.displayError302();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    break;

            } catch (Exception e) {

                break;

            }
            if (requestValidator.connectionClosed() || requestValidator.getMethodType() != HTTPMethods.GET) {
                break;
            }
        }

        try {
            socket.close();
            System.out.println("Socket has been closed!!!");
        } catch (IOException e) {
            try {
                responseInvoker.Display500InternalServerError();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        System.out.println("The Client with following id has been disconnected " + ", ID : "+ clientId);
        System.out.println("-------");
    }

    /**
     * Get method for socket
     * @return socket
     */

    public Socket getSocket() {
        return socket;
    }

    /**
     * get method for buffer
     * @return buffer array
     */

    public byte[] getBuffer() {
        return buffer;
    }

    /**
     * get method for client Id
     * @return client id
     */
    public int getClientId() {
        return clientId;
    }
}