package edu.stevens.cs522.chat.databases;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import edu.stevens.cs522.chat.entities.Peer;

/*
 * TODO add annotations (NB insert should ignore conflicts, for upsert)
 *
 * We will continue to allow insertion to be done on main thread for noew.
 */
public abstract class PeerDao {

    /**
     * Get all peers in the database.
     * @return
     */
    public abstract LiveData<List<Peer>> fetchAllPeers();

    /**
     * Get the database primary key for a peer, based on chat name.
     * @param name
     * @return
     */
    protected abstract long getPeerId(String name);

    /**
     *  Insert a peer and return their primary key (must not already be in database)
     * @param peer
     * @return
     */
    public abstract long insert(Peer peer);

    /**
     * Update the metadata for a peer (GPS coordinates, last seen)
     * @param peer
     */
    protected abstract void update(Peer peer);

    @Transaction
    /**
     * TODO Add a peer record if it does not already exist;
     * update information if it is already defined.
     * This operation must be transactional, to avoid race condition
     * between search and insert
     */
    public void upsert(Peer peer) {
        // TODO
        long id = getPeerId(peer.name);
        if (id == 0) {
            // TODO
        } else {
            // TODO
        }
    }
}
