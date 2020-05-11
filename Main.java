package advisor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Scanner;

/*
authorize - запускает сервер и в цикле ожидает ответа, когда ответ получен, результат присваивается переменной
access_code, а авторизация считается успешной, после чего серве завершает работу
makeRequest - делает запрос используя полученый код и прочие данные, получая JSON файл, который содержит access_token
getAccessToken - полученый JSON парситься и получаеться access_token
 */

public class Main {
    static boolean isExit = false;
    static String access_server = "https://accounts.spotify.com";
    static final String CLIENT_ID = "1dcaf4675c1f43f7a91bcc1bd2e94a13";
    static final String CLIENT_SECRET = "19aa2930df1f40cc98a31b24eff95b33";
    static String redirect_uri = "http://localhost:8080";
    static boolean isAuthorized = false;
    static String access_code;
    static String responseJson;
    static String access_token;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String standardAnswer = "Please, provide access for application.";

        for (int i = 0; i < args.length; i += 2) {
            if ("-access".equals(args[i])) {
                access_server = args[i + 1];
            }
            if ("-resource".equals(args[i])) {
                InfoProvider.setApiServerPath(args[i + 1]);
            }
            if ("-page".equals(args[i])) {
                ViewController.setCountOnPage(Integer.parseInt(args[i + 1]));
            }
        }

        while (!isExit) {
            String input = scanner.nextLine();

            if (input.equals("auth")) {
                authorize();
                if (access_code != null) {
                    System.out.println("code received");
                    System.out.println("Making http request for access_token...");
                    makeRequest();
                }
                if (isAuthorized) {
                    getAccessToken();
                    System.out.println("Success!");
                }
                else System.out.println("Error!");
            } else if (input.equals("exit")) {
                isExit = true;
                break;
            } else if (!isAuthorized) {
                System.out.println(standardAnswer);
            } else {
                switch (input) {
                    case "new":
                        for (Object release : ViewController.getNewReleases(access_token)) {
                            System.out.println(release);
                        }
                        System.out.println(ViewController.getInfoAboutPages());
                        break;
                    case "featured":
                        for (Object featured : ViewController.getFeatured(access_token)) {
                            System.out.println(featured);
                        }
                        System.out.println(ViewController.getInfoAboutPages());
                        break;
                    case "categories":
                        for (Object category : ViewController.getCategories(access_token)) {
                            System.out.println(category);
                        }
                        System.out.println(ViewController.getInfoAboutPages());
                        break;
                    case "next":
                        try {
                            for (Object object : ViewController.nextPage()) {
                                System.out.println(object);
                            }
                            System.out.println(ViewController.getInfoAboutPages());
                        } catch (NullPointerException e) {
                            System.out.println("No more pages.");
                        }
                        break;
                    case "prev":
                        try {
                            for (Object object : ViewController.prevPage()) {
                                System.out.println(object);
                            }
                            System.out.println(ViewController.getInfoAboutPages());
                        } catch (NullPointerException e) {
                            System.out.println("No more pages.");
                        }
                        break;
                    default:
                        if (input.startsWith("playlists")) {
                            String playlistsName = input.substring(10);
                            List<?> objects = ViewController.getPlaylist(playlistsName, access_token);

                            if (objects.size() != 0) {
                                for (Object playlist : objects) {
                                    System.out.println(playlist);
                                }
                                System.out.println(ViewController.getInfoAboutPages());
                            } else System.out.println("Unknown category name.");
                        }
                }
            }
        }
    }

    static void authorize() {

        Serverfy server = new Serverfy();
        server.start();
        String query = ""; //Тело ответа от spotify (code)

        System.out.println("use this link to request the access code:");
        System.out.println(access_server + "/authorize?client_id=" + CLIENT_ID + "&redirect_uri=" + redirect_uri + "&response_type=code");
        System.out.println("waiting for code...");

        while (true) {
            query = server.getQuery();

            if (query != null && query.startsWith("code=")) { //если код возвращается
                isAuthorized = true;
                access_code = query;
                break;
            } else if (query != null) { //если происходит ошибка
                break;
            } else { //если ответа еще нет
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        server.stop(1);
    }

    static void makeRequest() {
        HttpClient client = HttpClient.newBuilder().build();

        //Запрос
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(access_server + "/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString("client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&grant_type=authorization_code&" + access_code + "&redirect_uri=http://localhost:8080"))
                .build();

        //Ответ клиенту на основе запроса
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        //System.out.println("response:");
        //System.out.println(response.body());
        responseJson = response.body();
    }

    static void getAccessToken() {
        JsonObject jo = JsonParser.parseString(responseJson).getAsJsonObject();
        access_token = jo.get("access_token").getAsString();
    }

}
