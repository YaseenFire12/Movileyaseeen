package edu.stevens.cs522.chatserver.databases;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import edu.stevens.cs522.chatserver.entities.Message;
@Dao
public interface MessageDao {

    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    LiveData<List<Message>> fetchAllMessages();

    @Query("SELECT * FROM messages WHERE sender = :peerName ORDER BY timestamp DESC")
    LiveData<List<Message>> fetchMessagesFromPeer(String peerName);

    @Insert
    void persist(Message message);
}
