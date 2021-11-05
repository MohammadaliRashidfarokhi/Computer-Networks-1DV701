package response;

import server.ThreadScheduler;

public class Response500 extends Response {

    public Response500(ThreadScheduler client) {
        super(client, "500 Internal Server Error", " a \"server-side\" error has happened");
    }
}