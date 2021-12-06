package com.tw4rs.notesuni9;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tw4rs.notesuni9.adapter.NoteAdapter;
import com.tw4rs.notesuni9.model.NoteModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {

    //Firebase Variveis
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRef , reference , reference1;


    //Button
    private RecyclerView recyclerView;
    private CardView addMessage , removeAll;

    //Adapter
    NoteAdapter adapter;
    List<NoteModel> list;

    private String deletedUserID ,message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewM);



        FirebaseDatabase database =  FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user =  mAuth.getCurrentUser();
        String userId = user.getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Uninove").child("message").child(userId);

        addMessage = findViewById(R.id.addMessage);
        addMessage.setOnClickListener(v -> {
            startActivity(new Intent(this,addActivity.class));
        });


        removeAll = findViewById(R.id.removeAllM);
        removeAll.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Deseja realmente excluir?");
            builder.setMessage("Ao confirmar perderá todas as anotações feitas");
            builder.setPositiveButton("Sim , excluir",(dialog, which) -> {
              reference.removeValue();
            });
            builder.setNegativeButton("Não",(dialog, which) -> {
               dialog.dismiss();
            });
            builder.create();
            builder.show();
        });




        //ViewMessage
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false));

        list = new ArrayList<>();
        adapter = new NoteAdapter(list);

        recyclerView.setAdapter(adapter);

        reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        list.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            NoteModel model = dataSnapshot.getValue(NoteModel.class);
                            list.add(model);
                        }

                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Error: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();

                        finish();
                    }
                });

        //Swipe from right to left (Delete Specific User)
        swipeHandler();



    }

    private void swipeHandler() {

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            DatabaseReference withdrawRef;


            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                final int position = viewHolder.getAdapterPosition();

                switch (direction) {

                    case ItemTouchHelper.LEFT:
                        //Delete User

                        deletedUserID = list.get(position).getId();
                        message = list.get(position).getMessage();


                            withdrawRef = FirebaseDatabase.getInstance().getReference().child("Uninove").child("message").child(mAuth.getUid());


                            withdrawRef.child(deletedUserID).setValue(null)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {

                                            //To undo deleted User
                                            Snackbar.make(recyclerView, "Deletado", BaseTransientBottomBar.LENGTH_LONG)
                                                    .setAction("Desfazer", v -> {

                                                        HashMap<String, Object> map = new HashMap<>();
                                                        map.put("message", message);
                                                        map.put("id",deletedUserID);

                                                        reference.child(deletedUserID).updateChildren(map)
                                                                .addOnCompleteListener(task1 -> {
                                                                    if (task1.isSuccessful()) {
                                                                        Toast.makeText(MainActivity.this, "Desfazer",
                                                                                Toast.LENGTH_SHORT).show();

                                                                        adapter.notifyDataSetChanged();

                                                                    }
                                                                });

                                                    }).show();

                                        }
                                      //  dialog.dismiss();

                                    });


                            adapter.notifyItemRemoved(position);




                }


            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(getResources().getColor(R.color.colorRed))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

    }




}