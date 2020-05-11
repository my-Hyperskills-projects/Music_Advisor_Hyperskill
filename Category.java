package advisor;

public class Category {
    private String name;
    private String id;

    public Category(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
