package edu.stevens.cs522.chatserver.databases;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import edu.stevens.cs522.chatserver.entities.Message;

// TODO add annotations for Repository pattern
public interface MessageDao {

    public abstract LiveData<List<Message>> fetchAllMessages(String chatroom);

    public LiveData<List<Message>> fetchMessagesFromPeer(String peerName);

    public void persist(Message message);

}
