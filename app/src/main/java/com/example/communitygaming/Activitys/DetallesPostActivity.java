package com.example.communitygaming.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.communitygaming.Adapters.CommetsAdapter;
import com.example.communitygaming.Adapters.PostsAdapter;
import com.example.communitygaming.Adapters.SliderAdapter;
import com.example.communitygaming.Models.Comentarios;
import com.example.communitygaming.Models.FCMBody;
import com.example.communitygaming.Models.FCMResponse;
import com.example.communitygaming.Models.Post;
import com.example.communitygaming.Models.SliderItem;
import com.example.communitygaming.Providers.AuthProviders;
import com.example.communitygaming.Providers.ComentsProviders;
import com.example.communitygaming.Providers.LikesProviders;
import com.example.communitygaming.Providers.NotificationProvider;
import com.example.communitygaming.Providers.PostProviders;
import com.example.communitygaming.Providers.TokenProviders;
import com.example.communitygaming.Providers.UsuariosProviders;
import com.example.communitygaming.R;
import com.example.communitygaming.Utils.RelativeTime;
import com.example.communitygaming.Utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetallesPostActivity extends AppCompatActivity {


    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem> mSliderItems = new ArrayList<>();

    PostProviders mPostProviders;
    UsuariosProviders mUsuariosProviders;
    ComentsProviders mComentsProvider;
    AuthProviders mAuthProviders;
    LikesProviders mLikesProviders;
    NotificationProvider mNotiProviders;
    TokenProviders mTokenProviders;

    CommetsAdapter mCommentsAdapter;
    TextView mTextRelativeTime;
    TextView mTextViewLikes;
    TextView mNombreUsuario;
    TextView mtextViewtitulo;
    TextView mtextViewDescripcion;
    TextView mtextCategori;
    TextView mtextTelefono;
    ImageView mImageCategori;
    CircleImageView mCircleimageProfile;
    Button BTN_verPerfil;
    FloatingActionButton mbtnComent;
    RecyclerView mRecyclerView;
    Toolbar mToolbar;

    ListenerRegistration mListener;

    String mExtraPostId;

    String mIdUsuario = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_post);

        mSliderView = findViewById(R.id.imageSlider);
        mNombreUsuario = findViewById(R.id.textViewNombre);
        mtextViewtitulo = findViewById(R.id.textViewtitulo);
        mtextViewDescripcion = findViewById(R.id.textViewDescripción);
        mtextCategori = findViewById(R.id.textViewCtegori);
        mTextRelativeTime = findViewById(R.id.textViewRelativeTime);
        mTextViewLikes = findViewById(R.id.textLikeRelativeTime);
        mImageCategori = findViewById(R.id.imageViewCategori);
        mCircleimageProfile = findViewById(R.id.circleImageProfile);
        BTN_verPerfil = findViewById(R.id.btnVerPerfil);
        mtextTelefono = findViewById(R.id.TextViewTelefono);
        mbtnComent = findViewById(R.id.btnComment);
        mRecyclerView = findViewById(R.id.recyclerViewComments);
        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DetallesPostActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);


        mUsuariosProviders = new UsuariosProviders();
        mPostProviders = new PostProviders();
        mComentsProvider = new ComentsProviders();
        mAuthProviders = new AuthProviders();
        mLikesProviders = new LikesProviders();
        mNotiProviders = new NotificationProvider();
        mTokenProviders = new TokenProviders();

        mExtraPostId = getIntent().getStringExtra("id");
        

        mbtnComent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogComent();
            }
        });
        
        BTN_verPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ir_a_verPerfil();
            }
        });

        getPost();
        getNumberLikes();
    }

    private void getNumberLikes() {

        mListener = mLikesProviders.getLikesByPost(mExtraPostId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null){
                    int numberLikes = value.size();
                    if (numberLikes == 1) {
                        mTextViewLikes.setText(numberLikes + " Me gusta");
                    }
                    else{
                        mTextRelativeTime.setText(numberLikes + " Me gustas");
                    }
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = mComentsProvider.getCommentsByPost(mExtraPostId);
        FirestoreRecyclerOptions<Comentarios> options =
                new FirestoreRecyclerOptions.Builder<Comentarios>()
                        .setQuery(query, Comentarios.class)
                        .build();
        mCommentsAdapter = new CommetsAdapter(options, DetallesPostActivity.this);
        mRecyclerView.setAdapter(mCommentsAdapter);
        mCommentsAdapter.startListening();
        ViewedMessageHelper.updateOnline(true, DetallesPostActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCommentsAdapter.stopListening();
    }



    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, DetallesPostActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListener != null){
            mListener.remove();
        }
    }

    private void mostrarDialogComent() {
        AlertDialog.Builder alert = new AlertDialog.Builder(DetallesPostActivity.this);
        alert.setTitle("¡COMENTARIO!");
        alert.setMessage("Ingresa tu comentario");

        EditText editText = new EditText(DetallesPostActivity.this);
        editText.setHint("Texto");


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(36,0,36,36);
        editText.setLayoutParams(params);
        RelativeLayout container = new RelativeLayout(DetallesPostActivity.this);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
             RelativeLayout.LayoutParams.MATCH_PARENT,
             RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        container.setLayoutParams(relativeParams);
        container.addView(editText);

        alert.setView(container);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String value = editText.getText().toString();
                if (!value.isEmpty()){
                    createComment(value);
                }
                else{
                    Toast.makeText(DetallesPostActivity.this, "Debe ingresar un comentario", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alert.show();
    }

    private void createComment(String value) {
        Comentarios comments = new Comentarios();
        comments.setComment(value);
        comments.setIdPost(mExtraPostId);
        comments.setIdUsuario(mAuthProviders.getUid());
        comments.setFecha(new Date().getTime());
        mComentsProvider.create(comments).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    sentNotification(value);
                    Toast.makeText(DetallesPostActivity.this, "El comentario se creó correctamente", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(DetallesPostActivity.this, "No se pudo crear un comentario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sentNotification(String comment) {
        if (mIdUsuario == null){
            return;
        }
        mTokenProviders.getToken(mIdUsuario).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("token")){
                        String token = documentSnapshot.getString("token");
                        Map<String, String> data = new HashMap<>();
                        data.put("title", "PRIMER COMENTARIO");
                        data.put("body", comment);
                        FCMBody body = new FCMBody(token, "high", "4500s", data);
                        mNotiProviders.setNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if (response.body() != null){
                                    if (response.body().getSuccess() == 1){
                                        Toast.makeText(DetallesPostActivity.this, "La notificacion se envió correctamente", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(DetallesPostActivity.this, "La notificacion no se pudo enviar", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    Toast.makeText(DetallesPostActivity.this, "La notificacion no se pudo enviar", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {

                            }
                        });
                    }
                }
                else{
                    Toast.makeText(DetallesPostActivity.this, "El token de notificaciones del usuario no existe", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void Ir_a_verPerfil() {
        if (!mIdUsuario.equals("")){
            Intent intent = new Intent(DetallesPostActivity.this, UsuarioProfileActivity.class);
            intent.putExtra("idUsuario", mIdUsuario);
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "El ID del usuario aun no se carga", Toast.LENGTH_SHORT).show();
        }
    }

    private void instanceSlider(){

        mSliderAdapter = new SliderAdapter(DetallesPostActivity.this, mSliderItems);
        mSliderView.setSliderAdapter(mSliderAdapter);
        mSliderView.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM);
        mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        mSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        mSliderView.setIndicatorSelectedColor(Color.WHITE);
        mSliderView.setIndicatorUnselectedColor(Color.GRAY);
        mSliderView.setScrollTimeInSec(3);
        mSliderView.setAutoCycle(true);
        mSliderView.startAutoCycle();
    }

    private void getPost(){
        mPostProviders.getPostById(mExtraPostId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if (documentSnapshot.contains("image1")){
                        String image1 = documentSnapshot.getString("image1");
                        SliderItem item = new SliderItem();
                        item.setImageUrl(image1);
                        mSliderItems.add(item);
                    }
                    if (documentSnapshot.contains("image2")){
                        String image2 = documentSnapshot.getString("image2");
                        SliderItem item = new SliderItem();
                        item.setImageUrl(image2);
                        mSliderItems.add(item);
                    }
                    if (documentSnapshot.contains("title")){
                        String titulo = documentSnapshot.getString("title");
                        mtextViewtitulo.setText(titulo.toUpperCase());
                    }
                    if (documentSnapshot.contains("description")){
                        String descripción = documentSnapshot.getString("description");
                        mtextViewDescripcion.setText(descripción);
                    }
                    if (documentSnapshot.contains("category")){
                        String categoría = documentSnapshot.getString("category");
                        mtextCategori.setText(categoría);

                        if (categoría.equals("PS4")){
                            mImageCategori.setImageResource(R.drawable.icon_ps4);
                        }
                        else if (categoría.equals("XBOX")){
                            mImageCategori.setImageResource(R.drawable.icon_xbox);
                        }
                        else if (categoría.equals("PC")){
                            mImageCategori.setImageResource(R.drawable.icon_pc);
                        }
                        else if (categoría.equals("NINTENDO")){
                            mImageCategori.setImageResource(R.drawable.icon_nintendo);
                        }
                    }
                    if (documentSnapshot.contains("idUsuario")){
                        mIdUsuario = documentSnapshot.getString("idUsuario");
                        getUsuraioInfo(mIdUsuario);
                    }
                    if (documentSnapshot.contains("fecha")){
                        long fecha = documentSnapshot.getLong("fecha");
                        String relativeTime = RelativeTime.getTimeAgo(fecha, DetallesPostActivity.this);
                        mTextRelativeTime.setText(relativeTime);
                    }


                    instanceSlider();

                }
            }
        });
    }

    private void getUsuraioInfo(String idUsuario) {
        mUsuariosProviders.getUser(idUsuario).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("usuario")){
                        String username = documentSnapshot.getString("usuario");
                        mNombreUsuario.setText(username);
                    }
                    if (documentSnapshot.contains("phone")){
                        String telefono = documentSnapshot.getString("phone");
                        mtextTelefono.setText(telefono);
                    }
                    if (documentSnapshot.contains("image_profile")){
                        String imagePerfil = documentSnapshot.getString("image_profile");
                        Picasso.with(DetallesPostActivity.this).load(imagePerfil).into(mCircleimageProfile);
                    }
                }
            }
        });
    }
}