package com.example.communitygaming.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.communitygaming.Providers.AuthProviders;
import com.example.communitygaming.Providers.TokenProviders;
import com.example.communitygaming.Providers.UsuariosProviders;
import com.example.communitygaming.R;
import com.example.communitygaming.Utils.ViewedMessageHelper;
import com.example.communitygaming.fragmens.ChatsFragment;
import com.example.communitygaming.fragmens.FiltrosFragment;
import com.example.communitygaming.fragmens.HomeFragment;
import com.example.communitygaming.fragmens.PerfilFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    TokenProviders mTokenProviders;
    AuthProviders mAuthProviders;
    UsuariosProviders mUsuariosProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(new HomeFragment());

        mTokenProviders = new TokenProviders();
        mAuthProviders = new AuthProviders();
        mUsuariosProvider = new UsuariosProviders();
        createToken();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, HomeActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, HomeActivity.this);
    }



    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.ItemHome){

                        openFragment(new HomeFragment());

                    }else if(item.getItemId() == R.id.ItemFiltros){

                        openFragment(new FiltrosFragment());

                    }else if(item.getItemId() == R.id.ItemChats){

                        openFragment(new ChatsFragment());

                    }else if(item.getItemId() == R.id.ItemPerfil){

                        openFragment(new PerfilFragment());

                    }
                    return true;
                    }

            };

    private void createToken(){
        mTokenProviders.creeate(mAuthProviders.getUid());
    }
}