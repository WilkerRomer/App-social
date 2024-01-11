package com.example.communitygaming.Providers;

import com.example.communitygaming.Models.Token;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class TokenProviders {

    CollectionReference mCollection;

    public TokenProviders(){
        mCollection = FirebaseFirestore.getInstance().collection("Tokens");
    }

    public void creeate(String idUsuario){
        if (idUsuario == null) {
            return;
        }
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                Token token = new Token(s);
                mCollection.document(idUsuario).set(token);
            }
        });
    }

    public Task<DocumentSnapshot> getToken(String idUsuario){
        return mCollection.document(idUsuario).get();
    }
}
