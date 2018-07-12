package com.example.amit.snapchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRegistrar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SnapActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ListView mRecievedSnapsList;
    private ArrayAdapter<String> mArrayAdapter;
    private ArrayList<String> mRecievedSnaps;
    private ArrayList<DataSnapshot> snapshots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap);

        mRecievedSnapsList=findViewById(R.id.listview);
        mRecievedSnaps=new ArrayList<>();
        snapshots=new ArrayList<>();
        mArrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,mRecievedSnaps);
        mRecievedSnapsList.setAdapter(mArrayAdapter);
        firebaseAuth=FirebaseAuth.getInstance();
        mRecievedSnapsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewSnap(position);
            }
        });
        Log.i("oncreate:","oncreate of snap activity is called");

        getSnaps();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=new MenuInflater(this);
        inflater.inflate(R.menu.options,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        { case R.id.createsnap:
            createSnap();
            return true;
            case R.id.log_out:
                logOut();
                return true;

        }
        return false;
    }
    public void logOut()
    {
        firebaseAuth.signOut();
        finish();
    }
    public void createSnap()
    {
        Intent intent=new Intent(SnapActivity.this,ChooseSnapActivity.class);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        logOut();
       finish();
    }
    public void getSnaps()
    {
        FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.getCurrentUser().getUid()).child("snaps").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String email= dataSnapshot.child("from").getValue().toString();
                mRecievedSnaps.add(email);
                snapshots.add(dataSnapshot);
                mArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                //Remove the list of emails whose snaps have been deleted from the database
                Log.i("inside remove child","looping to remove");
                for(int i=0;i<mRecievedSnaps.size();i++)
                {
                    if(snapshots.get(i).getKey().equals(dataSnapshot.getKey()))
                    {
                        mRecievedSnaps.remove(i);
                        snapshots.remove(i);
                        Log.i("removed!","I did it!");

                    }
                }
                mArrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void viewSnap(int i)
    {
        Intent intent =new Intent(SnapActivity.this,SnapViewerActivity.class);
        intent.putExtra("message",snapshots.get(i).child("message").getValue().toString());
        intent.putExtra("imageUrl",snapshots.get(i).child("imageUrl").getValue().toString());
        Log.i("imageurl::", snapshots.get(i).child("imageUrl").getValue().toString());
        intent.putExtra("name",snapshots.get(i).child("imageName").getValue().toString());
        intent.putExtra("snapkey",snapshots.get(i).getKey());
        startActivity(intent);
    }

    /*@Override
    protected void onResume() {
        Log.i("onResume called","yes i was right!");
        getSnaps();
        super.onResume();
    }*/
}
