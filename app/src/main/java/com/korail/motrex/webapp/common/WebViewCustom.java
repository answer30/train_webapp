package com.korail.motrex.webapp.common;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

import com.korail.motrex.webapp.listener.ITouchEventListener;

import java.util.List;

import static android.content.ContentValues.TAG;

public class WebViewCustom extends WebView {


    ITouchEventListener touchEventListener;

    public WebViewCustom(Context context) {
        super(context);
    }

    public WebViewCustom(Context context, AttributeSet att){
        super(context, att);
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);

    }

    @Override
    public void scrollBy(int x, int y) {
        super.scrollBy(x, y);

    }

    @Override
    public void invokeZoomPicker() {
        super.invokeZoomPicker();

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.d(TAG, "onTouch: " +   event.getAction());
        if(touchEventListener != null){
            touchEventListener.TouchEvent(event.getAction());
        }

        return super.onTouchEvent(event);
    }


    public void setTouchListener(ITouchEventListener listener){
        touchEventListener = listener;
    }



}
