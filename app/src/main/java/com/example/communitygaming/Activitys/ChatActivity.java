package com.example.communitygaming.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.communitygaming.Adapters.MessageAdapter;
import com.example.communitygaming.Models.Chat;
import com.example.communitygaming.Models.FCMBody;
import com.example.communitygaming.Models.FCMResponse;
import com.example.communitygaming.Models.Message;
import com.example.communitygaming.Providers.AuthProviders;
import com.example.communitygaming.Providers.ChatsProviders;
import com.example.communitygaming.Providers.MessagesProviders;
import com.example.communitygaming.Providers.NotificationProvider;
import com.example.communitygaming.Providers.TokenProviders;
import com.example.communitygaming.Providers.UsuariosProviders;
import com.example.communitygaming.R;
import com.example.communitygaming.Utils.RelativeTime;
import com.example.communitygaming.Utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    String mExtraIdUsuario1;
    String mExtraIdUsuario2;
    String mExtraIdChat;

    long mIdNotificationChat;

    EditText editTextMessage;
    ImageView imageViewSendMessage;

    ChatsProviders mChatsProviders;
    MessagesProviders mMessageProviders;
    AuthProviders mAuthProviders;
    UsuariosProviders mUsuariosProviders;
    NotificationProvider mNotificatioProviders;
    TokenProviders mTokenProviders;

    MessageAdapter mAdapters;

    CircleImageView mCircleImagePerfil;
    ImageView imageViewBack;
    TextView textViewUsuario;
    TextView textViewRealTime;
    RecyclerView mRecyclerViewMessage;

    String mMyUsuario;
    String mUsuarioChat;
    String mImageReceiver = "";
    String mImageSender = "";

    LinearLayoutManager mLinearLayoutManager;

    ListenerRegistration mListener;

    View mActionBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatsProviders = new ChatsProviders();
        mMessageProviders = new MessagesProviders();
        mAuthProviders = new AuthProviders();
        mUsuariosProviders = new UsuariosProviders();
        mNotificatioProviders = new NotificationProvider();
        mTokenProviders = new TokenProviders();

        editTextMessage = findViewById(R.id.editTextMessage);
        imageViewSendMessage = findViewById(R.id.imageViewSendMessage);
        mRecyclerViewMessage = findViewById(R.id.recyclerViewMessage);

        mLinearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerViewMessage.setLayoutManager(mLinearLayoutManager);

        mExtraIdUsuario1 = getIntent().getStringExtra("idUsuario1");
        mExtraIdUsuario2 = getIntent().getStringExtra("idUsuario2");
        mExtraIdChat = getIntent().getStringExtra("idChat");

        showCustomToolbar(R.layout.custam_chat_toolbar);
        getMyInfoUsuario();

        imageViewSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        checkIfChatExist();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapters != null){
            mAdapters.startListening();
        }
        ViewedMessageHelper.updateOnline(true, ChatActivity.this);
    }


    @Override
    public void onStop() {
        super.onStop();
        mAdapters.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, ChatActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListener != null){
            mListener.remove();
        }
    }

    private void getMessageChat() {
        Query query = mMessageProviders.getMessageByChats(mExtraIdChat);
        FirestoreRecyclerOptions<Message> options =
                new FirestoreRecyclerOptions.Builder<Message>()
                        .setQuery(query, Message.class)
                        .build();
        mAdapters = new MessageAdapter(options, ChatActivity.this);
        mRecyclerViewMessage.setAdapter(mAdapters);
        mAdapters.startListening();
        mAdapters.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateViewed();
                int numberMessage = mAdapters.getItemCount();
                int lastMessagePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastMessagePosition == -1 || (positionStart >= (numberMessage -1) && lastMessagePosition == (positionStart - 1))) {
                    mRecyclerViewMessage.scrollToPosition(positionStart);
                }
            }
        });
    }

    private void sendMessage() {
        String textMessage = editTextMessage.getText().toString();
        if (!textMessage.isEmpty()) {
            final Message message = new Message();
            message.setIdChat(mExtraIdChat);
            if (mAuthProviders.getUid().equals(mExtraIdUsuario1)) {
                message.setIdSender(mExtraIdUsuario1);
                message.setIdReceiver(mExtraIdUsuario2);
            }
            else {
                message.setIdSender(mExtraIdUsuario2);
                message.setIdReceiver(mExtraIdUsuario1);
            }
            message.setFecha(new Date().getTime());
            message.setViewed(false);
            message.setIdChat(mExtraIdChat);
            message.setMessage(textMessage);

            mMessageProviders.create(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        editTextMessage.setText("");
                        mAdapters.notifyDataSetChanged();
                        getToken(message);
                    }
                    else {
                        Toast.makeText(ChatActivity.this, "El mensaje no se pudo crear", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showCustomToolbar(int resource) {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActionBarView = inflater.inflate(resource, null);
        actionBar.setCustomView(mActionBarView);
        mCircleImagePerfil = mActionBarView.findViewById(R.id.circleImageToolbar);
        imageViewBack = mActionBarView.findViewById(R.id.imageViewBack);
        textViewUsuario = mActionBarView.findViewById(R.id.textViewNameToolbar);
        textViewRealTime = mActionBarView.findViewById(R.id.textViewRelativeTimes);

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getUserInfo();
    }

    private void getUserInfo() {
        String idUserInfo = "";
        if (mAuthProviders.getUid().equals(mExtraIdUsuario1)) {
            idUserInfo = mExtraIdUsuario2;
        }
        else {
            idUserInfo = mExtraIdUsuario1;
        }

        mListener = mUsuariosProviders.getUserRealTime(idUserInfo).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("usuario")) {
                        mUsuarioChat = documentSnapshot.getString("usuario");
                        textViewUsuario.setText(mUsuarioChat);
                    }
                    if (documentSnapshot.contains("online")) {
                        boolean online = documentSnapshot.getBoolean("online");
                        if (online) {
                            textViewRealTime.setText("En linea");
                        }
                        else if (documentSnapshot.contains("lastConnect")) {
                            long lastConnect = documentSnapshot.getLong("lastConnect");
                            String relativeTime = RelativeTime.getTimeAgo(lastConnect, ChatActivity.this);
                            textViewRealTime.setText(relativeTime);
                        }
                    }
                    if (documentSnapshot.contains("image_profile")) {
                        mImageReceiver = documentSnapshot.getString("image_profile");
                        if (mImageReceiver != null) {
                            if (!mImageReceiver.equals("")) {
                                Picasso.with(ChatActivity.this).load(mImageReceiver).into(mCircleImagePerfil);
                            }
                        }
                    }
                }
            }
        });
    }

    private void checkIfChatExist() {
        mChatsProviders.getChatByIdUser1AndUser2(mExtraIdUsuario1, mExtraIdUsuario2).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                if (size == 0) {
                    createChat();
                }
                else {
                    mExtraIdChat = queryDocumentSnapshots.getDocuments().get(0).getId();
                    mIdNotificationChat = queryDocumentSnapshots.getDocuments().get(0).getLong("idNotification");
                    getMessageChat();
                    updateViewed();
                }
            }
        });
    }

    private void updateViewed() {
        String idSender = "";

        if (mAuthProviders.getUid().equals(mExtraIdUsuario1)) {
            idSender = mExtraIdUsuario2;
        }
        else {
            idSender = mExtraIdUsuario1;
        }

        mMessageProviders.getLastMessageSender(mExtraIdChat, idSender).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    mMessageProviders.updateViewed(document.getId(), true);
                }
            }
        });

    }

    private void createChat() {
        Chat chat = new Chat();
        chat.setIdUsuario1(mExtraIdUsuario1);
        chat.setIdUsuario2(mExtraIdUsuario2);
        chat.setWriting(false);
        chat.setFecha(new Date().getTime());
        chat.setId(mExtraIdUsuario1 + mExtraIdUsuario2);
        Random random = new Random();
        int n = random.nextInt(1000000);
        chat.setIdNotification(n);
        mIdNotificationChat = n;

        ArrayList<String> ids = new ArrayList<>();
        ids.add(mExtraIdUsuario1);
        ids.add(mExtraIdUsuario2);
        chat.setIds(ids);
        mChatsProviders.create(chat);
        mExtraIdChat = chat.getId();
        getMessageChat();
    }

    private void getToken(Message message) {
        String idUsuario = "";
        if (mAuthProviders.getUid().equals(mExtraIdUsuario1)) {
            idUsuario = mExtraIdUsuario2;
        }
        else {
            idUsuario = mExtraIdUsuario1;
        }
        mTokenProviders.getToken(idUsuario).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("token")) {
                        String token = documentSnapshot.getString("token");
                        getLastThreeMessages(message, token);


                    }
                }
                else {
                    Toast.makeText(ChatActivity.this, "El token de notificaciones del usuario no existe", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getLastThreeMessages(Message message, String token) {
        mMessageProviders.getLastThreeMessageByChatsAndSender(mExtraIdChat, mAuthProviders.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Message> messageArrayList = new ArrayList<>();

                for (DocumentSnapshot d: queryDocumentSnapshots.getDocuments()){
                    if (d.exists()){
                        Message message = d.toObject(Message.class);
                        messageArrayList.add(message);
                    }

                }

                if (messageArrayList.size() == 0){
                    messageArrayList.add(message);
                }

                Collections.reverse(messageArrayList);

                Gson gson = new Gson();
                String messages = gson.toJson(messageArrayList);

                sendNotification(token, messages, message);
            }
        });
    }

    private void sendNotification(String token, String messages, Message message){
        Map<String, String> data = new HashMap<>();
        data.put("title", "NUEVO MENSAJE");
        data.put("body", message.getMessage());
        data.put("idNotification", String.valueOf(mIdNotificationChat));
        data.put("messages", messages);
        data.put("usuarioSender", mMyUsuario);
        data.put("usuarioReceiver", mUsuarioChat);
        data.put("idSender", message.getIdSender());
        data.put("idReceiber", message.getIdReceiver());
        data.put("idChat", message.getIdChat());

        if (mImageSender.equals("")){
            mImageSender = "IMAGEN_NO_VALIDA";
        }
        if (mImageReceiver.equals("")){
            mImageReceiver = "IMAGEN_NO_VALIDA";
        }

        data.put("imageSender", mImageSender);
        data.put("imageReceiver", mImageReceiver);

        String idSender = "";
        if (mAuthProviders.getUid().equals(mExtraIdUsuario1)){
            idSender = mExtraIdUsuario2;
        }
        else{
            idSender = mExtraIdUsuario1;
        }

        mMessageProviders.getLastMessageSender(mExtraIdChat, idSender).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                String lastMessage = "";
                if (size > 0){
                    lastMessage = queryDocumentSnapshots.getDocuments().get(0).getString("message");
                    data.put("lastMessage",lastMessage);
                }
                FCMBody body = new FCMBody(token, "high", "4500s", data);
                mNotificatioProviders.setNotification(body).enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getSuccess() == 1) {
                                // Toast.makeText(ChatActivity.this, "La notificacion se envio correcatemente", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(ChatActivity.this, "La notificacion no se pudo enviar", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(ChatActivity.this, "La notificacion no se pudo enviar", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {

                    }
                });
            }
        });

    }

    private void getMyInfoUsuario(){
        mUsuariosProviders.getUser(mAuthProviders.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("usuario")){
                        mMyUsuario = documentSnapshot.getString("usuario");
                    }
                    if (documentSnapshot.contains("image_profile")) {
                        mImageSender = documentSnapshot.getString("image_profile");
                    }
                }
            }
        });
    }

}
