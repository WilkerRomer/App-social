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
import android.widget.TextView;
import android.widget.Toast;

import com.example.communitygaming.Models.Post;
import com.example.communitygaming.Providers.AuthProviders;
import com.example.communitygaming.Providers.ImageProviders;
import com.example.communitygaming.Providers.PostProviders;
import com.example.communitygaming.R;
import com.example.communitygaming.Utils.FileUtil;
import com.example.communitygaming.Utils.ViewedMessageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class PostActivity extends AppCompatActivity {

    ImageProviders mImageProviders;
    PostProviders postProviders;
    AuthProviders mAuthProviders;
    ImageView ImageView1;
    ImageView ImageView2;
    File ImageFile;
    File ImageFile2;
    Button PublicarBTN;
    CircleImageView BTNImageBack;
    TextInputEditText textInputTitle;
    TextInputEditText textInpuTDescription;
    ImageView Image_Pc, Image_Ps4, Image_Xbox, Image_Nintendo;
    TextView TextCategories;
    private final int GALLERY_REQUEST_CODE = 1;
    private final int GALLERY_REQUEST_CODE_2 = 2;
    private final int PHOTO_REQUEST_CODE = 3;
    private final int PHOTO_REQUEST_CODE_2 = 4;
    String mCategory = "";
    String mtitle = "";
    String mDescription = "";
    AlertDialog mDialog;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mImageProviders = new ImageProviders();
        postProviders = new PostProviders();
        mAuthProviders = new AuthProviders();

        mDialog = new  SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false)
                .build();

        options = new CharSequence[] {"Imagen de galeria", "Tomar foto"};

        mDialogBuilder = new AlertDialog.Builder(this);
        mDialogBuilder.setTitle("Selecciona una opcion");

        textInputTitle = findViewById(R.id.CampoNombreJuego);
        textInpuTDescription = findViewById(R.id.CampoDescripcion);
        TextCategories = findViewById(R.id.TextViewCategorias);

        Image_Pc = findViewById(R.id.imagePc);
        Image_Ps4 = findViewById(R.id.imagePs4);
        Image_Xbox = findViewById(R.id.imageXbox);
        Image_Nintendo = findViewById(R.id.imageNintendo);

        ImageView1 = findViewById(R.id.SubirImage1);
        ImageView2 = findViewById(R.id.SubirImage2);

        PublicarBTN = findViewById(R.id.BotonPublicar);
        BTNImageBack = findViewById(R.id.ImageBack);

        BTNImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        PublicarBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPost();
            }
        });

        ImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectOptionsImage(1);

            }
        });

        ImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectOptionsImage(2);
            }
        });

        Image_Pc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "PC";
                TextCategories.setText(mCategory);
            }
        });

        Image_Ps4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "PS4";
                TextCategories.setText(mCategory);
            }
        });

        Image_Xbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "XBOX";
                TextCategories.setText(mCategory);
            }
        });

        Image_Nintendo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategory = "NINTENDO";
                TextCategories.setText(mCategory);
            }
        });
    }

    private void SelectOptionsImage(int numberImage) {

        mDialogBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){
                    if (numberImage == 1){
                        AbrirGalleria(GALLERY_REQUEST_CODE);

                    }else if (numberImage == 2){
                        AbrirGalleria(GALLERY_REQUEST_CODE_2);

                    }

                }
                else if (i == 1){
                    if (numberImage == 1){
                        TomarFoto(PHOTO_REQUEST_CODE);

                    }else if(numberImage == 2){
                        TomarFoto(PHOTO_REQUEST_CODE_2);
                    }
                }
            }
        });

        mDialogBuilder.show();
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
                Uri photoUri = FileProvider.getUriForFile(PostActivity.this, "com.example.communitygaming", fotoFile);
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
        if (requestCode == PHOTO_REQUEST_CODE){
            fotoP = "file:" + photoFile.getAbsolutePath();
            mFotoPath = photoFile.getAbsolutePath();

        }else if (requestCode == PHOTO_REQUEST_CODE_2){

            fotoP2 = "file:" + photoFile.getAbsolutePath();
            mFotoPath2 = photoFile.getAbsolutePath();
        }

        return photoFile;
    }

    private void clickPost() {

        mtitle = textInputTitle.getText().toString();
        mDescription = textInpuTDescription.getText().toString();

        if (!mtitle.isEmpty() && !mDescription.isEmpty() && !mCategory.isEmpty()){
            //SELECCIONÓ AMBAS IMAGENES DE LA GALLERIA
            if (ImageFile != null && ImageFile2 != null){
                saveImage(ImageFile, ImageFile2);
            }
            //TOMO AMBAS FOTOS DESDE LA CAMARA
            else if (fileFotoPath != null && fileFotoPath2 != null){
                saveImage(fileFotoPath, fileFotoPath2);
            }
            //SELECCIONÓ LA PRIMERA FOTO DE GALLERIA && LA SEGUNDA LA TOMO DE LA CAMARA
            else if (ImageFile != null && fileFotoPath2 != null){
                saveImage(ImageFile, fileFotoPath2);
            }
            //VICEVERSA
            else if (fileFotoPath != null && ImageFile2 != null) {
                saveImage(fileFotoPath, ImageFile2);
            }
            else {
                Toast.makeText(this, "Debe seleccionar una imagen", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, "Debe completar todos para hacer la publicación", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage(File mImageFile, File mImageFile2) {
        mDialog.show();
        mImageProviders.save(PostActivity.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    mImageProviders.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String url = uri.toString();
                            mImageProviders.save(PostActivity.this, mImageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if(taskImage2.isSuccessful()){
                                        mImageProviders.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                String url2 = uri2.toString();
                                                Post post = new Post();
                                                post.setImage1(url);
                                                post.setImage2(url2);
                                                post.setTitle(mtitle.toLowerCase());
                                                post.setDescription(mDescription);
                                                post.setCategory(mCategory);
                                                post.setIdUsuario(mAuthProviders.getUid());
                                                post.setFecha(new Date().getTime());
                                                postProviders.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        mDialog.dismiss();
                                                        if (task.isSuccessful()){
                                                            clearForm();
                                                            Toast.makeText(PostActivity.this, "La informacion se almacenó correctamente", Toast.LENGTH_SHORT).show();

                                                        } else {
                                                            Toast.makeText(PostActivity.this, "No se pudo almacenar la informacion", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }else {
                                        mDialog.dismiss();
                                        Toast.makeText(PostActivity.this, "La imagen numero 2 no se pudo almacenar", Toast.LENGTH_SHORT).show();
                                    }
                                }


                            });
                        }
                    });

                }else{
                    mDialog.dismiss();
                    Toast.makeText(PostActivity.this, "Se produjo un error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clearForm() {

        textInputTitle.setText("");
        textInpuTDescription.setText("");
        TextCategories.setText("");
        ImageView1.setImageResource(R.drawable.upload_image);
        ImageView2.setImageResource(R.drawable.upload_image);
        mtitle = "";
        mDescription = "";
        ImageFile = null;
        ImageFile2 = null;
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

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            try {
                fileFotoPath = null;
                ImageFile = FileUtil.from(this, data.getData());
                ImageView1.setImageBitmap(BitmapFactory.decodeFile(ImageFile.getAbsolutePath()));
            }catch (Exception e){
                Log.d("ERROR", "Se  produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == GALLERY_REQUEST_CODE_2 && resultCode == RESULT_OK){
            try {
                fileFotoPath2 = null;
                ImageFile2 = FileUtil.from(this, data.getData());
                ImageView2.setImageBitmap(BitmapFactory.decodeFile(ImageFile2.getAbsolutePath()));
            }catch (Exception e){
                Log.d("ERROR", "Se  produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        /*
        * SELECCION DE FOTOGRAFIA
        */

        if (requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            ImageFile = null;
            fileFotoPath = new File(mFotoPath);
            Picasso.with(PostActivity.this).load(fotoP).into(ImageView1);
        }

        /*
         * SELECCION DE FOTOGRAFIA 2
         */

        if (requestCode == PHOTO_REQUEST_CODE_2 && resultCode == RESULT_OK) {
            ImageFile2 = null;
            fileFotoPath2 = new File(mFotoPath2);
            Picasso.with(PostActivity.this).load(fotoP2).into(ImageView2);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, PostActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, PostActivity.this);
    }
}