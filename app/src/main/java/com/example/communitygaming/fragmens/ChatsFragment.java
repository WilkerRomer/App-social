package com.example.communitygaming.fragmens;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.communitygaming.Adapters.ChatsAdapter;
import com.example.communitygaming.Models.Chat;
import com.example.communitygaming.Providers.AuthProviders;
import com.example.communitygaming.Providers.ChatsProviders;
import com.example.communitygaming.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {

    ChatsAdapter mChatAdapter;
    RecyclerView mRecyclerView;
    AuthProviders mAuthProviders;
    View mView;

    Toolbar mToolbar;

    ChatsProviders mChatProvider;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatsFragment newInstance(String param1, String param2) {
        ChatsFragment fragment = new ChatsFragment();
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
       mView = inflater.inflate(R.layout.fragment_chats, container, false);
       mRecyclerView = mView.findViewById(R.id.recyclerViewChats);
       mToolbar = mView.findViewById(R.id.toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Chats");

       LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
       mRecyclerView.setLayoutManager(linearLayoutManager);

        mChatProvider = new ChatsProviders();
        mAuthProviders = new AuthProviders();

       return  mView;

    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mChatProvider.getAll(mAuthProviders.getUid());
        FirestoreRecyclerOptions<Chat> options =
                new FirestoreRecyclerOptions.Builder<Chat>()
                        .setQuery(query, Chat.class)
                        .build();
        mChatAdapter = new ChatsAdapter(options, getContext());
        mRecyclerView.setAdapter(mChatAdapter);
        mChatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mChatAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatAdapter.getListener() != null){
            mChatAdapter.getListener().remove();
        }

        if (mChatAdapter.getmListenerLastMessage() != null){
            mChatAdapter.getmListenerLastMessage().remove();
        }
    }
}