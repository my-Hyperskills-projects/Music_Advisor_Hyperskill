package advisor;

import java.util.Arrays;

public class Album {
    private String[] artists;
    private String albumsName;
    private String url;

    public Album(String[] artists, String albumsName, String url) {
        this.artists = artists;
        this.albumsName = albumsName;
        this.url = url;
    }

    @Override
    public String toString() {
        return albumsName + "\n" + Arrays.toString(artists) + "\n" + url + "\n";
    }
}
