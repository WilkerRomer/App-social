package com.example.communitygaming.Providers;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthProviders {

    private FirebaseAuth mAuth;

    public AuthProviders (){

        mAuth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> Register(String email, String contraseña){
        return mAuth.createUserWithEmailAndPassword(email, contraseña);
    }

    public Task<AuthResult> login(String email, String password){

       return mAuth.signInWithEmailAndPassword(email, password);

    }

    public Task<AuthResult> loginGoogle(String googleSignInAccount){
        AuthCredential credential = GoogleAuthProvider.getCredential(String.valueOf(googleSignInAccount), null);
        return mAuth.signInWithCredential(credential);
    }

    public String getEmail(){
        if (mAuth.getCurrentUser() != null){
            return mAuth.getCurrentUser().getEmail();
        }else{
            return null;
        }
    }

    public String getUid(){
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getUid();
        }else {
            return null;
        }
    }

    public FirebaseUser getUserSesion(){
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser();
        }else {
            return null;
        }
    }

    public void logout(){
        if (mAuth != null){
            mAuth.signOut();
        }

    }
}
