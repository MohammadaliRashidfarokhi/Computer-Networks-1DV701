package response;

import server.ThreadScheduler;

public class Response403 extends Response {

    public Response403(ThreadScheduler client) {
        super(client, "403 Forbidden",
                " Access to the requested resource is forbidden.");
    }
}