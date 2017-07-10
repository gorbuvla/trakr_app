package cz.trakrco.trakr_app.domain;

/**
 * Created by vlad on 05/07/2017.
 */

public class Location {

    private String id;
    private String address;

    public Location(String id, String address) {
        this.id = id;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }
}
