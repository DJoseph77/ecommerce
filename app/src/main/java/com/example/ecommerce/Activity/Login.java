package com.example.ecommerce.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecommerce.Helper.SharedPreferencesManager;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        databaseReference= FirebaseDatabase.getInstance().getReference();
        sharedPreferencesManager = new SharedPreferencesManager(this);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Navigate to Signup Activity
        binding.noAccountTxt.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, signup.class);
            startActivity(intent);
        });

        // Handle Login Button Click
        binding.loginBtn.setOnClickListener(v -> {
            String email = binding.edittxtEmail.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                binding.edittxtEmail.setError("Email is required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                binding.editTextPassword.setError("Password is required.");
                return;
            }

            loginUser(email, password);
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId=mAuth.getUid();
                            Toast.makeText(Login.this, "Login successful.", Toast.LENGTH_SHORT).show();
                            sharedPreferencesManager.setLogin(true,userId);
                            DatabaseReference pathAdmin=databaseReference.child("admin").child("email");
                            pathAdmin.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (email.equals(snapshot.getValue())) {
                                        Intent intent12 = new Intent(Login.this, DashBoardActivity.class);
                                        startActivity(intent12);
                                        finish();
                                    }else {
                                        Intent intent = new Intent(Login.this, Dashboard_user.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
