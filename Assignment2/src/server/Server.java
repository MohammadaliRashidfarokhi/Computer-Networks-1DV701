package server;

import response.HTTPMethods;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Server {


    public static String publicPath;
    private final String slash = "/";
    private final File DataFiles = new File(publicPath);
    private static int myPort;

    public static void main(String[] args) {

        try {
            int clientId = 0;
            if (args.length != 2) {
                System.out.println("2 arguments must be provided!");
                System.out.println("Usage: Server args[port] args[path]");
                System.exit(1);
            }

            if (validPort(args[0])){
                myPort=Integer.parseInt(args[0]);
            }
            else {
                System.out.println("Invalid port number");
                System.exit(1);
            }

            if (validatePath(args[1])) {
                publicPath = args[1];
            }
            else
                System.out.println("Provided path does not exist");


            ServerSocket Server = new ServerSocket(myPort);
            System.out.println("Server is accepting connection from clients on port: " + myPort + "\n" + "Path: " + publicPath);


            while (true) {
                Socket server = Server.accept();
                ThreadScheduler myServer = new ThreadScheduler(server, ++clientId);
                myServer.start();
            }
        } catch (IndexOutOfBoundsException | NumberFormatException | IOException e) { // in case of invalid input
            System.out.println("2 arguments must be provided!");
            System.out.println("Usage: Server args[port] args[path]");
        }
    }

    /**
     *
     * @param method
     * @return type
     * @throws Exception
     */

    public static HTTPMethods getEnumype(String method) throws Exception {

        for (HTTPMethods type : HTTPMethods.values()) {
            if (method.equals(type.name())) {
                return type;
            }
        }
        throw new Exception();
    }

    /**
     *
     * @param path
     * @return file
     * @throws Exception
     */

    public File GET(String path)
            throws Exception {

        if (path.equals("/")) {
            path += "index.htm";

        }

        if (path.endsWith("htm") && !new File(DataFiles, path).exists()) {
            path += "l";
        } else if (path.endsWith("html") && !new File(DataFiles, path).exists()) {
            path = path.substring(0, path.length() - 1);
        } else if (path.charAt(path.length() - 1) != '/' && path.split("\\.").length == 0) {
            path += "/";
        }

        File file = new File(DataFiles, path);

        if (file.isDirectory()) {
            for (int i = 0; i < file.listFiles().length; i++) {
                if (file.listFiles()[i].getName().equals("index.htm")
                        || file.listFiles()[i].getName().equals("index.html")) {
                    file = file.listFiles()[i];
                    break;
                }
            }
        }


        if (path.endsWith("forbidden")) {
            throw new SecurityException();
        } else if (path.endsWith("unavailable")) {
            throw new FileNotFoundException();
        } else if (path.endsWith("redirection")) {
            throw new NoSuchFieldException();
        }


        if (!file.isDirectory() && file.exists()) {
            return file;
        } else {
            throw new FileNotFoundException();
        }
    }
    private static boolean validPort(String port) {
        try {
            int portAsInteger = Integer.parseInt(port);
            if (portAsInteger < 0 || portAsInteger > 65535) // Port cannot be less than 0 or more than 65535
                return false;
        } catch (NumberFormatException e) {
            return false; // If the port is not parsable to int, valid = false.
        }
        return true;
    }
    private static boolean validatePath(String pathString) {
        Path path = Paths.get(pathString);
        if (Files.notExists(path) || !Files.isDirectory(path))
            return false;

        return true;
    }

}