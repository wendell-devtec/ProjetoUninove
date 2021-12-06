package com.tw4rs.notesuni9;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class addActivity extends AppCompatActivity {
    private EditText etAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etAdd = findViewById(R.id.etMessage);


        Button add = findViewById(R.id.btnAddM);
        add.setOnClickListener(v -> sendMessage1());

    }

    DatabaseReference withdrawRef;
    private void sendMessage1() {

        FirebaseDatabase database =  FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user =  mAuth.getCurrentUser();
        assert user != null;
        withdrawRef = FirebaseDatabase.getInstance().getReference().child("Uninove").child("message").child(user.getUid());
        String id = withdrawRef.push().getKey();


        String message1 = etAdd.getText().toString();

        HashMap<String, Object> map = new HashMap<>();
        map.put("message",message1);
        map.put("id",id);

        withdrawRef.child(id).setValue(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(this,MainActivity.class));
                    }
                });

    }
}