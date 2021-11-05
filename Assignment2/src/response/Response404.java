package response;

import server.ThreadScheduler;

public class Response404 extends Response {

    public Response404(ThreadScheduler client) {
        super(client, "404 Not Found", "The requested file or page is not accessible.");
    }
}