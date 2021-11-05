package response;

import server.ThreadScheduler;

public class Response302 extends Response {

    public Response302(ThreadScheduler client) {
        super(client, "302 URL redirection",
                " The requested resource has been temporarily moved to a different URI");
    }
}