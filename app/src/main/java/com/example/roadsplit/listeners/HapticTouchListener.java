package com.example.roadsplit.listeners;

import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

class HapticTouchListener implements View.OnTouchListener {
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                break;
            case MotionEvent.ACTION_UP:
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
                break;
        }
        return true;
    }
}
