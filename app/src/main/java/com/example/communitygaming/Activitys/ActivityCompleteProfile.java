package com.example.communitygaming.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.communitygaming.Models.Usuarios;
import com.example.communitygaming.Providers.AuthProviders;
import com.example.communitygaming.Providers.UsuariosProviders;
import com.example.communitygaming.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class ActivityCompleteProfile extends AppCompatActivity {

    TextInputEditText Usuario;
    TextInputEditText telefonoUsuario;

    Button btnConfirmarInfo;
    AuthProviders authProviders;
    UsuariosProviders usuariosProviders;
    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        Usuario = findViewById(R.id.AggNombreUsuario);
        telefonoUsuario = findViewById(R.id.AggTelefonoUsuario);
        btnConfirmarInfo = findViewById(R.id.Boton_confirm);

        usuariosProviders = new UsuariosProviders();
        authProviders = new AuthProviders();
        mDialog = new  SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false)
                .build();

        btnConfirmarInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }

    private void register() {
        String usuario = Usuario.getText().toString();
        String phone = telefonoUsuario.getText().toString();

        if (!usuario.isEmpty()) {
            AñadirUsuario(usuario, phone);
        } else {
            Toast.makeText(this, "Para continuar inserta todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void AñadirUsuario(String Usuario, String Phone){
        String id = authProviders.getUid();
        Usuarios usuarios = new Usuarios();
        usuarios.setUsuario(Usuario);
        usuarios.setPhone(Phone);
        usuarios.setId(id);
        usuarios.setFecha(new Date().getTime());
        mDialog.show();
        usuariosProviders.update(usuarios).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDialog.dismiss();
                if( task.isSuccessful()){
                    Intent intent = new Intent(ActivityCompleteProfile.this, HomeActivity.class);
                    startActivity(intent);

                }else{
                    Toast.makeText(ActivityCompleteProfile.this, "No se pudo almacenar el usuario en la base datos", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    }
