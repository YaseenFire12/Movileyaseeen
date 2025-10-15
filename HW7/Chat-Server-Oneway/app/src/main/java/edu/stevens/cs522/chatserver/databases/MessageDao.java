package edu.stevens.cs522.chatserver.databases;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import edu.stevens.cs522.chatserver.entities.Message;

@Dao
public interface MessageDao {

    @Query("SELECT * FROM Message WHERE chatroom = :chatroom ORDER BY timestamp ASC")
    public abstract LiveData<List<Message>> fetchAllMessages(String chatroom);

    @Query("SELECT * FROM Message WHERE sender = :peerName ORDER BY timestamp ASC")
    public LiveData<List<Message>> fetchMessagesFromPeer(String peerName);

    @Insert
    public void persist(Message message);

}