package response;

import server.ThreadScheduler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public abstract class Response {

    private final String fileValue;
    private final String fileExtension = "html";
    protected ThreadScheduler client;
    private String response;

    public Response(ThreadScheduler client, String header, String content) {   //constructor
        this.response = "HTTP/1.1 " + header + "\r\n";
        this.fileValue = "<html><body><h1>" + header + "</h1><p>" + content + "</p></body></html>";
        this.client = client;
    }

    /**
     * write the file
     *
     * @throws Exception
     */

    public void write() throws Exception {
        writeHeader(fileValue.getBytes().length, fileExtension);
        writeContent();
    }

    /**
     * writes header
     *
     * @param length
     * @param fileExtension
     * @throws Exception
     */
    protected void writeHeader(long length, String fileExtension) throws Exception {

        response += "Date: " + new Date().toString() + "\r\n";
        response += "Content-Length: " + length + "\r\n";
        response += "Content-HTTPMethods: " + getContentType(fileExtension) + "\r\n\r\n";

        PrintWriter printer = new PrintWriter(client.getSocket().getOutputStream(), true);
        printer.write(response);
        printer.flush();

    }

    /**
     * writes the content of the file
     * @throws IOException
     */

    private void writeContent() throws IOException {
        client.getSocket().getOutputStream().write(fileValue.getBytes());
    }

    /**
     * get the file tupe
     * @param fileExtension
     * @return value
     * @throws Exception
     */
    private String getContentType(String fileExtension) throws Exception {

        for (FileType type : FileType.values()) {
            for (String extension : type.fileSuffix) {
                if (fileExtension.equals(extension)) {
                    return type.value;
                }
            }
        }

        throw new Exception("unknown type");
    }


}