package edu.stevens.cs522.chatserver.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;
import edu.stevens.cs522.chatserver.ui.MessageChatroomAdapter;
import edu.stevens.cs522.chatserver.viewmodels.PeerViewModel;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends FragmentActivity {

    public static final String TAG = ViewPeerActivity.class.getCanonicalName();

    public static final String PEER_KEY = "peer";

    private MessageChatroomAdapter messagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_peer);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.view_peer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Peer peer = getPeer(getIntent());
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer as intent extra");
        }

        TextView userNameView = findViewById(R.id.view_user_name);
        userNameView.setText(getString(R.string.view_user_name, peer.name));

        TextView timestampView = findViewById(R.id.view_timestamp);
        timestampView.setText(getString(R.string.view_timestamp, formatTimestamp(peer.timestamp)));

        TextView locationView = findViewById(R.id.view_location);
        locationView.setText(getString(R.string.view_location, peer.latitude, peer.longitude));

        // Initialize the recyclerview and adapter for messages
        RecyclerView messageList = findViewById(R.id.message_list);
        messageList.setLayoutManager(new LinearLayoutManager(this));

        messagesAdapter = new MessageChatroomAdapter();
        messageList.setAdapter(messagesAdapter);

        PeerViewModel peerViewModel = new ViewModelProvider(this).get(PeerViewModel.class);

        LiveData<List<Message>> messages = peerViewModel.fetchMessagesFromPeer(peer);
        messages.observe(this, messageList1 -> {
            messagesAdapter.setMessages(messageList1);
            messagesAdapter.notifyDataSetChanged();
        });


    }

    private static String formatTimestamp(Instant timestamp) {
        LocalDateTime dateTime = timestamp.atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return dateTime.format(formatter);
    }

    private static Peer getPeer(Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return intent.getParcelableExtra(PEER_KEY, Peer.class);
        } else {
            return intent.getParcelableExtra(PEER_KEY);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
