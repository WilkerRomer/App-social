package com.example.communitygaming.fragmens;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.communitygaming.Activitys.EditProfileEditActivity;
import com.example.communitygaming.Adapters.MyPostsAdapter;
import com.example.communitygaming.Adapters.PostsAdapter;
import com.example.communitygaming.Models.Post;
import com.example.communitygaming.Providers.AuthProviders;
import com.example.communitygaming.Providers.PostProviders;
import com.example.communitygaming.Providers.UsuariosProviders;
import com.example.communitygaming.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment {

    LinearLayout mLinearEditProfile;
    View mView;

    RecyclerView recyclerViewMyPost;
    TextView textPublicaciones;
    TextView textTelefono;
    TextView textCorreo;
    TextView textViewNombreUser;
    TextView textViewPostExist;
    ImageView imageViewFondo;
    CircleImageView imageCircleProfile;

    ListenerRegistration mListener;

    UsuariosProviders mUsuariosProviders;
    AuthProviders mAuthProviders;
    PostProviders mPostProviders;
    MyPostsAdapter myPostsAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_perfil, container, false);
        mLinearEditProfile = mView.findViewById(R.id.linearLayoutEditPerfil);
        textPublicaciones = mView.findViewById(R.id.CantidadPublicaciones);
        textCorreo = mView.findViewById(R.id.CampoCorreoUsuario);
        textViewNombreUser = mView.findViewById(R.id.CampoNombrePerfil);
        textTelefono = mView.findViewById(R.id.NumeroTelefono);
        textViewPostExist = mView.findViewById(R.id.textViewPostExist);
        imageViewFondo = mView.findViewById(R.id.imageFondo);
        imageCircleProfile = mView.findViewById(R.id.ImageProfile);
        recyclerViewMyPost = mView.findViewById(R.id.recyclerViewMyPost);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewMyPost.setLayoutManager(linearLayoutManager);

        mLinearEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEditProfile();
            }
        });

        mPostProviders = new PostProviders();
        mUsuariosProviders = new UsuariosProviders();
        mAuthProviders = new AuthProviders();


        getUsuarios();
        getNumeroPublicaciones();
        checkViewPostExist();
        return mView;
    }

    private void checkViewPostExist() {
        mListener = mPostProviders.getPostByUser(mAuthProviders.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProviders.getPostByUser(mAuthProviders.getUid());
        FirestoreRecyclerOptions<Post> options =
                new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();
        myPostsAdapter = new MyPostsAdapter(options, getContext());
        recyclerViewMyPost.setAdapter(myPostsAdapter);
        myPostsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        myPostsAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mListener != null){
            mListener.remove();
        }
    }

    private void goToEditProfile() {
        Intent intent = new Intent(getContext(), EditProfileEditActivity.class);
        startActivity(intent);
    }

    private void getNumeroPublicaciones(){
        mPostProviders.getPostByUser(mAuthProviders.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int NumeroPublic = queryDocumentSnapshots.size();
                textPublicaciones.setText(String.valueOf(NumeroPublic));
            }
        });
    }

    private void getUsuarios(){
        mUsuariosProviders.getUser(mAuthProviders.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                                Picasso.with(getContext()).load(imageProfie).into(imageCircleProfile);
                            }
                        }
                    }
                    if (documentSnapshot.contains("image_fondo")){
                        String imageFondo = documentSnapshot.getString("image_fondo");
                        if(imageFondo != null){
                            if (!imageFondo.isEmpty()){
                                Picasso.with(getContext()).load(imageFondo).into(imageViewFondo);
                            }
                        }

                    }
                }
            }
        });
    }
}