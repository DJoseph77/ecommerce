package com.example.ecommerce.Helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.ecommerce.domain.RouletteSection;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomWheelView extends View {

    private Paint sectionPaint;
    private Paint textPaint;
    private ArrayList<String> labels = new ArrayList<>();

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
        invalidate(); // Request a redraw when labels are updated
    }

    public CustomWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        sectionPaint = new Paint();
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50); // Adjust text size if needed
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        remplir();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int minDimension = Math.min(width, height);
        int radius = minDimension / 2;
        float angle = 360f / labels.size();
        float angleRemoved=angle/2;

        if (labels.isEmpty()) {
            return; // Do not draw anything if there are no labels
        }

        // Draw sections
        for (int i = 0; i < labels.size(); i++) {
            sectionPaint.setColor(i % 2 == 1 ? Color.GRAY : Color.BLUE);
            canvas.drawArc(0, 0, minDimension, minDimension, i * angle, angle, true, sectionPaint);
        }

        // Draw labels on top of sections
        for (int i = 0; i < labels.size(); i++) {
            float textAngle = i * angle + angle / 2 - 90; // Adjusted to start from top
            float textX = (float) (width / 2 + (radius * 0.6) * Math.cos(Math.toRadians(textAngle-angleRemoved)));
            float textY = (float) (height / 2 + (radius * 0.6) * Math.sin(Math.toRadians(textAngle-angleRemoved)));
            canvas.drawText(labels.get(i), textX, textY, textPaint);
        }
    }

    private void remplir() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("roulette");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                labels.clear(); // Clear existing labels before adding new ones
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    RouletteSection section = snapshot1.getValue(RouletteSection.class);
                    if (section != null) {
                        labels.add(section.getGift());
                    }
                }
                setLabels(labels); // Update labels and trigger redraw
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors here
            }
        });
    }
}
