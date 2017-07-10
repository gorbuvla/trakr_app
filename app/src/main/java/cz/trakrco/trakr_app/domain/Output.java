package cz.trakrco.trakr_app.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vlad on 02/07/2017.
 */
@IgnoreExtraProperties
@DatabaseTable(tableName = "outputs")
public class Output implements Parcelable {

    @DatabaseField(generatedId = true)
    Integer dbid;

    String oid;

    @DatabaseField
    String name;

    @DatabaseField
    String address;

    @DatabaseField
    Double latitude;

    @DatabaseField
    Double longitude;

    @DatabaseField
    Integer time;

    @DatabaseField
    Long timestamp;

    @DatabaseField
    String mode;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "user_id")
    User user;

    public Output() {

    }

    public Output(String name, String address, Double latitude, Double longitude, String mode, Integer time, User user) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.mode = mode;
        this.user = user;
        this.timestamp = System.currentTimeMillis();
    }

    protected Output(Parcel in) {
        oid = in.readString();
        name = in.readString();
        address = in.readString();
        mode = in.readString();
    }

    public String getId() {
        return oid;
    }

    public void setId(String oid) {
        this.oid = oid;
    }

    public Integer getDbid() {
        return dbid;
    }

    public void setDbid(Integer dbid) {
        this.dbid = dbid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("oid", oid);
        map.put("uid", user.getUid());
        map.put("name", name);
        map.put("address", address);
        map.put("latitude", latitude);
        map.put("longitude", longitude);
        map.put("mode", mode);
        map.put("time", time);
        map.put("timestamp", timestamp);
        return map;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(oid);
        parcel.writeString(user.getUid());
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(mode);
        parcel.writeInt(time);
        parcel.writeLong(timestamp);
    }


    public static final Creator<Output> CREATOR = new Creator<Output>() {
        @Override
        public Output createFromParcel(Parcel in) {
            return new Output(in);
        }

        @Override
        public Output[] newArray(int size) {
            return new Output[size];
        }
    };

}
