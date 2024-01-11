package com.example.communitygaming.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communitygaming.Activitys.DetallesPostActivity;
import com.example.communitygaming.Models.Comentarios;
import com.example.communitygaming.Models.Post;
import com.example.communitygaming.Providers.UsuariosProviders;
import com.example.communitygaming.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommetsAdapter extends FirestoreRecyclerAdapter<Comentarios, CommetsAdapter.ViewHolder > {

    Context context;
    UsuariosProviders mUsuariosProvider;

    public CommetsAdapter(FirestoreRecyclerOptions<Comentarios> options, Context context){
        super(options);
        this.context  = context;
        mUsuariosProvider = new UsuariosProviders();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Comentarios comentarios) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String commentId = document.getId();
        String idUsuario = document.getString("idUsuario");

        holder.textViewComment.setText(comentarios.getComment());
        getUserInfo(idUsuario, holder);

    }

    private void getUserInfo (String idUsuario, ViewHolder holder) {
        mUsuariosProvider.getUser(idUsuario).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
             if (documentSnapshot.exists()){
                 if (documentSnapshot.contains("usuario")){
                     String usuario = documentSnapshot.getString("usuario");
                     holder.textViewUserName.setText(usuario.toUpperCase());
                 }
                 if (documentSnapshot.contains("image_profile")){
                     String imageProfile = documentSnapshot.getString("image_profile");
                     if (imageProfile != null){
                         if (!imageProfile.isEmpty()){
                             Picasso.with(context).load(imageProfile).into(holder.circleViewComment);
                         }
                     }
                 }
             }
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_comment, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewUserName;
        TextView textViewComment;
        CircleImageView circleViewComment;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            textViewUserName = view.findViewById(R.id.textViewUsuario);
            textViewComment = view.findViewById(R.id.textViewCommentario);
            circleViewComment = view.findViewById(R.id.circleImageComment);
            viewHolder = view;
        }
    }
}
