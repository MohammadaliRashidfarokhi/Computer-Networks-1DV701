package server;

import response.HTTPMethods;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestValidator {

    //private String Methodtype;
    private String path;
    private final String HTTPVersion = "HTTP/1.1";
    private boolean connectionTerminated = false;
    private long contentLength;
    private String body;
    private String uploadedFileName;
    private String uploadedFileExtension;
    private BufferedReader reader;
    private HTTPMethods type;


    public RequestValidator() {
    }


    public RequestValidator parse(BufferedReader read) throws Exception {
        this.reader = read;
        this.contentLength = 0;
        String[] info = checkHttpHeader(readHttp(reader));
        String body = readBody();


        if (contentLength > 0) {
            convertBody(readBody());
        }

        return new RequestValidator(getMethodType(info[0]), path, body, connectionTerminated, uploadedFileName, uploadedFileExtension);
    }

    /**
     * getting http methods
     * @param type
     * @return
     */

    public HTTPMethods getMethodType(String type) {
        for (HTTPMethods method : HTTPMethods.values()) {
            if (type.equals(method.name())) {
                return method;
            }

        }
        //bad request...
        return null;
    }

    /**
     * checking header
     * @param header
     * @return requestline array
     * @throws Exception
     */


    public String[] checkHttpHeader(String header) throws Exception {

        String[] lineSplitter = header.split("\r\n");
        String[] requestLine = lineSplitter[0].split(" ");
        if (!requestLine[2].equals(HTTPVersion)) { // checking for http version

            throw new Exception("Wrong Http format");
        }

        for (int i = 1; i < lineSplitter.length; i++) {
            if (lineSplitter[i].startsWith("Connection")) { // checking for connection status
                this.connectionTerminated = lineSplitter[i].split(": ")[1].equals("close");
                break;
            }
        }

        if (type != HTTPMethods.PUT) {
            this.type = Server.getEnumype(requestLine[0]);
        }

        if (requestLine.length != 3) {
            throw new Exception();
        }

        if (!requestLine[2].equals(HTTPVersion)) {
            throw new Exception();
        }


        this.path = requestLine[1];
        return requestLine;
    }

    /**
     *
     * @param reader
     * @return header
     * @throws IOException
     * @throws NumberFormatException
     */

    private String readHttp(BufferedReader reader) throws IOException, NumberFormatException {

        StringBuilder header = new StringBuilder();

     //   if (header.toString().isEmpty())throw new IOException();  uncomment to get error 500....

        while (true) {

            String line = reader.readLine();

            if (line == null || line.equals("\r\n") || line.isEmpty() || line.equals("")) {
                break;
            }

            header.append(line + "\r\n");

            if (line.startsWith("Content-Length")) {
                contentLength = Integer.parseInt(line.substring(16));
            }
        }

        return header.toString();
    }

    /**
     * method for parsing body
     * @param body
     * @throws ArrayIndexOutOfBoundsException
     */

    private void convertBody(String body) throws ArrayIndexOutOfBoundsException {

        if (body.startsWith("Server-PUT")) {
            //this.Methodtype = HTTPMethods.PUT;
            this.uploadedFileName = body.split("=")[0].split("Method-PUT")[1];
        } else {
            this.uploadedFileName = body.split("=")[0];
        }

        this.body = body.split("base64,")[1];
        this.uploadedFileExtension = body.split(":")[1].split(";")[0].split("/")[1];
    }

    /**
     * reads body
     * @return
     */
    public String readBody() {

        StringBuilder data = new StringBuilder();

        for (int i = 0; i < contentLength; i++) {
            try {
                data.append((char) reader.read());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return data.toString();
    }


    private RequestValidator(HTTPMethods type, String path, String body, boolean connectionTerminated, String uploadedFileName,
                             String uploadedFileExtension) {
    // constructor
        this.type = type;
        this.path = path;
        this.body = body;
        this.connectionTerminated = connectionTerminated;
        this.uploadedFileName = uploadedFileName;
        this.uploadedFileExtension = uploadedFileExtension;
    }

    /**
     * get method type
     * @return type
     *
     */
    public HTTPMethods getMethodType() {
        return type;
    }

    /**
     * get the directory
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * check for connection status
     * @return boolean for connection termination
     */
    public boolean connectionClosed() {
        return connectionTerminated;
    }

}