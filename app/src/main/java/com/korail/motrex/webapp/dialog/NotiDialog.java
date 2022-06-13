package com.korail.motrex.webapp.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.korail.motrex.webapp.R;
import com.korail.motrex.webapp.listener.IPopUpButtonEventListener;

public class NotiDialog extends CustomDialog implements View.OnClickListener{

	private Context con;

//	Button btn01, btn02;

    RelativeLayout noti_layout;

	IPopUpButtonEventListener mIPopUpButtonEventListener;

	TextView noti_message, noti_title;

	int time_count = 2;

	public NotiDialog(Context context) {
		super(context);
		con = context;
		mDialogType = DIALOG_TYPE_EDIT;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.dialog_layout_noti);

		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		

		noti_layout = findViewById(R.id.noti_layout);
		noti_layout.setOnClickListener(this);

		noti_message = findViewById(R.id.noti_message);
		noti_title = findViewById(R.id.noti_title);
	}
	
	
	public void show(String title, String message ){

		noti_title.setSelected(true);
		noti_message.setText(message);

		time_count = 3;


		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

		show();

		mfinish_Handler.removeMessages(0);
		mfinish_Handler.sendMessageDelayed(mfinish_Handler.obtainMessage(0), 1000);


	}


	public void setButtonListener(IPopUpButtonEventListener listener){
		mIPopUpButtonEventListener = listener;
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch(id){
			case R.id.noti_layout : {
				dismiss();
			}
				break;
		}
		dismiss();
	}

	private Handler mfinish_Handler = new Handler(){
		public void handleMessage(Message msg) {
			time_count --;

			if(time_count > 0){
				mfinish_Handler.sendMessageDelayed(mfinish_Handler.obtainMessage(0), 1000);
			}else {
				dismiss();
			}

		}
	};

	@Override
	public void dismiss() {
		super.dismiss();

		if(mIPopUpButtonEventListener != null){
			mIPopUpButtonEventListener.buttonEvent(1);
		}

		mfinish_Handler.removeMessages(0);
	}


	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
//		if (hasFocus) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


//		}
	}
}
