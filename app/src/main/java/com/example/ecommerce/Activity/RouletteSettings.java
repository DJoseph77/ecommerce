package com.example.ecommerce.Activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.Adapter.SectionAdapter;
import com.example.ecommerce.R;
import com.example.ecommerce.domain.RouletteSection;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RouletteSettings extends AppCompatActivity implements SectionAdapter.OnItemClickListener {

    private DatabaseReference databaseReference;
    private Button buttonAdd;
    private Button buttonDelete;
    private EditText gift, percentage;
    private RecyclerView recyclerView;
    private ArrayList<RouletteSection> rouletteSections;
    private SectionAdapter adapter;
    private int selectedPosition = -1;

    @Override
    public void onItemClick(int position) {
        this.selectedPosition = position;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roulette_settings);
        databaseReference = FirebaseDatabase.getInstance().getReference("roulette");

        buttonAdd = findViewById(R.id.buttonAdd);
        buttonDelete = findViewById(R.id.buttonDelete);
        gift = findViewById(R.id.editGift);
        percentage = findViewById(R.id.editPercentage);
        recyclerView = findViewById(R.id.sectionRecycler);
        rouletteSections = new ArrayList<>();

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                RouletteSection rouletteSection = snapshot.getValue(RouletteSection.class);
                if (rouletteSection != null) {
                    rouletteSection.setId(snapshot.getKey());
                    rouletteSections.add(rouletteSection);
                    adapter.notifyItemInserted(rouletteSections.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                RouletteSection updatedSection = snapshot.getValue(RouletteSection.class);
                if (updatedSection != null) {
                    String key = snapshot.getKey();
                    for (int i = 0; i < rouletteSections.size(); i++) {
                        if (rouletteSections.get(i).getId().equals(key)) {
                            rouletteSections.set(i, updatedSection);
                            adapter.notifyItemChanged(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String removedId = snapshot.getKey();
                for (int i = 0; i < rouletteSections.size(); i++) {
                    if (rouletteSections.get(i).getId().equals(removedId)) {
                        rouletteSections.remove(i);
                        adapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle data move if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RouletteSettings.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new SectionAdapter(rouletteSections, this, this);
        recyclerView.setAdapter(adapter);

        buttonAdd.setOnClickListener(v -> {
            String giftText = gift.getText().toString();
            String percentageText = percentage.getText().toString();
            if (!giftText.isEmpty() && !percentageText.isEmpty()) {
                String id = databaseReference.push().getKey();
                RouletteSection rouletteSection = new RouletteSection(id, giftText, percentageText);
                databaseReference.child(id).setValue(rouletteSection);
            } else {
                Toast.makeText(this, "Make sure the gift and the percentage are not empty", Toast.LENGTH_SHORT).show();
            }
        });

        buttonDelete.setOnClickListener(v -> {
            if (selectedPosition != -1 && selectedPosition < rouletteSections.size()) {
                deleteItem(selectedPosition);
            } else {
                Toast.makeText(RouletteSettings.this, "Please select an item to delete", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteItem(int selectedPosition) {
        RouletteSection sectionToDelete = rouletteSections.get(selectedPosition);
        databaseReference.child(sectionToDelete.getId()).removeValue();
    }
}
