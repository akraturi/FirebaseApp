package com.example.amit.snapchat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class ChooseSnapActivity extends AppCompatActivity {

    private ImageView mChosenImageView;
    private Button mChooseButton,mUploadButton;
    private EditText mMessageEditText;
    private String imageName=UUID.randomUUID().toString()+".jpg";
    private String imageUrl;


   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1)
        {
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                getPhoto();
            }
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_snap);

        mChosenImageView=findViewById(R.id.choosenimageView);
        mChooseButton=findViewById(R.id.chooseImagebutton);
        mUploadButton=findViewById(R.id.uploadbutton);
        mMessageEditText=findViewById(R.id.captioneditText);

        mChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });





    }
    public void getPhoto()
    {
        Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

       if(requestCode==1&&resultCode==RESULT_OK&&data!=null)
       {    Uri uri=data.getData();
         try{
             Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
             mChosenImageView.setImageBitmap(bitmap);
         }catch (Exception e)
         {
             e.printStackTrace();
         }
       }

        super.onActivityResult(requestCode, resultCode, data);
    }
    public void chooseImage()
    {   requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this,"Please grant permission",Toast.LENGTH_SHORT).show();
        }
        else
        {
            getPhoto();
        }
    }
    public void uploadImage()
    {
        // Get the data from an ImageView as bytes
        mChosenImageView.setDrawingCacheEnabled(true);
        mChosenImageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) mChosenImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();


        final UploadTask uploadTask =   FirebaseStorage.getInstance().getReference().child("images").child(imageName).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(ChooseSnapActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Toast.makeText(ChooseSnapActivity.this, "Choose user to send...", Toast.LENGTH_SHORT).show();
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.i("url::",uri.toString());
                        imageUrl=uri.toString();
                        moveToUserList();
                    }
                });



            }
        });


    }
    public void moveToUserList()
    {
        Intent intent =new Intent(ChooseSnapActivity.this,UserListActivity.class);
        intent.putExtra("from",FirebaseAuth.getInstance().getCurrentUser().getEmail());
        intent.putExtra("imageName",imageName);
        intent.putExtra("imageUrl",imageUrl);
        intent.putExtra("message",mMessageEditText.getText().toString());
        startActivity(intent);
    }
}
