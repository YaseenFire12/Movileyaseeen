package edu.stevens.cs522.chat.web;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.time.Instant;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.databases.ChatDatabase;
import edu.stevens.cs522.chat.entities.Chatroom;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.location.CurrentLocation;
import edu.stevens.cs522.chat.web.request.ChatServiceRequest;
import edu.stevens.cs522.chat.web.request.ChatServiceResponse;
import edu.stevens.cs522.chat.web.request.ErrorResponse;
import edu.stevens.cs522.chat.web.request.PostMessageRequest;
import edu.stevens.cs522.chat.web.request.PostMessageResponse;
import edu.stevens.cs522.chat.web.request.RegisterRequest;
import edu.stevens.cs522.chat.web.request.RegisterResponse;
import edu.stevens.cs522.chat.settings.Settings;

/**
 * Created by dduggan.
 */

public class RequestProcessor {

    private static final String TAG = RequestProcessor.class.getCanonicalName();

    private final Context context;

    private final CurrentLocation location;

    private final RestMethod restMethod;

    private final ChatDatabase chatDatabase;

    private RequestProcessor(Context context) {
        this.context = context;

        this.location = new CurrentLocation(context);

        this.restMethod = new RestMethod(context);

        this.chatDatabase = ChatDatabase.getInstance(context);
    }

    public static RequestProcessor getInstance(Context context) {
        return new RequestProcessor(context);
    }

    /**
     * We use the Visitor pattern to dispatch to the appropriate request processing.
     * This is also where we attach metadata to the request that is attached as
     * application-specific request headers to the HTTP request.
     * 
     * @param request
     * @return
     */
    public ChatServiceResponse process(ChatServiceRequest request) {
        request.appId = Settings.getAppId(context);
        if (request.chatName == null) {
            /*
             * chatName is only already set if this is a RegisterRequest
             */
            request.chatName = Settings.getChatName(context);
        }
        String packageName = context.getPackageName();
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            request.version = pInfo.getLongVersionCode();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Unrecognized package name: " + packageName, e);
        }
        request.latitude = location.getLatitude();
        request.longitude = location.getLongitude();
        return request.process(this);
    }

    public ChatServiceResponse perform(RegisterRequest request) {

        Log.d(TAG, "Registering as " + request.chatName);
        ChatServiceResponse response = restMethod.perform(request);

        if (response instanceof RegisterResponse) {
            /*
             * Add a record for this peer to the local database.
             */
            RegisterResponse registration = (RegisterResponse) response;

            final Peer peer = new Peer();
            peer.name = request.chatName;
            peer.timestamp = Instant.now();
            peer.latitude = request.latitude;
            peer.longitude = request.longitude;
            chatDatabase.peerDao().upsert(peer);

            // Initialize the chatrooms database with the default chatroom
            chatDatabase.chatroomDao().insert(new Chatroom(context.getString(R.string.default_chat_room)));

            Settings.saveServerUri(context, request.registerUrl);
            Settings.saveChatName(context, request.chatName);

        }
        return response;
    }

    public ChatServiceResponse perform(PostMessageRequest request) {

        Log.d(TAG, "Posting message." + request.message.messageText);

        Log.d(TAG, "Inserting the message into the local database.");
        long id = chatDatabase.requestDao().insert(request.message);
        Log.d(TAG, "Uploading the message to the server...");
        ChatServiceResponse response = restMethod.perform(request);
        if (response instanceof PostMessageResponse) {
            Log.d(TAG, "Message upload successful!");
            PostMessageResponse postMessageResponse = (PostMessageResponse) response;

            chatDatabase.requestDao().updateSeqNum(id, postMessageResponse.getMessageId());

        } else if (response instanceof ErrorResponse) {
            ErrorResponse errorResponse = (ErrorResponse) response;
            Log.d(TAG, String.format("Message upload failed: %s (%d)", errorResponse.responseMessage,
                    errorResponse.responseCode));
            Log.d(TAG, String.format("Upload error message: %s", errorResponse.errorMessage));
        }
        return response;
    }

}
