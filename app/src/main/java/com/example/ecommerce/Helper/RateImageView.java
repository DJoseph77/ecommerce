package com.example.ecommerce.Helper;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class RateImageView extends androidx.appcompat.widget.AppCompatImageView {

    public RateImageView(Context context) {
        super(context);
    }

    public RateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RateImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        super.performClick(); // Ensure the default behavior is preserved
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Handle touch events
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Optional: Change appearance or behavior on touch down
                return true; // Indicate that the touch event was handled
            case MotionEvent.ACTION_UP:
                // Perform the click action
                performClick();
                return true; // Indicate that the touch event was handled
        }
        return super.onTouchEvent(event);
    }

}
