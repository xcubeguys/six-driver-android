package com.tommy.driver.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.tommy.driver.Map_Activity;
import com.tommy.driver.R;

public class WidgetService extends Service  {

	private WindowManager windowManager;
	private ImageView widgetHead;
	WindowManager.LayoutParams params;

	private GestureDetector gestureDetector;

	@Override
	public void onCreate() {
		super.onCreate();

		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        gestureDetector = new GestureDetector(this, new SingleTapConfirm());
		widgetHead = new ImageView(this);

		widgetHead.setImageResource(R.mipmap.ic_widget_button);

		params= new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 100;

		//this code is for dragging the chat head
		widgetHead.setOnTouchListener(new View.OnTouchListener() {
			private int initialX;
			private int initialY;
			private float initialTouchX;
			private float initialTouchY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (gestureDetector.onTouchEvent(event)) {
					// single tap
					stopService(new Intent(WidgetService.this, WidgetService.class));
					//Toast.makeText(WidgetService.this, "am touched.....", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(WidgetService.this, Map_Activity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					return true;
				} else {
					// your code for move and drag
					switch (event.getAction()) {

						case MotionEvent.ACTION_DOWN:
							initialX = params.x;
							initialY = params.y;
							initialTouchX = event.getRawX();
							initialTouchY = event.getRawY();
							return true;
						case MotionEvent.ACTION_UP:
							return true;

						case MotionEvent.ACTION_MOVE:
							params.x = initialX
									+ (int) (event.getRawX() - initialTouchX);
							params.y = initialY
									+ (int) (event.getRawY() - initialTouchY);
							windowManager.updateViewLayout(widgetHead, params);
							return true;
					}
				}

				return false;
			}
		});

		windowManager.addView(widgetHead, params);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (widgetHead != null)
			windowManager.removeView(widgetHead);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }
}