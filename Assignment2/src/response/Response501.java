package response;

import server.ThreadScheduler;

public class Response501 extends Response {

    public Response501(ThreadScheduler client) {
        super(client, "501 Not Implemented",
                "The server either does not recognize the request method, or it lacks the ability to fulfill the"
                        + " request. The sever only support GET, POST and PUT methods");
    }
}