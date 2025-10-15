package edu.stevens.cs522.chatserver.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.Instant;

/**
 * Created by dduggan.
 */

@Entity(indices = {@Index(value = {"name"}, unique = true)})
public class Peer implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;

    // Last time we heard from this peer.
    public Instant timestamp;

    // Where we heard from them
    public Double latitude;

    public Double longitude;

    @Override
    public String toString() {
        return name;
    }

    public Peer() {
    }

    public Peer(Parcel in) {
        id = in.readLong();
        name = in.readString();
        timestamp = TimestampConverter.deserialize(in.readString());
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(name);
        out.writeString(TimestampConverter.serialize(timestamp));
        out.writeDouble(latitude);
        out.writeDouble(longitude);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Peer> CREATOR = new Creator<Peer>() {

        @Override
        public Peer createFromParcel(Parcel source) {
            return new Peer(source);
        }

        @Override
        public Peer[] newArray(int size) {
            return new Peer[size];
        }

    };
}
