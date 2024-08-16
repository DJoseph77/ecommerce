package com.example.ecommerce.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.Helper.NotificationHelper;
import com.example.ecommerce.Helper.SharedPreferencesManager;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ViewholderOrderBinding;
import com.example.ecommerce.domain.Notification;
import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.Products_order;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private ArrayList<Order> orders;
    private Context context;
    private DatabaseReference databaseReference;
    private SharedPreferencesManager sharedPreferencesManager;

    public OrderAdapter(ArrayList<Order> orders, Context context) {
        this.orders = orders;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewholderOrderBinding binding = ViewholderOrderBinding.inflate(inflater, parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        sharedPreferencesManager=new SharedPreferencesManager(context);
        String idUser=sharedPreferencesManager.getUserId();
        databaseReference=FirebaseDatabase.getInstance().getReference();
        Order order = orders.get(position);
        holder.binding.name.setText(order.getUserName());
        holder.binding.date.setText(order.getDateOrder());
        holder.binding.price.setText(order.getTotalPrice());
        holder.binding.localisation.setText(order.getUserAddress());
        if (order.getStatus().equals("delivred")){
            holder.binding.confirmButton.setVisibility(View.GONE);
        }
        holder.binding.Order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomAlertDialog(order.getUserName(), order.getUserId(), order.getDateOrder(), order.getStatus(), order.getUserAddress(), order.getTotalPrice(),order.getUserPhoneNumber());
            }
        });

        holder.binding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order.getStatus().equals("in progress")) {
                    changeStatusConfirm(order.getDateOrder(), order.getUserId());
                    notifyDataSetChanged();
                    NotificationHelper.showNotification(context, "Order Confirmed", "Your order has been confirmed.");
                    String message = "Your Order Has been Confirmed";  // Replace with your actual message
                    String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    boolean seen = false;

                    // Create a Notification object
                    DatabaseReference notificationRef=databaseReference.child("notifications").child(idUser).push();
                    String notificationKey=notificationRef.getKey();
                    Notification notification=new Notification(notificationKey,message,currentTime,seen);
                    notificationRef.setValue(notification);

                } else if (order.getStatus().equals("confirmed")) {
                    changeStatusDelivery(order.getDateOrder(), order.getUserId());
                    notifyDataSetChanged();
                    NotificationHelper.showNotification(context, "Order Delivered", "Your order has been delivered.");
                    String message = "Your Order Has been delivred";  // Replace with your actual message
                    String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    boolean seen = false;

                    // Create a Notification object
                    DatabaseReference notificationRef=databaseReference.child("notifications").child(idUser).push();
                    String notificationKey=notificationRef.getKey();
                    Notification notification=new Notification(notificationKey,message,currentTime,seen);
                    notificationRef.setValue(notification);

                }
            }
        });

        holder.binding.annulButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchOrder(order.getDateOrder(), order.getUserId(), new OrderChangeCallback() {
                    @Override
                    public void onOrderFound(String orderId) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference orderPath = databaseReference.child("orders").child(orderId);
                        orderPath.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "removed", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private void searchOrder(String dateOrder, String userId, final OrderChangeCallback callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference orderRef = databaseReference.child("orders");
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String orderId = null;
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    String orderDate = orderSnapshot.child("DateOrder").getValue(String.class);
                    String orderUserId = orderSnapshot.child("userId").getValue(String.class);
                    if (dateOrder.equals(orderDate) && userId.equals(orderUserId)) {
                        orderId = orderSnapshot.getKey();
                        break; // Stop loop if found
                    }
                }
                if (callback != null) {
                    callback.onOrderFound(orderId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void changeStatusConfirm(String dateOrder, String userId) {
        searchOrder(dateOrder, userId, new OrderChangeCallback() {
            @Override
            public void onOrderFound(String orderId) {
                if (orderId != null) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference orderPath = databaseReference.child("orders").child(orderId);
                    orderPath.child("status").setValue("confirmed");
                }
            }
        });
    }

    private void changeStatusDelivery(String dateOrder, String userId) {
        searchOrder(dateOrder, userId, new OrderChangeCallback() {
            @Override
            public void onOrderFound(String orderId) {
                if (orderId != null) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference orderPath = databaseReference.child("orders").child(orderId);
                    orderPath.child("status").setValue("delivred");
                }
            }
        });
    }


    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ViewholderOrderBinding binding;

        public OrderViewHolder(ViewholderOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    interface OrderChangeCallback {
        void onOrderFound(String orderId);
    }
    // Inside your activity or fragment
    public void showCustomAlertDialog(String userName, String userId, String dateOrder, String status, String userAdress, String price, String phone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_order, null);
        builder.setView(dialogView);

        // Initialize views
        TextView textName = dialogView.findViewById(R.id.textName);
        TextView textUserID = dialogView.findViewById(R.id.textUserID);
        RecyclerView recyclerViewProducts = dialogView.findViewById(R.id.recyclerViewProducts);
        TextView textDate = dialogView.findViewById(R.id.textDate);
        TextView textStatus = dialogView.findViewById(R.id.textStatus);
        TextView textAddress = dialogView.findViewById(R.id.textAddress);
        TextView textPhone = dialogView.findViewById(R.id.textPhone);
        TextView textPrice = dialogView.findViewById(R.id.textPrice);

        // Set data to views
        textName.setText("Name: " + userName);
        textUserID.setText("User ID: " + userId);
        textDate.setText("Date: " + dateOrder);
        textStatus.setText("Status: " + status);
        textAddress.setText("Address: " + userAdress);
        textPrice.setText("Price: " + price);
        textPhone.setText("Phone: " + phone);

        // Fetch products for this order
        ArrayList<Products_order> productsList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Retrieve the order details
        DatabaseReference orderRef = databaseReference.child("orders");
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    String orderDate = orderSnapshot.child("DateOrder").getValue(String.class);
                    String orderUserId = orderSnapshot.child("userId").getValue(String.class);
                    if (dateOrder.equals(orderDate) && userId.equals(orderUserId)) {
                        // Found matching order, now retrieve products
                        DataSnapshot productsSnapshot = orderSnapshot.child("nameProducts");
                        for (DataSnapshot productSnapshot : productsSnapshot.getChildren()) {
                            String productName = productSnapshot.child("name").getValue(String.class);
                            String quantity = String.valueOf(productSnapshot.child("nbrInCart").getValue(Long.class));

                            // Build product string
                            Products_order productInfo=new Products_order();
                            productInfo.setName(productName);
                            productInfo.setNumber(quantity);
                            productsList.add(productInfo);
                        }
                        break; // Stop loop if found
                    }
                }

                // Set up RecyclerView adapter with the list of products
                product_order_Adapter adapter=new product_order_Adapter(productsList);
                recyclerViewProducts.setAdapter(adapter);
                recyclerViewProducts.setLayoutManager(new LinearLayoutManager(context));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        // Set negative button
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
