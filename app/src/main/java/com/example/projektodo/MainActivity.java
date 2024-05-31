package com.example.projektodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    RecyclerView recyclerView;
    List<DataClass> dataList;
    private final int MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // if permission not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)  {
            //permission not granted
            // should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Request Permission");
                builder.setMessage("You should enable this permission to ACCES LOCATION");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                    }
                });
                builder.setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                    }
                });
                builder.show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            // permission has already been granted
        }


        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();

        MyAdapter adapter = new MyAdapter(MainActivity.this, dataList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Android Tutorials");
        dialog.show();

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                    dataClass.setKey(itemSnapshot.getKey());
                    dataList.add(dataClass);
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                // if request is canceled, the array is empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Terimakasih,Silahkan Menggunakan Aplikasi Dengan Bijak", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Izin Tidak Dibolehkan,Silahkan Izinkan Akses", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // there can be more cases for more different permissions here
        }
    }
}