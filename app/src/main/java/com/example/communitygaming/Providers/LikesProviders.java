package com.example.communitygaming.Providers;

import com.example.communitygaming.Models.Like;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class LikesProviders {

    CollectionReference mCollection;

    public LikesProviders(){
        mCollection = FirebaseFirestore.getInstance().collection("Like");
    }

    public Task<Void> create(Like like){
        DocumentReference document = mCollection.document();
        String id = document.getPath();
        like.setId(id);
        return document.set(like);
    }

    public Query getLikesByPost(String idPost){
        return mCollection.whereEqualTo("idPost", idPost);
    }

    public Query getLikeByPostAndUser(String idPost, String idUsuario){
        return mCollection.whereEqualTo("idPost", idPost).whereEqualTo("idUsuario", idUsuario);
    }

    public Task<Void> delete(String id){
        return mCollection.document(id).delete();
    }
}
