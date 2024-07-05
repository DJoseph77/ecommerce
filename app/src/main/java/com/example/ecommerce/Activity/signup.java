package com.example.ecommerce.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecommerce.R;
import com.example.ecommerce.databinding.ActivitySignupBinding;
import com.example.ecommerce.domain.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends AppCompatActivity {
    ActivitySignupBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    String email,name,password,phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding=ActivitySignupBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent2=getIntent();
        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=binding.editTextName.getText().toString();
                email=binding.editTextEmail.getText().toString();
                password=binding.editTextPassword.getText().toString();
                phoneNumber=binding.editTextPhoneNumber.getText().toString();
                createAccount(name,email,password,phoneNumber);
                Intent intent3=new Intent(signup.this, Login.class);
            }
        });

    }

    private void createAccount(String name, String email, String password, String phoneNumber) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(signup.this, "Auth successful", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        // Get user ID
                        String userId = user.getUid();
                        // Write user data to the database
                        writeNewUser(userId);
                    }

                }else {
                    Toast.makeText(signup.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void writeNewUser(String userId) {
        // Create a User object
        User user = new User(email,password,name,phoneNumber);
        // Write a User object to the Firebase Realtime Database
        mDatabase.child("users").child(userId).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(signup.this, "User data saved.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(signup.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}