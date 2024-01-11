package com.example.communitygaming.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class PostsAdapter extends FirestoreRecyclerAdapter<Post,PostsAdapter.ViewHolder > {

    Context context;
    UsuariosProviders mUsuariosProvider;
    LikesProviders mLikeProviders;
    AuthProviders mAuthProviders;

    ListenerRegistration mListener;

    TextView mNumberFilters;

    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context context){
        super(options);
        this.context  = context;
        mUsuariosProvider = new UsuariosProviders();
        mLikeProviders = new LikesProviders();
        mAuthProviders = new AuthProviders();
    }

    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context context, TextView textView){
        super(options);
        this.context  = context;
        mUsuariosProvider = new UsuariosProviders();
        mLikeProviders = new LikesProviders();
        mAuthProviders = new AuthProviders();
        mNumberFilters = textView;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post post) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String postId = document.getId();

        if (mNumberFilters != null){
            int numberFilters = getSnapshots().size();
            mNumberFilters.setText(String.valueOf(numberFilters));
        }

        holder.textViewTitle.setText(post.getTitle().toUpperCase());
        holder.textViewDescriptions.setText(post.getDescription());
        if (post.getImage1() != null){
            if (!post.getImage1().isEmpty()){
                Picasso.with(context).load(post.getImage1()).into(holder.ImageViewPost);
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

        holder.imageViewLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Like like = new Like();
                like.setIdUsuario(mAuthProviders.getUid());
                like.setIdPost(postId);
                like.setFecha(new Date().getTime());
                like(like, holder);
            }
        });

            getUsuarioInfo(post.getIdUsuario(), holder);
            numberLikesByPost(postId, holder);
            checkIfExistLike(postId, mAuthProviders.getUid(), holder);

    }

    private void numberLikesByPost(String idPost, ViewHolder holder){
        mListener = mLikeProviders.getLikesByPost(idPost).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null){
                    int numberLikes = value.size();
                    holder.textViewLike.setText(String.valueOf(numberLikes) + " Me gustas");
                }
            }
        });
    }

    private void like(Like like, ViewHolder holder) {

        mLikeProviders.getLikeByPostAndUser(like.getIdPost(), mAuthProviders.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberDocuments = queryDocumentSnapshots.size();
                if (numberDocuments > 0){
                    String idLike = queryDocumentSnapshots.getDocuments().get(0).getId();
                    holder.imageViewLike.setImageResource(R.drawable.icon_like_grey);
                    mLikeProviders.delete(idLike);
                }
                else{
                    holder.imageViewLike.setImageResource(R.drawable.icon_like_blue);
                    mLikeProviders.create(like);
                }

            }
        });

    }

    private void checkIfExistLike(String idPost, String idUsuario, ViewHolder holder) {

        mLikeProviders.getLikeByPostAndUser(idPost, idUsuario).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberDocuments = queryDocumentSnapshots.size();
                if (numberDocuments > 0){
                    holder.imageViewLike.setImageResource(R.drawable.icon_like_blue);
                }
                else{
                    holder.imageViewLike.setImageResource(R.drawable.icon_like_grey);
                }

            }
        });

    }

    private void getUsuarioInfo(String idUsuario, ViewHolder holder) {
        mUsuariosProvider.getUser(idUsuario).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()){
                        if (documentSnapshot.contains("usuario")){
                            String username = documentSnapshot.getString("usuario");
                            holder.textViewNombre.setText("By: " + username.toUpperCase());
                        }

                    }
                }
        });
    }

    public ListenerRegistration getListener(){
        return mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewTitle;
        TextView textViewDescriptions;
        TextView textViewNombre;
        TextView textViewLike;
        ImageView imageViewLike;
        ImageView ImageViewPost;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            textViewTitle = view.findViewById(R.id.TextViewTitlePostCard);
            textViewDescriptions = view.findViewById(R.id.TextViewDescriptionPostCard);
            textViewNombre = view.findViewById(R.id.TextViewPostCardUser);
            textViewLike = view.findViewById(R.id.TextViewLike);
            imageViewLike = view.findViewById(R.id.ImageViewLike);
            ImageViewPost = view.findViewById(R.id.ImageViewPostCard);
            viewHolder = view;
        }
    }
}
