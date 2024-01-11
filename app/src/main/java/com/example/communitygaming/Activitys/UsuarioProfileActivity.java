package com.example.communitygaming.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.communitygaming.Adapters.MyPostsAdapter;
import com.example.communitygaming.Models.Post;
import com.example.communitygaming.Providers.AuthProviders;
import com.example.communitygaming.Providers.PostProviders;
import com.example.communitygaming.Providers.UsuariosProviders;
import com.example.communitygaming.R;
import com.example.communitygaming.Utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsuarioProfileActivity extends AppCompatActivity {

    LinearLayout mLinearEditProfile;
    TextView textPublicaciones;
    TextView textTelefono;
    TextView textCorreo;
    TextView textViewNombreUser;
    TextView textViewPostExist;
    ImageView imageViewFondo;
    CircleImageView imageCircleProfile;
    RecyclerView recyclerViewMyPost;
    Toolbar mToolbar;
    FloatingActionButton mFabChat;

    UsuariosProviders mUsuariosProviders;
    AuthProviders mAuthProviders;
    PostProviders mPostProviders;

    ListenerRegistration mListener;

    MyPostsAdapter myPostsAdapter;

    String mExtraIdUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_profile);

        mLinearEditProfile = findViewById(R.id.linearLayoutEditPerfil);
        textPublicaciones = findViewById(R.id.CantidadPublicaciones);
        textCorreo = findViewById(R.id.CampoCorreoUsuario);
        textViewNombreUser = findViewById(R.id.CampoNombrePerfil);
        textTelefono = findViewById(R.id.NumeroTelefono);
        textViewPostExist = findViewById(R.id.textViewPostExistUsers);
        imageViewFondo = findViewById(R.id.imageFondo);
        imageCircleProfile = findViewById(R.id.ImageProfile);
        mToolbar = findViewById(R.id.toolbar);
        mFabChat = findViewById(R.id.buttonChats);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerViewMyPost = findViewById(R.id.recyclerViewMyPost);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UsuarioProfileActivity.this);
        recyclerViewMyPost.setLayoutManager(linearLayoutManager);

        mPostProviders = new PostProviders();
        mUsuariosProviders = new UsuariosProviders();
        mAuthProviders = new AuthProviders();

        mExtraIdUser = getIntent().getStringExtra("idUsuario");

        if (mAuthProviders.getUid().equals(mExtraIdUser)){
            mFabChat.setEnabled(false);
        }

        mFabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChatsActivity();
            }
        });

        getNumeroPublicaciones();
        getUsuarios();
        checkViewPostExist();
    }

    private void goToChatsActivity() {
        Intent intent = new Intent(UsuarioProfileActivity.this, ChatActivity.class);
        intent.putExtra("idUsuario1", mAuthProviders.getUid());
        intent.putExtra("idUsuario2", mExtraIdUser);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProviders.getPostByUser(mExtraIdUser);
        FirestoreRecyclerOptions<Post> options =
                new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();
        myPostsAdapter = new MyPostsAdapter(options, UsuarioProfileActivity.this);
        recyclerViewMyPost.setAdapter(myPostsAdapter);
        myPostsAdapter.startListening();
        ViewedMessageHelper.updateOnline(true, UsuarioProfileActivity.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        myPostsAdapter.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, UsuarioProfileActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListener != null){
            mListener.remove();
        }
    }

    private void checkViewPostExist() {
        mListener = mPostProviders.getPostByUser(mExtraIdUser).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null){
                    int numberPost = value.size();
                    if (numberPost > 0){
                        textViewPostExist.setText("Publicaciones");
                        textViewPostExist.setTextColor(Color.RED);
                    }
                    else{
                        textViewPostExist.setText("No hay publicaciones");
                        textViewPostExist.setTextColor(Color.GRAY);
                    }
                }

            }
        });
    }


    private void getNumeroPublicaciones(){
        mPostProviders.getPostByUser(mExtraIdUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int NumeroPublic = queryDocumentSnapshots.size();
                textPublicaciones.setText(String.valueOf(NumeroPublic));
            }
        });
    }

    private void getUsuarios(){
        mUsuariosProviders.getUser(mExtraIdUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if (documentSnapshot.contains("email")){
                        String email = documentSnapshot.getString("email");
                        textCorreo.setText(email);
                    }
                    if (documentSnapshot.contains("phone")){
                        String phone = documentSnapshot.getString("phone");
                        textTelefono.setText(phone);
                    }
                    if (documentSnapshot.contains("usuario")){
                        String name = documentSnapshot.getString("usuario");
                        textViewNombreUser.setText(name);
                    }
                    if (documentSnapshot.contains("image_profile")){
                        String imageProfie = documentSnapshot.getString("image_profile");
                        if (imageProfie != null){
                            if (!imageProfie.isEmpty()){
                                Picasso.with(UsuarioProfileActivity.this).load(imageProfie).into(imageCircleProfile);
                            }
                        }
                    }
                    if (documentSnapshot.contains("image_fondo")){
                        String imageFondo = documentSnapshot.getString("image_fondo");
                        if(imageFondo != null){
                            if (!imageFondo.isEmpty()){
                                Picasso.with(UsuarioProfileActivity.this).load(imageFondo).into(imageViewFondo);
                            }
                        }

                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}