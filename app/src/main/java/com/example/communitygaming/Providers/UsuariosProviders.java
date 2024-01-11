package com.example.communitygaming.Providers;

import com.example.communitygaming.Models.Usuarios;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UsuariosProviders {

    public CollectionReference mCollection;

    public UsuariosProviders(){
        mCollection = FirebaseFirestore.getInstance().collection("Usuarios");
    }

    public Task<DocumentSnapshot> getUser(String id){
      return mCollection.document(id).get();
    }

    public DocumentReference getUserRealTime(String id){
        return mCollection.document(id);
    }

    public Task<Void> create(Usuarios usuarios){
       return mCollection.document(usuarios.getId()).set(usuarios);
    }

    public Task<Void> update(Usuarios usuarios){
        Map<String, Object> map = new HashMap<>();
        map.put("usuario", usuarios.getUsuario());
        map.put("phone", usuarios.getPhone());
        map.put("fecha", new Date().getTime());
        map.put("image_profile", usuarios.getImageProfile());
        map.put("image_fondo", usuarios.getImageFondo());
        return mCollection.document(usuarios.getId()).update(map);
    }

    public Task<Void> updateOnline(String idUsuario, boolean status){
        Map<String, Object> map = new HashMap<>();
        map.put("online", status);
        map.put("lastConnect", new Date().getTime());
        return mCollection.document(idUsuario).update(map);
    }
}
