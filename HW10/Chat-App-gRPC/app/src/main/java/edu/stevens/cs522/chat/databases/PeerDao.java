package edu.stevens.cs522.chat.databases;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

import edu.stevens.cs522.chat.entities.Peer;

@Dao
public abstract class PeerDao {

    private static final String TAG = PeerDao.class.getCanonicalName();

    @Query("SELECT * FROM Peer")
    public abstract LiveData<List<Peer>> fetchAllPeers();

    @Query("SELECT * FROM Peer WHERE id = :peerId")
    public abstract ListenableFuture<Peer> fetchPeer(long peerId);

    @Query("SELECT id FROM Peer WHERE name = :name")
    protected abstract long getPeerId(String name);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long insert(Peer peer);

    @Update
    protected abstract void update(Peer peer);

    @Transaction
    public void upsert(Peer peer) {
        long id = getPeerId(peer.name);
        if (id == 0) {
            insert(peer);
        } else {
            peer.id = id;
            update(peer);
        }
    }
}
