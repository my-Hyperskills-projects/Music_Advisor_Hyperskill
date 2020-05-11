package advisor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/*
Обертка для используемого сервера, который принимает код доступа от Spotify и присваевает его переменной query
 */

public class Serverfy {
    private HttpServer server;
    private String query;

    public Serverfy() {
        try {
            server = HttpServer.create();
            server.bind(new InetSocketAddress(8080), 0);
            server.createContext("/", new HttpHandler() {
                public void handle(HttpExchange exchange) throws IOException {
                    query = exchange.getRequestURI().getQuery();

                    String hello;
                    if (query != null && query.startsWith("code=")) {
                        hello = "Got the code. Return back to your program.";
                    }
                    else {
                        hello = "Not found authorization code. Try again.";
                    }
                    exchange.sendResponseHeaders(200, hello.length());
                    exchange.getResponseBody().write(hello.getBytes());
                    exchange.getResponseBody().close();
                    exchange.close();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.setExecutor(null);
    }

    public void start() {
        server.start();
    }

    public void stop(int i) {
        server.stop(i);
    }

    public String getQuery() {
        return query;
    }
}
