package com.example.my12306_ymc.my;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.example.my12306_ymc.my.MyPasswordActivity;
import com.example.my12306_ymc.utils.CONSTANT;
import com.example.my12306_ymc.utils.NetUtils;
import com.example.my12306_ymc.MainActivity;
import com.example.my12306_ymc.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MyPasswordActivity extends Activity{
	
	EditText edtNewPassword = null;
	EditText edtReNewPassword = null;
	Button btnSavePassword = null;
	ProgressDialog pDialog = null;
	String action = "";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_password);
		
		edtNewPassword=(EditText)findViewById(R.id.edtNewPassword);
		edtReNewPassword=(EditText)findViewById(R.id.edtReNewPassword);
		btnSavePassword=(Button)findViewById(R.id.btnSavePassword);
		
		//final String text1=edtNewPassword.getText().toString();
		//final String text2=edtReNewPassword.getText().toString();
		
		btnSavePassword.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(TextUtils.isEmpty(edtNewPassword.getText().toString())){
					edtNewPassword.setError("请输入密码");
					edtNewPassword.requestFocus();
				}
				
				else if(TextUtils.isEmpty(edtReNewPassword.getText().toString())){
					
						edtReNewPassword.setError("请再次输入密码");
						edtReNewPassword.requestFocus();
				}
				
				
				/*else if(edtNewPassword.getText().toString().equals(edtReNewPassword.getText().toString())){
					Intent intent=new Intent();
					intent.setClass(MyPasswordActivity.this, MainActivity.class);
					startActivity(intent);
					
				}
				
				else{
					edtReNewPassword.setError("两次密码输入不一致，请再次输入");
					edtReNewPassword.requestFocus();
				}*/
				
				else if (!(edtNewPassword.getText().toString().equals(edtReNewPassword.getText().toString()))) {
					edtNewPassword.setError("两次密码输入不一致，请重新输入");
					edtNewPassword.requestFocus();
				}
				
				else {
					Intent intent = new Intent();
					intent.setClass(MyPasswordActivity.this, MainActivity.class);
					startActivity(intent);
					
					// 1.数据保存到服务器上
					// 2.finish()
					if (!NetUtils.check(MyPasswordActivity.this)) {
						Toast.makeText(MyPasswordActivity.this,
								getString(R.string.network_check),
								Toast.LENGTH_SHORT).show();
						return; // 后续代码不执行
					}

					// 进度对话框
					pDialog = ProgressDialog.show(MyPasswordActivity.this, null,
							"正在保存中...", false, true);

					// 开始线程
					action = "update";
					new Thread(){

						public void run() {
							// 获取message
							Message msg = handler.obtainMessage();

							HttpPost post = new HttpPost(CONSTANT.HOST + "/otn/AccountPassword");

							// 发送请求
							DefaultHttpClient client = new DefaultHttpClient();

							try {
								// jsessionid
								SharedPreferences pref = getSharedPreferences("user",
										Context.MODE_PRIVATE);
								String value = pref.getString("Cookie", "");
								BasicHeader header = new BasicHeader("Cookie", value);
								post.setHeader(header);

								// 请求参数
								List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
								params.add(new BasicNameValuePair("newPassword", edtReNewPassword.getText().toString()));
								params.add(new BasicNameValuePair("action", "update"));

								UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params,
										"UTF-8");
								post.setEntity(entity);

								// 超时设置
								client.getParams().setParameter(
										CoreConnectionPNames.CONNECTION_TIMEOUT,
										CONSTANT.REQUEST_TIMEOUT);
								client.getParams().setParameter(
										CoreConnectionPNames.SO_TIMEOUT, CONSTANT.SO_TIMEOUT);

								HttpResponse response = client.execute(post);

								// 处理结果
								if (response.getStatusLine().getStatusCode() == 200) {

									Gson gson = new GsonBuilder().create();
									String result = gson.fromJson(
											EntityUtils.toString(response.getEntity()),
											String.class);
									// 发送消息
									msg.obj = result;
									msg.what = 1;

								} else {
									msg.what = 2;
								}

								client.getConnectionManager().shutdown();

							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								msg.what = 2;
							} catch (ClientProtocolException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								msg.what = 2;
							} catch (IOException e) {
								e.printStackTrace();
								msg.what = 2;
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								msg.what = 2;
							} catch (JsonSyntaxException e) {
								e.printStackTrace();
								msg.what = 1; // 重新登录
							}

							// 发送消息
							handler.sendMessage(msg);

						};
						
					};
				}
					
			
			}
		});
		
		
	}

	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 关闭对话框
			if (pDialog != null) {
				pDialog.dismiss();
			}
			String result = (String) msg.obj;
			switch (msg.what) {
			case 1:
				if(result.equals("1")){
					Toast.makeText(MyPasswordActivity.this, "修改成功",
							Toast.LENGTH_SHORT).show();
					finish();
				}else if(result.equals("0")){
					Toast.makeText(MyPasswordActivity.this, "修改失败",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 2:
				Toast.makeText(MyPasswordActivity.this, "服务器错误，请重试",
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(MyPasswordActivity.this, "请重新登录>_<",
						Toast.LENGTH_SHORT).show();
				break;
			}

		};
	};
	
}
