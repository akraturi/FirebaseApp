package com.example.amit.snapchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class SnapViewerActivity extends AppCompatActivity {

    private TextView mCaptionTextView;
    private ImageView mSnapImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap_viewer);

        mCaptionTextView=findViewById(R.id.captiontextView);
        mSnapImageView=findViewById(R.id.imageView);
        mCaptionTextView.setText(getIntent().getStringExtra("message"));
        downloadImage();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        deleteSnap();

    }

    public void downloadImage()
    {
        Picasso.get().load(getIntent().getStringExtra("imageUrl")).into(mSnapImageView);
    }
    public void deleteSnap()
    {
        // remove snap from the database
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("snaps").child(getIntent().getStringExtra("snapkey")).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("removed from db:","sucess!");
            }
        });
        // delete image from the storage
        FirebaseStorage.getInstance().getReference().child("images").child(getIntent().getStringExtra("name")).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("removed from storage:","sucess!");
            }
        });
    }


}
