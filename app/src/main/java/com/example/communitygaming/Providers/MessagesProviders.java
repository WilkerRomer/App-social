package com.example.communitygaming.Providers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.communitygaming.Models.Message;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MessagesProviders {

    CollectionReference mCollection;

    public MessagesProviders(){
        mCollection = FirebaseFirestore.getInstance().collection("Messages");
    }

    public Task<Void> create(Message message){
        DocumentReference document = mCollection.document();
        message.setId(document.getId());
        return document.set(message);
    }

    public Query getMessageByChats(String idChat){
        return mCollection.whereEqualTo("idChat", idChat).orderBy("fecha", Query.Direction.ASCENDING);
    }

    public Query getMessageByChatsAndSender(String idChat, String idSender){
        return mCollection.whereEqualTo("idChat", idChat).whereEqualTo("idSender", idSender).whereEqualTo("viewed", false);
    }

    public Query getLastThreeMessageByChatsAndSender(String idChat, String idSender){
        return mCollection
                .whereEqualTo("idChat", idChat)
                .whereEqualTo("idSender", idSender)
                .whereEqualTo("viewed", false)
                .orderBy("fecha", Query.Direction.DESCENDING)
                .limit(3);
    }

    public Query getLastMessage(String idChat){
        return mCollection.whereEqualTo("idChat", idChat).orderBy("fecha",Query.Direction.DESCENDING).limit(1);
    }

    public Query getLastMessageSender(String idChat, String idSender){
        return mCollection.whereEqualTo("idChat", idChat).whereEqualTo("idSender", idSender).orderBy("fecha",Query.Direction.DESCENDING).limit(1);
    }

    public Task<Void> updateViewed(String idDocument, boolean state){
        Map<String, Object> map = new HashMap<>();
        map.put("viewed", state);
        return mCollection.document(idDocument).update(map);
    }
}
