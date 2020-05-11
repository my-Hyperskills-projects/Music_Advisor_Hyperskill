package advisor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

/*
getNewReleases, getFeatured, getCategories, getPlaylist - делают запрос с полученым токеном, с помощью
соответствующих методов получает всю необходимую инфу, создают нужный обьект и записывают его в список,
когда все экземляры обработаны список возвращается
getArtistsNames, getSpotifyUrl, getName, getCategoryId - парсит JSON и возвращает нужную информацию
 */

public class InfoProvider {
    private static String api_server_path = "https://api.spotify.com";

    public static ArrayList<Album> getNewReleases(String access_token) {
        ArrayList<Album> newReleases = new ArrayList<>();
        String response = makeRequest(access_token, api_server_path + "/v1/browse/new-releases");

        JsonObject jo = JsonParser.parseString(response).getAsJsonObject();
        JsonObject albums = jo.get("albums").getAsJsonObject();
        JsonArray items = albums.get("items").getAsJsonArray();

        for (JsonElement element : items) {
            JsonObject object = element.getAsJsonObject();
            String[] artists = getArtistsNames(object);
            String albumsName = getName(object);
            String url = getSpotifyUrl(object);
            newReleases.add(new Album(artists, albumsName, url));
        }

        return newReleases;
    }

    public static ArrayList<Featured> getFeatured(String access_token) {
        ArrayList<Featured> featured = new ArrayList<>();
        String response = makeRequest(access_token, api_server_path + "/v1/browse/featured-playlists");

        JsonObject jo = JsonParser.parseString(response).getAsJsonObject();
        JsonObject albums = jo.get("playlists").getAsJsonObject();
        JsonArray items = albums.get("items").getAsJsonArray();

        for (JsonElement element : items) {
            JsonObject object = element.getAsJsonObject();
            String albumsName = getName(object);
            JsonObject owner = object.get("owner").getAsJsonObject();
            String url = getSpotifyUrl(owner);
            featured.add(new Featured(albumsName, url));
        }

        return featured;
    }

    public static ArrayList<Category> getCategories(String access_token) {
        ArrayList<Category> categories = new ArrayList<>();
        String response = makeRequest(access_token, api_server_path + "/v1/browse/categories");

        JsonObject jo = JsonParser.parseString(response).getAsJsonObject();
        JsonObject cInfo = jo.get("categories").getAsJsonObject();
        JsonArray items = cInfo.get("items").getAsJsonArray();

        for (JsonElement element : items) {
            JsonObject object = element.getAsJsonObject();
            String category = getName(object);
            String id = getCategoryId(object);
            categories.add(new Category(category, id));
        }

        return categories;
    }

    public static ArrayList<Playlist> getPlaylist(String categoryName, String access_token) {
        ArrayList<Playlist> playlists = new ArrayList<>();
        ArrayList<Category> categories = getCategories(access_token);
        String id = null;

        for (Category category : categories) {
            if (categoryName.contains(category.getName())) {
                id = category.getId();
            }
        }

        if (id != null) {
            String response = makeRequest(access_token, api_server_path + "/v1/browse/categories/" + id + "/playlists");

            JsonObject jo = JsonParser.parseString(response).getAsJsonObject();
            JsonObject pInfo = jo.get("playlists").getAsJsonObject();
            JsonArray items = pInfo.get("items").getAsJsonArray();

            for (JsonElement element : items) {
                JsonObject object = element.getAsJsonObject();
                String name = getName(object);
                String url = getSpotifyUrl(object);
                playlists.add(new Playlist(name, url));
            }
        }

        return playlists;
    }

    private static String makeRequest(String access_token, String address) {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + access_token)
                .uri(URI.create(address))
                .GET()
                .build();


        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return response.body();
    }

    private static String[] getArtistsNames(JsonObject object) {
        JsonArray artists = object.get("artists").getAsJsonArray();
        int len = artists.size();
        String[] result = new String[len];
        int i = 0;

        for (JsonElement el : artists) {
            JsonObject artist = el.getAsJsonObject();
            result[i] = artist.get("name").getAsString();
            i++;
        }

        return result;
    }

    private static String getSpotifyUrl(JsonObject object) {
        JsonObject externalUrls = object.get("external_urls").getAsJsonObject();
        return externalUrls.get("spotify").getAsString();
    }

    private static String getName(JsonObject object) {
        return object.get("name").getAsString();
    }

    private static String getCategoryId(JsonObject object) {
        return object.get("id").getAsString();
    }

    public static void setApiServerPath(String api_server_path) {
        InfoProvider.api_server_path = api_server_path;
    }
}
