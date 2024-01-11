package com.example.communitygaming.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.communitygaming.Adapters.PostsAdapter;
import com.example.communitygaming.Models.Post;
import com.example.communitygaming.Providers.AuthProviders;
import com.example.communitygaming.Providers.PostProviders;
import com.example.communitygaming.R;
import com.example.communitygaming.Utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class FilterActivity extends AppCompatActivity {

    String mExtraCategory;
    TextView mViewNumber;

    AuthProviders mAuthProviders;
    RecyclerView mRecyclerView;
    PostProviders mPostProviders;
    PostsAdapter mPostAdater;

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        mRecyclerView = findViewById(R.id.recyclerViewFilter);
        mViewNumber = findViewById(R.id.textViewNumber);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Filtros");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mRecyclerView.setLayoutManager(new GridLayoutManager(FilterActivity.this, 2));

        mExtraCategory = getIntent().getStringExtra("category");

        mAuthProviders = new AuthProviders();
        mPostProviders = new PostProviders();

    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProviders.getPostByCategoryAndTimestamp(mExtraCategory);
        FirestoreRecyclerOptions<Post> options =
                new FirestoreRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .build();
        mPostAdater = new PostsAdapter(options, FilterActivity.this, mViewNumber);
        mRecyclerView.setAdapter(mPostAdater);
        mPostAdater.startListening();
        ViewedMessageHelper.updateOnline(true, FilterActivity.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPostAdater.stopListening();
    }


   @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, FilterActivity.this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}