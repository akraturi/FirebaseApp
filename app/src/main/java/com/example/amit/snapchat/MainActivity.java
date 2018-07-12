package com.example.amit.snapchat;



import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText mUsernameEditText,mPasswordEditText;
    private Button mLoginButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        mUsernameEditText=findViewById(R.id.usereditText);
        mPasswordEditText=findViewById(R.id.passwordeditText);

        mLoginButton=findViewById(R.id.button);

        firebaseAuth=FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()!=null)
        {
           moveToSnap();
        }
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logIn();
            }
        });


    }
    public void logIn()
    {
        firebaseAuth.signInWithEmailAndPassword(mUsernameEditText.getText().toString(),mPasswordEditText.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            String msg="";
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    msg="Login sucessful";
                    Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                    moveToSnap();
                }
                else
                {
                    msg="Signing you up....";
                    Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                    signUp();

                }
                //Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void signUp()
    {
        Log.i("inside signUp:","reached");
        firebaseAuth.createUserWithEmailAndPassword(mUsernameEditText.getText().toString(),mPasswordEditText.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            String msg="";
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    msg="SignUp succesful";
                    // write user to the database
                    FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid()).child("email").setValue(task.getResult().getUser().getEmail());
                    Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                    moveToSnap();
                }
                else
                {
                    msg="SignUp failed";
                    Log.i("signup failed:",task.getException().toString());
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void moveToSnap()
    {
        Intent intent =new Intent(MainActivity.this,SnapActivity.class);
        startActivity(intent);
    }

}
