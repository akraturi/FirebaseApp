package com.example.amit.snapchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserListActivity extends AppCompatActivity {

    private ListView mUsersList;
    private ArrayAdapter<String> mArrayAdapter;
    private ArrayList<String> mEmails;
    private ArrayList<String> mUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        mUsersList=findViewById(R.id.listview);
        mUsersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              sendSnap(position);
            }
        });
        mEmails=new ArrayList<>();
        mUid=new ArrayList<>();
        mArrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,mEmails);
        mUsersList.setAdapter(mArrayAdapter);
        getUsers();




    }
    public void getUsers()
    {
        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String email=dataSnapshot.child("email").getValue().toString();
                mEmails.add(email);
                mUid.add(dataSnapshot.getKey());
                mArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void sendSnap(int i)
    {
        Map<String,String> snapMap=new HashMap<>();
        snapMap.put("from",getIntent().getStringExtra("from"));
        snapMap.put("imageName",getIntent().getStringExtra("imageName"));
        snapMap.put("imageUrl",getIntent().getStringExtra("imageUrl"));
        snapMap.put("message",getIntent().getStringExtra("message"));
        FirebaseDatabase.getInstance().getReference().child("users").child(mUid.get(i)).child("snaps").push().setValue(snapMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(UserListActivity.this, "Snap sent successfully!", Toast.LENGTH_SHORT).show();
                moveToSnaps();
            }
        });
    }
    public void moveToSnaps()
    {
        Intent intent=new Intent(UserListActivity.this,SnapActivity.class);
        startActivity(intent);
    }
}
