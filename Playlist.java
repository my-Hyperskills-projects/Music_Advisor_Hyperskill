package advisor;

public class Playlist {
    private String name;
    private String url;

    public Playlist(String name, String url) {
        this.name = name;
        this.url = url;
    }

    @Override
    public String toString() {
        return name + "\n" + url + "\n";
    }
}
