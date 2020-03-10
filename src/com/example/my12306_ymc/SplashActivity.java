package com.example.my12306_ymc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import com.example.my12306_ymc.LoginActivity;


public class SplashActivity extends Activity{

	Handler pause = new Handler(){
		public void handleMessage(android.os.Message msg){
			switch (msg.what) {
			case 1:
				//自动登录
				SharedPreferences pref = getSharedPreferences("user", 0);
				String username = pref.getString("username", "");
				String password = pref.getString("password", "");
				
				if(TextUtils.isEmpty(username)||TextUtils.isEmpty(password)){
					Intent intent = new Intent();
					intent.setClass(SplashActivity.this, LoginActivity.class);
					startActivity(intent);
				}else{
					Intent intent = new Intent();
					intent.setClass(SplashActivity.this, MainActivity.class);
					startActivity(intent);
				}
				finish();
					
				break;

			default:
				break;
			}
		}
	};
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		// 设置全屏模式
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 去除标题行
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_splash);
		
		
		pause.sendEmptyMessageDelayed(1, 1000);
		
		
	}
	

	

}
