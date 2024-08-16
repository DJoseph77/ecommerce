package com.example.ecommerce.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce.Adapter.OrderAdapter;
import com.example.ecommerce.Helper.NotificationHelper;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityCartsInProgressBinding;
import com.example.ecommerce.domain.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class CartsInProgress extends AppCompatActivity {
    int numberOfOrders;
    ActivityCartsInProgressBinding binding;
    ArrayList orderList;
    OrderAdapter orderAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityCartsInProgressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Inside onCreate() method of your MainActivity or Application class
        NotificationHelper.createNotificationChannel(this);

        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList,this);
        binding.recycler.setAdapter(orderAdapter);
        Intent intent=getIntent();
        String status=intent.getStringExtra("status");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("orders");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"DefaultLocale", "NotifyDataSetChanged"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                numberOfOrders=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    switch (Objects.requireNonNull(status)) {
                        case "in progress":
                            if (order.getStatus().equals("in progress")) {
                                orderList.add(order);
                                numberOfOrders++;
                                orderAdapter.notifyDataSetChanged();
                            }
                            break;
                        case "confirmed":
                            assert order != null;
                            if (order.getStatus().equals("confirmed")) {
                                orderList.add(order);
                                numberOfOrders++;
                                orderAdapter.notifyDataSetChanged();
                            }
                            break;
                        case "delivred":
                            assert order != null;
                            if (order.getStatus().equals("delivred")) {
                                orderList.add(order);
                                numberOfOrders++;
                                orderAdapter.notifyDataSetChanged();
                            }

                            break;
                    }
                    binding.textView18.setText(String.format("Total Commands : %d", numberOfOrders));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(CartsInProgress.this, DashBoardActivity.class);
                startActivity(intent1);
                finish();
            }
        });
    }
}