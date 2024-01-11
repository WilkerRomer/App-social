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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {

    CircleImageView imgBack;

    TextInputEditText Usuario, Correo;
    TextInputEditText Contraseña, ConfirmContraseña, CampoTelefono;
    Button btnRegistrarse;
    AuthProviders authProviders;
    UsuariosProviders usuariosProviders;
    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        imgBack = findViewById(R.id.circleImageBack);
        Usuario = findViewById(R.id.CampoNombreUsuario);
        Correo = findViewById(R.id.CampoCorreo);
        Contraseña = findViewById(R.id.CampoContraseña);
        ConfirmContraseña = findViewById(R.id.CampoContraseñaConfir);
        CampoTelefono = findViewById(R.id.CampoTelefono);
        btnRegistrarse = findViewById(R.id.Boton_Registrarse);

        authProviders = new AuthProviders();
        usuariosProviders = new UsuariosProviders();
        mDialog = new  SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false)
                .build();

        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void register(){
        String usuario = Usuario.getText().toString();
        String correo = Correo.getText().toString();
        String contraseña = Contraseña.getText().toString();
        String confirContraseña = ConfirmContraseña.getText().toString();
        String phone = CampoTelefono.getText().toString();

        if (!usuario.isEmpty() && !correo.isEmpty() && !contraseña.isEmpty() && !confirContraseña.isEmpty() && !phone.isEmpty()){
            if (isEmailValid(correo)){
                if (contraseña.equals(confirContraseña)){
                    if (contraseña.length() >= 6){

                        CrearUsuario(correo, contraseña, usuario, phone);
                    }
                    else{
                        Toast.makeText(this, "El minimo de caracteres es de 6", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }

            }
            else{
                Toast.makeText(this, "Has insertado todos los campos pero el email no es valido", Toast.LENGTH_LONG).show();
            }

        }
        else{
            Toast.makeText(this, "Para validar la accion inserta todos los campos", Toast.LENGTH_LONG).show();
        }

    }

    private void CrearUsuario(String email, String contraseña, String usuario, String phone){
        mDialog.show();
        authProviders.Register(email, contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mDialog.dismiss();
                if (task.isSuccessful()){
                    String id = authProviders.getUid();

                    Usuarios usuarios = new Usuarios();
                    usuarios.setId(id);
                    usuarios.setEmail(email);
                    usuarios.setUsuario(usuario);
                    usuarios.setPhone(phone);
                    usuarios.setFecha(new Date().getTime());

                    usuariosProviders.create(usuarios).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mDialog.dismiss();
                            if( task.isSuccessful()){
                                Intent intent = new Intent(RegisterActivity.this,HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }else{
                                Toast.makeText(RegisterActivity.this, "No se pudo almacenar el usuario en la base datos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    mDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}