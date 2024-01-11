package com.example.communitygaming.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.communitygaming.Models.Usuarios;
import com.example.communitygaming.Providers.AuthProviders;
import com.example.communitygaming.Providers.ImageProviders;
import com.example.communitygaming.Providers.UsuariosProviders;
import com.example.communitygaming.R;
import com.example.communitygaming.Utils.FileUtil;
import com.example.communitygaming.Utils.ViewedMessageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class EditProfileEditActivity extends AppCompatActivity {

    UsuariosProviders mUsuariosProviders;
    ImageProviders mImageProviders;
    AuthProviders mAuthProviders;

    Button mButtonEditProfile;
    CircleImageView mCircleImgBack;
    CircleImageView mCircleImageViewProfile;
    ImageView mImageViewFondo;
    TextInputEditText mUserName;
    TextInputEditText mTelefonoUser;
    File ImageFile;
    File ImageFile2;
    private final int GALLERY_REQUEST_CODE_PROFILE = 1;
    private final int GALLERY_REQUEST_CODE_COVER = 2;
    private final int PHOTO_REQUEST_CODE_PROFILE = 3;
    private final int PHOTO_REQUEST_CODE_COVER = 4;
    AlertDialog.Builder mDialogBuilder;
    CharSequence options[];

    //FOTO 1
    String mFotoPath;
    String fotoP;
    File fileFotoPath;

    //FOTO 2
    String mFotoPath2;
    String fotoP2;
    File fileFotoPath2;

    String username = "";
    String telefono = "";
    String mImageProfile = "";
    String mImageFondo = "";

    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_edit);

        mCircleImgBack = findViewById(R.id.circleImageBack);
        mUserName = findViewById(R.id.CampoNombreUsuario);
        mTelefonoUser = findViewById(R.id.CampoTextInputTelefono);
        mCircleImageViewProfile = findViewById(R.id.circleImagePerfil);
        mImageViewFondo = findViewById(R.id.imageFondo);
        mButtonEditProfile = findViewById(R.id.btnEditProfile);

        mImageProviders = new ImageProviders();
        mUsuariosProviders = new UsuariosProviders();
        mAuthProviders = new AuthProviders();

        mDialog = new  SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false)
                .build();

        mDialogBuilder = new AlertDialog.Builder(this);
        mDialogBuilder.setTitle("Selecciona una opcion");

        options = new CharSequence[] {"Imagen de galeria", "Tomar foto"};

        mButtonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEditProfile();
            }
        });

        mCircleImageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectOptionsImage(1);
            }
        });

        mImageViewFondo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectOptionsImage(2);
            }
        });

        mCircleImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getUsuario();
    }

    public void getUsuario(){
        mUsuariosProviders.getUser(mAuthProviders.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    if (documentSnapshot.contains("usuario")){
                        username = documentSnapshot.getString("usuario");
                        mUserName.setText(username);
                    }
                    if (documentSnapshot.contains("phone")){
                        telefono = documentSnapshot.getString("phone");
                        mTelefonoUser.setText(telefono);
                    }
                    if (documentSnapshot.contains("image_pofile")){
                        mImageProfile = documentSnapshot.getString("image_profile");
                        if (mImageProfile != null){
                            if (!mImageProfile.isEmpty()){
                                Picasso.with(EditProfileEditActivity.this).load(mImageProfile).into(mCircleImageViewProfile);
                            }
                        }

                    }
                    if (documentSnapshot.contains("image_fondo")){
                        mImageFondo = documentSnapshot.getString("image_fondo");
                        if (mImageFondo != null){
                            if (!mImageFondo.isEmpty()){
                                Picasso.with(EditProfileEditActivity.this).load(mImageFondo).into(mImageViewFondo);
                            }
                        }

                    }

                }
            }
        });
    }

    private void clickEditProfile() {

        username = mUserName.getText().toString();
        telefono = mTelefonoUser.getText().toString();
        if(!username.isEmpty() && !telefono.isEmpty()){

            if (ImageFile != null && ImageFile2 != null){
                saveImageFondoAnProfile(ImageFile, ImageFile2);
            }
            //TOMO AMBAS FOTOS DESDE LA CAMARA
            else if (fileFotoPath != null && fileFotoPath2 != null){
                saveImageFondoAnProfile(fileFotoPath, fileFotoPath2);
            }
            //SELECCIONÓ LA PRIMERA FOTO DE GALLERIA && LA SEGUNDA LA TOMO DE LA CAMARA
            else if (ImageFile != null && fileFotoPath2 != null){
                saveImageFondoAnProfile(ImageFile, fileFotoPath2);
            }
            //VICEVERSA
            else if (fileFotoPath != null && ImageFile2 != null) {
                saveImageFondoAnProfile(fileFotoPath, ImageFile2);

            }else if (fileFotoPath != null){
                saveImage(fileFotoPath, true);

            }else if (fileFotoPath2 != null){
                saveImage(fileFotoPath2, true);

            }else if (ImageFile != null){
                saveImage(ImageFile, true);

            }else if (ImageFile2 != null){
                saveImage(ImageFile2, true);
            }
            else{
                Usuarios usuarios =  new Usuarios();
                usuarios.setUsuario(username);
                usuarios.setPhone(telefono);
                usuarios.setId(mAuthProviders.getUid());
                updateInfo(usuarios);
            }

        }else{
            Toast.makeText(this, "Ingrese el nombre de usario y el telefeno", Toast.LENGTH_SHORT).show();
        }
    }



    private void SelectOptionsImage(int numberImage) {

        mDialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){
                    if (numberImage == 1){
                        AbrirGalleria(GALLERY_REQUEST_CODE_PROFILE);

                    }else if (numberImage == 2){
                        AbrirGalleria(GALLERY_REQUEST_CODE_COVER);

                    }

                }
                else if (i == 1){
                    if (numberImage == 1){
                        TomarFoto(PHOTO_REQUEST_CODE_PROFILE);

                    }else if(numberImage == 2){
                        TomarFoto(PHOTO_REQUEST_CODE_COVER);
                    }
                }
            }
        });

        mDialogBuilder.show();
    }

    private void saveImageFondoAnProfile(File mImageFile, File mImageFile2) {
        mDialog.show();
        mImageProviders.save(EditProfileEditActivity.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    mImageProviders.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String urlProfile = uri.toString();
                            mImageProviders.save(EditProfileEditActivity.this, mImageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if(taskImage2.isSuccessful()){
                                        mImageProviders.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                String urlCover = uri2.toString();
                                                Usuarios usuarios = new Usuarios();
                                                usuarios.setUsuario(username);
                                                usuarios.setPhone(telefono);
                                                usuarios.setImageProfile(urlProfile);
                                                usuarios.setImageFondo(urlCover);
                                                usuarios.setId(mAuthProviders.getUid());
                                                updateInfo(usuarios);
                                            }
                                        });
                                    }else {
                                        mDialog.dismiss();
                                        Toast.makeText(EditProfileEditActivity.this, "La imagen numero 2 no se pudo almacenar", Toast.LENGTH_SHORT).show();
                                    }
                                }


                            });
                        }
                    });

                }else{
                    mDialog.dismiss();
                    Toast.makeText(EditProfileEditActivity.this, "Se produjo un error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveImage(File Image, boolean isProfileImage){
        mDialog.show();
        mImageProviders.save(EditProfileEditActivity.this, Image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    mImageProviders.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url = uri.toString();
                            Usuarios usuarios = new Usuarios();
                            usuarios.setUsuario(username);
                            usuarios.setPhone(telefono);
                            if (isProfileImage){
                                usuarios.setImageProfile(url);
                                usuarios.setImageFondo(mImageFondo);
                            }else {
                                usuarios.setImageFondo(url);
                                usuarios.setImageProfile(mImageProfile);
                            }
                            usuarios.setId(mAuthProviders.getUid());
                            updateInfo(usuarios);
                        }
                    });

                }else{
                    mDialog.dismiss();
                    Toast.makeText(EditProfileEditActivity.this, "Se produjo un error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateInfo(Usuarios usuarios){
        if (mDialog.isShowing()){
            mDialog.show();
        }
        mUsuariosProviders.update(usuarios).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(EditProfileEditActivity.this, "La información se actualizó correctamente", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(EditProfileEditActivity.this, "La información no se pudo actualizar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void TomarFoto(int requestCode) {

        Intent tomarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (tomarFotoIntent.resolveActivity(getPackageManager()) != null){
            File fotoFile = null;
            try {
                fotoFile = createFotofile(requestCode);
            }catch (Exception e){
                Toast.makeText(this, "Hubo un error con el archivo" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            if (fotoFile != null){
                Uri photoUri = FileProvider.getUriForFile(EditProfileEditActivity.this, "com.example.communitygaming", fotoFile);
                tomarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(tomarFotoIntent, requestCode);
            }
        }
    }

    private File createFotofile(int requestCode) throws IOException {

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                new Date() + "_Foto",
                ".jpg",
                storageDir
        );
        if (requestCode == PHOTO_REQUEST_CODE_PROFILE){
            fotoP = "file:" + photoFile.getAbsolutePath();
            mFotoPath = photoFile.getAbsolutePath();

        }else if (requestCode == PHOTO_REQUEST_CODE_COVER){

            fotoP2 = "file:" + photoFile.getAbsolutePath();
            mFotoPath2 = photoFile.getAbsolutePath();
        }

        return photoFile;
    }

    private void AbrirGalleria(int requestCode) {
        Intent GalleriaInten = new Intent(Intent.ACTION_GET_CONTENT);
        GalleriaInten.setType("image/*");
        startActivityForResult(GalleriaInten, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
         * SELECCION DE IMAGEN DESDE LA GALLERIA
         */

        if (requestCode == GALLERY_REQUEST_CODE_PROFILE && resultCode == RESULT_OK){
            try {
                fileFotoPath = null;
                ImageFile = FileUtil.from(this, data.getData());
                mCircleImageViewProfile.setImageBitmap(BitmapFactory.decodeFile(ImageFile.getAbsolutePath()));
            }catch (Exception e){
                Log.d("ERROR", "Se  produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == GALLERY_REQUEST_CODE_COVER && resultCode == RESULT_OK){
            try {
                fileFotoPath2 = null;
                ImageFile2 = FileUtil.from(this, data.getData());
                mImageViewFondo.setImageBitmap(BitmapFactory.decodeFile(ImageFile2.getAbsolutePath()));
            }catch (Exception e){
                Log.d("ERROR", "Se  produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        /*
         * SELECCION DE FOTOGRAFIA
         */

        if (requestCode == PHOTO_REQUEST_CODE_PROFILE && resultCode == RESULT_OK) {
            ImageFile = null;
            fileFotoPath = new File(mFotoPath);
            Picasso.with(EditProfileEditActivity.this).load(fotoP).into(mCircleImageViewProfile);
        }

        /*
         * SELECCION DE FOTOGRAFIA 2
         */

        if (requestCode == PHOTO_REQUEST_CODE_COVER && resultCode == RESULT_OK) {
            ImageFile2 = null;
            fileFotoPath2 = new File(mFotoPath2);
            Picasso.with(EditProfileEditActivity.this).load(fotoP2).into(mImageViewFondo);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, EditProfileEditActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, EditProfileEditActivity.this);
    }
}