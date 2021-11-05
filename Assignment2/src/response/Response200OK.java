package response;

import server.ThreadScheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Response200OK extends Response {

    private final File file;

    public Response200OK(ThreadScheduler client, File file) {
        super(client, "200 OK", "");
        this.file = file;
    }


    @Override
    public void write() throws Exception {

        String[] parts = file.getName().split("\\.");
        super.writeHeader(file.length(), parts[parts.length - 1]);
        writeFile();
    }


    private void writeFile() throws IOException {

        FileInputStream in = new FileInputStream(file);
        OutputStream out = super.client.getSocket().getOutputStream();

        int bytesRead = 0;
        while ((bytesRead = in.read(super.client.getBuffer())) != -1) {
            out.write(super.client.getBuffer(), 0, bytesRead);
        }
        in.close();

        System.out.println("Client " + super.client.getClientId() + " got " + file.getName());
    }
}