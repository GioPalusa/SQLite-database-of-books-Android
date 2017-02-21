package se.palusa.v5librarybooks;

/**
 * Created by Giovanni on 2017-02-14.
 * © Giovanni Palusa 2017
 */

class Books {

    private long id;
    private String name;
    private String author;

    String getAuthor() {
        return author;
    }

    void setAuthor(String author) {
        this.author = author;
    }

    long getId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }


}
