package advisor;

public class Featured {
    private String albumsName;
    private String url;

    public Featured(String albumsName, String url) {
        this.albumsName = albumsName;
        this.url = url;
    }

    @Override
    public String toString() {
        return albumsName + "\n" + url + "\n";
    }
}