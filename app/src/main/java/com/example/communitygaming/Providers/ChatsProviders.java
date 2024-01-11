package com.example.communitygaming.Providers;

import com.example.communitygaming.Models.Chat;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ChatsProviders {

    CollectionReference mCollection;

    public ChatsProviders(){
        mCollection = FirebaseFirestore.getInstance().collection("Chats");
    }

    public void create(Chat chat){
        mCollection.document(chat.getIdUsuario1() + chat.getIdUsuario2()).set(chat);
    }

    public Query getAll(String idUsuario){
        return mCollection.whereArrayContains("ids", idUsuario);
    }

    public Query getChatByIdUser1AndUser2(String idUsuario1, String idUsuario2){
        ArrayList<String> ids = new ArrayList<>();
        ids.add(idUsuario1 + idUsuario2);
        ids.add(idUsuario2 + idUsuario1);
        return mCollection.whereIn("id", ids);
    }
}
