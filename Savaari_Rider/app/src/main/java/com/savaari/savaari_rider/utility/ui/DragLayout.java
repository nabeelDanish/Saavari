package com.savaari.savaari_rider.utility.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.customview.widget.ViewDragHelper;

public class DragLayout extends LinearLayout {

    private final ViewDragHelper mDragHelper = null;
    private View mDragView;

    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
    }
}
