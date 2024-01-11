package com.example.communitygaming.Providers;

import com.example.communitygaming.Models.Post;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PostProviders {

    CollectionReference mColllection;

    public PostProviders(){
        mColllection = FirebaseFirestore.getInstance().collection("Post");
    }

    public Task<Void> save(Post post){
        return mColllection.document().set(post);
    }

    public Query getAll(){
        return mColllection.orderBy("fecha", Query.Direction.DESCENDING);
    }

    public Query getPostByCategoryAndTimestamp(String category){
        return mColllection.whereEqualTo("category", category).orderBy("fecha", Query.Direction.DESCENDING);
    }

    public Query getPostByTitle(String title){
        return mColllection.orderBy("title").startAt(title).endAt(title+'\uf8ff');
    }

    public Query getPostByUser(String id){
        return mColllection.whereEqualTo("idUsuario", id);
    }

    public Task<DocumentSnapshot> getPostById(String id){
        return mColllection.document(id).get();
    }

    public Task<Void> delete(String id){
        return mColllection.document(id).delete();
    }
}
