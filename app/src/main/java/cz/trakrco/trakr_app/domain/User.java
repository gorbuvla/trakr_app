package cz.trakrco.trakr_app.domain;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vlad on 03/07/2017.
 */
@IgnoreExtraProperties
@DatabaseTable(tableName = "users")
public class User {

    @DatabaseField(id = true)
    private String uid;

    @DatabaseField
    private String email;

    private int counter;

    @ForeignCollectionField
    private ForeignCollection<Output> outputs;

    public User() {}

    public User(String uid, String email) {
        this.uid = uid;
        this.email = email;
        this.counter = 0;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public ForeignCollection<Output> getOutputs() {
        return outputs;
    }

    public void setOutputs(ForeignCollection<Output> outputs) {
        this.outputs = outputs;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("email", email);
        map.put("counter", counter);
        return map;
    }
}
