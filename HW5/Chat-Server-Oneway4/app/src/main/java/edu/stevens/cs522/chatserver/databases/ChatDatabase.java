package edu.stevens.cs522.chatserver.databases;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;


import androidx.room.TypeConverters;
import androidx.room.Database;

import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;
import edu.stevens.cs522.chatserver.entities.TimestampConverter;

/**
 * Created by dduggan.
 *
 * See build.gradle file for app for where schema file is left after processing.
 */

@Database(entities = {Message.class, Peer.class}, version = 1)
@TypeConverters({TimestampConverter.class})
public abstract class ChatDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "messages.db";

    private static ChatDatabase instance;

    public abstract PeerDao peerDao();

    public abstract MessageDao messageDao();

    public static ChatDatabase getInstance(Context context) {
        if (instance == null) {
            // We allow main thread queries (for insert/upsert) for THIS ASSIGNMENT ONLY!
            instance = Room.databaseBuilder(context, ChatDatabase.class, DATABASE_NAME).allowMainThreadQueries().build();
        }
        return instance;
    }

}