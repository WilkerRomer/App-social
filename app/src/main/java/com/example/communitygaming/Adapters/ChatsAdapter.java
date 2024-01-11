package com.example.communitygaming.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communitygaming.Activitys.ChatActivity;
import com.example.communitygaming.Models.Chat;
import com.example.communitygaming.Providers.AuthProviders;
import com.example.communitygaming.Providers.ChatsProviders;
import com.example.communitygaming.Providers.MessagesProviders;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends FirestoreRecyclerAdapter<Chat, ChatsAdapter.ViewHolder > {

    Context context;
    UsuariosProviders mUsuariosProvider;
    AuthProviders mAuthProvider;
    ChatsProviders mChatsProviders;
    MessagesProviders mMessageProviders;
    ListenerRegistration mListener;
    ListenerRegistration mListenerLastMessage;

    public ChatsAdapter(FirestoreRecyclerOptions<Chat> options, Context context){
        super(options);
        this.context  = context;
        mUsuariosProvider = new UsuariosProviders();
        mAuthProvider = new AuthProviders();
        mChatsProviders = new ChatsProviders();
        mMessageProviders = new MessagesProviders();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Chat chat) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String chatId = document.getId();
        if (mAuthProvider.getUid().equals(chat.getIdUsuario1())){
            getUserInfo(chat.getId(), holder);
        }
        else{
            getUserInfo(chat.getId(), holder);
        }

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChatActivity(chatId, chat.getIdUsuario1(), chat.getIdUsuario2());
            }
        });

        getLastMessage(chatId, holder.textViewLastMessage);


        String idSender = "";
        if (mAuthProvider.getUid().equals(chat.getIdUsuario1())){
            idSender = chat.getIdUsuario2();
        }
        else{
            idSender = chat.getIdUsuario1();
        }
        getMessageNotRead(chatId, idSender, holder.textViewMessageNotRead, holder.messageFrameLayoutNotRead);
    }

    private void getMessageNotRead(String chatId, String idSender, TextView textViewMessageNotRead, FrameLayout messageFrameLayoutNotRead) {

        mListener = mMessageProviders.getMessageByChatsAndSender(chatId, idSender).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null){
                    int size = value.size();
                    if (size > 0 ){
                        messageFrameLayoutNotRead.setVisibility(View.VISIBLE);
                        textViewMessageNotRead.setText(String.valueOf(size));
                    }
                    else{
                        messageFrameLayoutNotRead.setVisibility(View.GONE);
                    }
                }

            }
        });
    }

    public ListenerRegistration getListener(){
        return  mListener;
    }



    private void getLastMessage(String ChatId, TextView textViewLastChat) {
        mListenerLastMessage = mMessageProviders.getLastMessage(ChatId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
               if (value != null){
                   int size = value.size();
                   if (size > 0){
                       String lastMessage = value.getDocuments().get(0).getString("message");
                       textViewLastChat.setText(lastMessage);
                   }
               }
            }
        });
    }

    public ListenerRegistration getmListenerLastMessage(){
        return  mListenerLastMessage;
    }

    private void goToChatActivity(String chatId, String idUsuario1, String idUsuario2) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("idChat", chatId);
        intent.putExtra("idUsuario1", idUsuario1);
        intent.putExtra("idUsuario2", idUsuario2);
        context.startActivity(intent);
    }

    private void getUserInfo(String idUser, final ViewHolder holder) {
        mUsuariosProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("usuario")) {
                        String usuario = documentSnapshot.getString("usuario");
                        holder.textViewUserName.setText(usuario.toUpperCase());
                    }
                    if (documentSnapshot.contains("image_profile")) {
                        String imageProfile = documentSnapshot.getString("image_profile");
                        if (imageProfile != null) {
                            if (!imageProfile.isEmpty()) {
                                Picasso.with(context).load(imageProfile).into(holder.circleViewChat);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chats, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewUserName;
        TextView textViewLastMessage;
        TextView textViewMessageNotRead;
        FrameLayout messageFrameLayoutNotRead;
        CircleImageView circleViewChat;
        View viewHolder;

        public ViewHolder(View view){
            super(view);
            textViewUserName = view.findViewById(R.id.textViewUserChat);
            textViewLastMessage = view.findViewById(R.id.textViewLastChat);
            textViewMessageNotRead = view.findViewById(R.id.textViewMessageNotReade);
            circleViewChat = view.findViewById(R.id.circleImageChats);
            messageFrameLayoutNotRead = view.findViewById(R.id.frameLayoutNotRead);
            viewHolder = view;
        }
    }
}
