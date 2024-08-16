package com.example.ecommerce.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.Adapter.ProductCartAdapter;
import com.example.ecommerce.Helper.CartManager;
import com.example.ecommerce.Helper.SharedPreferencesManager;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityCartBinding;
import com.example.ecommerce.domain.Notification;
import com.example.ecommerce.domain.ProductCart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    ActivityCartBinding binding;
    private CartManager cartManager;
    private ArrayList<ProductCart> products;
    private ProductCartAdapter productCartAdapter;
    private SharedPreferencesManager sharedPreferencesManager;
    private DatabaseReference databaseReference;
    private ArrayList<String> nameProducts;
    private double subTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        nameProducts=new ArrayList<>();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        sharedPreferencesManager=new SharedPreferencesManager(this);
        cartManager = new CartManager(this);
        products = cartManager.getCartItems();
        productCartAdapter = new ProductCartAdapter(products, this);
        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(productCartAdapter);
        binding.backBtn.setOnClickListener(v -> finish());
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });
        updateCartUI();

        productCartAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updateCartUI();

            }
        });


    }

    private void updateCartUI() {
        subTotal = 0;
        for (ProductCart product : products) {
            subTotal += product.getNbrInCart() * product.getPrice();
        }

        binding.totalFeeTxt.setText(String.valueOf((int) subTotal) + " DT");
        binding.totalTxt.setText(String.valueOf((int) subTotal + 7) + " DT");
        String userId=sharedPreferencesManager.getUserId();
        DatabaseReference userAdress = databaseReference.child("users").child(userId).child("adress");
        userAdress.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.getValue(String.class);
                    binding.textView23.setText(name);
                } else {
                    binding.textView23.setText("Adress not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                binding.textView23.setText("Error: " + databaseError.getMessage());
            }
        });
        databaseReference.child("users").child(userId).child("lastGift").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String gift=snapshot.getValue(String.class);
                    if (gift.contains("%")){
                        int indexPercentage=gift.indexOf("%");
                        String numberStr=gift.substring(0,indexPercentage);
                        int percentage=Integer.parseInt(numberStr);
                        subTotal = subTotal -(subTotal /100)*40;
                        binding.totalDiscountTxt.setText(String.valueOf(subTotal+7));
                        binding.textWithDiscount.setText("Total After Discount ("+gift+")");

                    }else {
                        binding.textWithDiscount.setVisibility(View.GONE);
                        binding.totalDiscountTxt.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        showDiseppear();


    }
    private void placeOrder() {
        SharedPreferencesManager sharedPreferences = new SharedPreferencesManager(this);
        CartManager cartManager = new CartManager(this);
        String idUser = sharedPreferences.getUserId();
        DatabaseReference userRef = databaseReference.child("users").child(idUser);
        String message = "Your Order Has been Placed";  // Replace with your actual message
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        boolean seen = false;

        // Create a Notification object
        DatabaseReference notificationRef=databaseReference.child("notifications").child(idUser).push();
        String notificationKey=notificationRef.getKey();
        Notification notification=new Notification(notificationKey,message,currentTime,seen);
        notificationRef.setValue(notification);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("name").getValue(String.class);
                    String userAddress = dataSnapshot.child("adress").getValue(String.class);
                    String userPhoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);

                    ArrayList<ProductCart> productCarts = cartManager.getCartItems();
                    ArrayList<Map<String, Object>> nameProducts = new ArrayList<>();
                    double subTotal = 0;
                    for (ProductCart productCart : productCarts) {
                        Map<String, Object> productDetails = new HashMap<>();
                        productDetails.put("name", productCart.getTitle());
                        productDetails.put("nbrInCart", productCart.getNbrInCart());
                        nameProducts.add(productDetails);
                        subTotal += productCart.getNbrInCart() * productCart.getPrice();
                        subTotal=checkDiscount(subTotal);
                    }

                    Map<String, Object> order = new HashMap<>();
                    order.put("userId", idUser);
                    order.put("userName", userName);
                    order.put("userAddress", userAddress);
                    order.put("userPhoneNumber", userPhoneNumber);
                    order.put("nameProducts", nameProducts);
                    order.put("status", "in progress");
                    order.put("totalPrice", Math.round(subTotal) + 7 + " DT");
                    long timestamp = System.currentTimeMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String dateStr = sdf.format(new Date(timestamp));
                    order.put("DateOrder", dateStr);

                    DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");
                    String orderId = ordersRef.push().getKey();
                    if (orderId != null) {
                        ordersRef.child(orderId).setValue(order)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            cartManager.clearCart();
                                            Toast.makeText(getApplicationContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Failed to place order. Please try again.", Toast.LENGTH_SHORT).show();
                                        }
                                        finish();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error retrieving user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showDiseppear() {
        if (cartManager.getCartItems().isEmpty()) {
            binding.scroll.setVisibility(View.GONE);
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.deliveryTxt.setText("-");
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scroll.setVisibility(View.VISIBLE);
            binding.deliveryTxt.setText("7 DT");
        }
    }
    private double checkDiscount(double subTotal) {
        String userId=sharedPreferencesManager.getUserId();
        final double[] price = {subTotal};
        databaseReference.child("users").child(userId).child("lastGift").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String gift=snapshot.getValue(String.class);
                    if (gift.contains("%")){
                        int indexPercentage=gift.indexOf("%");
                        String numberStr=gift.substring(0,indexPercentage);
                        int percentage=Integer.parseInt(numberStr);
                        price[0] = price[0] -(price[0] /100)*40;

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return price[0];

    }
}
