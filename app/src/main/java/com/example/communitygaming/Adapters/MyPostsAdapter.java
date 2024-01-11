package com.example.communitygaming.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communitygaming.Activitys.DetallesPostActivity;
import com.example.communitygaming.Models.Like;
import com.example.communitygaming.Models.Post;
import com.example.communitygaming.Providers.AuthProviders;
import com.example.communitygaming.Providers.LikesProviders;
import com.example.communitygaming.Providers.PostProviders;
import com.example.communitygaming.Providers.UsuariosProviders;
import com.example.communitygaming.R;
import com.example.communitygaming.Utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostsAdapter extends FirestoreRecyclerAdapter<Post, MyPostsAdapter.ViewHolder > {

    Context context;
    UsuariosProviders mUsuariosProvider;
    LikesProviders mLikeProviders;
    AuthProviders mAuthProviders;
    PostProviders mPostProviders;

    public MyPostsAdapter(FirestoreRecyclerOptions<Post> options, Context context){
        super(options);
        this.context  = context;
        mUsuariosProvider = new UsuariosProviders();
        mLikeProviders = new LikesProviders();
        mAuthProviders = new AuthProviders();
        mPostProviders = new PostProviders();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post post) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();
        String relativeTime = RelativeTime.getTimeAgo(post.getFecha(), context);
        holder.textRelativeTime.setText(relativeTime);
        holder.textViewTitlePost.setText(post.getTitle().toUpperCase());
        if (post.getIdUsuario().equals(mAuthProviders.getUid())){
            holder.imageDeletePost.setVisibility(View.VISIBLE);
        }
        else{
            holder.imageDeletePost.setVisibility(View.INVISIBLE);
        }
        if (post.getImage1() != null){
            if (!post.getImage1().isEmpty()){
                Picasso.with(context).load(post.getImage1()).into(holder.circleImagePost);
            }

        }

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetallesPostActivity.class);
                intent.putExtra("id", postId);
                context.startActivity(intent);
            }
        });

        holder.imageDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmPostDelete(postId);
            }
        });

    }

    private void confirmPostDelete(String postId) {
        new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Eliminar publicación")
                        .setMessage("¿Estas seguro de realizar esta acción?")
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deletePost(postId);
                            }
                        })
                        .setNegativeButton("NO", null)
                        .show();
    }

    private void deletePost(String postId) {
        mPostProviders.delete(postId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(context, "La publicación se eliminó correctamente", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "No se pudo eliminar la publicación", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_publicaciones, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTitlePost;
        TextView textRelativeTime;
        ImageView imageDeletePost;
        CircleImageView circleImagePost;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            textViewTitlePost = view.findViewById(R.id.textViewtitlePost);
            textRelativeTime = view.findViewById(R.id.textViewRelativeTimeMyPost);
            imageDeletePost = view.findViewById(R.id.imageViewDeleteMyPost);
            circleImagePost = view.findViewById(R.id.circleImageMyPublicaciones);
            viewHolder = view;
        }
    }
}
