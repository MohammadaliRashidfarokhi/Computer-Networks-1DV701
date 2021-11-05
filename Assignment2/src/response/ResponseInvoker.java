package response;

import server.Server;
import server.RequestValidator;
import server.ThreadScheduler;

import java.io.IOException;

public class ResponseInvoker {

    private Server method;
    private ThreadScheduler client;

    public ResponseInvoker(ThreadScheduler client) { //constructor
        method = new Server();
        this.client = client;
    }

    /**
     *
     * @param requestValidator
     * @return
     * @throws Exception
     */
    public Response getResponse(RequestValidator requestValidator) throws Exception {

        if (requestValidator.getMethodType()== HTTPMethods.GET){
            return new Response200OK(client, method.GET(requestValidator.getPath()));
        }else {
            return new Response501(client);
        }

    }

    /**
     * invoke display error 404
     */
    public void displayError404NotFound() throws Exception {
        write(new Response404(client));
    }

    /**
     * invoke method for display error 500
     * @throws Exception
     */
    public void Display500InternalServerError() throws Exception {
        write(new Response500(client));
    }

    /**
     *  invoke method for display error 403
     * @throws Exception
     */
    public void displayError403Forbidden() throws Exception {
        write(new Response403(client));
    }

    /**
     * invoke method for display error 302
     * @throws Exception
     */
    public void displayError302() throws Exception{
        write(new Response302(client));
    }

    /**
     * write the specific response
     * @param response
     * @throws Exception
     */
    private void write(Response response) throws Exception {
        try {
            response.write();
        } catch (IOException e) {
            System.out.println(e.getMessage());

        }
    }
}