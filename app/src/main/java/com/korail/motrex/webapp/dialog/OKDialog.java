package com.korail.motrex.webapp.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.korail.motrex.webapp.R;
import com.korail.motrex.webapp.listener.IPopUpButtonEventListener;

public class OKDialog extends CustomDialog implements View.OnClickListener{

	private Context con;

//	Button btn01, btn02;

    RelativeLayout btn01;

	TextView title_txt;
	TextView time_txt;

	IPopUpButtonEventListener mIPopUpButtonEventListener;

	int a_id = 0;

	long dismis_time = 60000;

	int time_count = 60;

	public OKDialog(Context context) {
		super(context);
		con = context;
		mDialogType = DIALOG_TYPE_EDIT;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.dialog_layout_ok);

		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		

		btn01 = findViewById(R.id.btn01);

		title_txt = findViewById(R.id.title_txt);

		time_txt = findViewById(R.id.time_txt);

		btn01.setOnClickListener(this);

	}
	
	
	public void show(String title, int id ){

		String query = String.format(con.getResources().getString(R.string.popup_message), 5);
		title_txt.setText(query);

//		a_id = id;

		time_count = 60;

		time_txt.setText(""+time_count);

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
			case R.id.btn01 : {
				dismiss();
			}
				break;
		}
		dismiss();
	}

	@Override
	public void dismiss() {
		super.dismiss();

		if(mIPopUpButtonEventListener != null){
			mIPopUpButtonEventListener.buttonEvent(1);
		}

		mfinish_Handler.removeMessages(0);
	}


	private Handler mfinish_Handler = new Handler(){
		public void handleMessage(Message msg) {

			time_count --;

			if(time_count > 0){
				time_txt.setText(""+time_count);
				mfinish_Handler.sendMessageDelayed(mfinish_Handler.obtainMessage(0), 1000);
			}else {
				dismiss();
				if (mIPopUpButtonEventListener != null) {
					mIPopUpButtonEventListener.buttonEvent(-1);
				}
			}

		}
	};


	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

		}
	}

}
