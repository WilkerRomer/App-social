package com.example.communitygaming.Providers;

import com.example.communitygaming.Models.Comentarios;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ComentsProviders {

    CollectionReference mCollection;


    public ComentsProviders(){
        mCollection = FirebaseFirestore.getInstance().collection("Comments");
    }

    public Task<Void> create(Comentarios comentarios) {
        return mCollection.document().set(comentarios);
    }

    public Query getCommentsByPost(String idPost){
        return mCollection.whereEqualTo("idPost", idPost);
    }

}
