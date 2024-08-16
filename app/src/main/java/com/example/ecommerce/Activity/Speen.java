package com.example.ecommerce.Activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecommerce.Helper.CustomWheelView;
import com.example.ecommerce.Helper.SharedPreferencesManager;
import com.example.ecommerce.R;
import com.example.ecommerce.domain.RouletteSection;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class Speen extends AppCompatActivity {
    private CustomWheelView wheelView;
    private Button spinButton;
    private DatabaseReference database;
    private ArrayList<Integer> percentages = new ArrayList<>();
    private ArrayList<String> labels = new ArrayList<>();
    private DatabaseReference userRef;  // Reference to the user's data
    private String userId ;
    private TextView textTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_speen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        wheelView = findViewById(R.id.wheel_view);
        spinButton = findViewById(R.id.spin_button);
        textTime=findViewById(R.id.textTime);
        database = FirebaseDatabase.getInstance().getReference();
        SharedPreferencesManager sharedPreferencesManager=new SharedPreferencesManager(this);
        userId=sharedPreferencesManager.getUserId();
        userRef = database.child("users").child(userId);  // Reference to the user's node

        database.child("roulette").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                percentages.clear();
                labels.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    RouletteSection rouletteSection = snapshot1.getValue(RouletteSection.class);
                    percentages.add(Integer.valueOf(rouletteSection.getPercentage()));
                    labels.add(rouletteSection.getGift());
                }
                wheelView.setLabels(labels); // Update wheel view labels
                wheelView.invalidate(); // Redraw the wheel
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        spinButton.setOnClickListener(v -> checkAndSpinWheel());
    }

    private void checkAndSpinWheel() {
        userRef.child("lastSpin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long lastSpinTime = 0;
                if (snapshot.exists()) {
                    lastSpinTime = snapshot.getValue(Long.class);
                }

                long currentTime = System.currentTimeMillis();
                long oneWeekInMillis = 7 * 24 * 60 * 60 * 1000;

                if (currentTime - lastSpinTime >= oneWeekInMillis) {
                    // Allow the spin
                    spinWheel();
                    // Update the last spin time
                    userRef.child("lastSpin").setValue(currentTime);
                } else {
                    // Calculate remaining time
                    long timeLeft = oneWeekInMillis - (currentTime - lastSpinTime);
                    String timeLeftFormatted = formatTimeLeft(timeLeft);
                    Toast.makeText(Speen.this, "You can spin again in: " + timeLeftFormatted, Toast.LENGTH_LONG).show();
                    textTime.setText("You can spin again in: " + timeLeftFormatted);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private String formatTimeLeft(long millis) {
        long days = millis / (24 * 60 * 60 * 1000);
        millis %= 24 * 60 * 60 * 1000;
        long hours = millis / (60 * 60 * 1000);
        millis %= 60 * 60 * 1000;
        long minutes = millis / (60 * 1000);
        millis %= 60 * 1000;
        long seconds = millis / 1000;

        return String.format("%d days, %d hours, %d minutes, %d seconds", days, hours, minutes, seconds);
    }

    private void spinWheel() {
        // Calculate the result based on the percentages
        int selectedIndex = getResultBasedOnPercentage();

        // Calculate the rotation angle
        float anglePerSection = 360f / percentages.size();
        float endAngle = 360 * 5 + (anglePerSection * selectedIndex) + 2*anglePerSection; // 5 full rotations plus the result section

        // Create the rotation animation
        ObjectAnimator animator = ObjectAnimator.ofFloat(wheelView, "rotation", wheelView.getRotation(), endAngle);
        animator.setDuration(5000); // 5 seconds
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // Determine the final angle and section
                float finalAngle = ((wheelView.getRotation() % 360) + 360) % 360;
                int resultIndex = (int) ((360 - finalAngle) / anglePerSection) % percentages.size();

                // Debugging output
                Log.d("Speen", "Final Angle: " + finalAngle);
                Log.d("Speen", "Result Index: " + resultIndex);

                // Show the result
                Toast.makeText(Speen.this, "You won: " + labels.get(resultIndex), Toast.LENGTH_SHORT).show();
                String lastGift=labels.get(resultIndex);
                userRef.child("lastGift").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userRef.child("lastGift").setValue(lastGift);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.start();
    }

    private int getResultBasedOnPercentage() {
        Random random = new Random();
        int randomNumber = random.nextInt(100) + 1; // Random number between 1 and 100
        int cumulativePercentage = 0;

        for (int i = 0; i < percentages.size(); i++) {
            cumulativePercentage += percentages.get(i);
            if (randomNumber <= cumulativePercentage) {
                return i;
            }
        }

        return percentages.size() - 1; // Fallback in case of rounding issues
    }
}
