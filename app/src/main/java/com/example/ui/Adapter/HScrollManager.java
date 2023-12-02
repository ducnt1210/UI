package com.example.ui.Adapter;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class HScrollManager extends LinearLayoutManager {
    private boolean scrollingEnabled = true;

    public HScrollManager(Context context) {
        super(context);
    }

    public boolean getScrollingEnabled() {
        return scrollingEnabled;
    }
    public void setScrollingEnabled(boolean enabled) {
        scrollingEnabled = enabled;
    }

    @Override
    public boolean canScrollHorizontally() {
        return scrollingEnabled && super.canScrollHorizontally();
    }
}
