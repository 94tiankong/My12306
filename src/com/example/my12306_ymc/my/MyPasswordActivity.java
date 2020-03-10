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
					edtNewPassword.setError("����������");
					edtNewPassword.requestFocus();
				}
				
				else if(TextUtils.isEmpty(edtReNewPassword.getText().toString())){
					
						edtReNewPassword.setError("���ٴ���������");
						edtReNewPassword.requestFocus();
				}
				
				
				/*else if(edtNewPassword.getText().toString().equals(edtReNewPassword.getText().toString())){
					Intent intent=new Intent();
					intent.setClass(MyPasswordActivity.this, MainActivity.class);
					startActivity(intent);
					
				}
				
				else{
					edtReNewPassword.setError("�����������벻һ�£����ٴ�����");
					edtReNewPassword.requestFocus();
				}*/
				
				else if (!(edtNewPassword.getText().toString().equals(edtReNewPassword.getText().toString()))) {
					edtNewPassword.setError("�����������벻һ�£�����������");
					edtNewPassword.requestFocus();
				}
				
				else {
					Intent intent = new Intent();
					intent.setClass(MyPasswordActivity.this, MainActivity.class);
					startActivity(intent);
					
					// 1.���ݱ��浽��������
					// 2.finish()
					if (!NetUtils.check(MyPasswordActivity.this)) {
						Toast.makeText(MyPasswordActivity.this,
								getString(R.string.network_check),
								Toast.LENGTH_SHORT).show();
						return; // �������벻ִ��
					}

					// ���ȶԻ���
					pDialog = ProgressDialog.show(MyPasswordActivity.this, null,
							"���ڱ�����...", false, true);

					// ��ʼ�߳�
					action = "update";
					new Thread(){

						public void run() {
							// ��ȡmessage
							Message msg = handler.obtainMessage();

							HttpPost post = new HttpPost(CONSTANT.HOST + "/otn/AccountPassword");

							// ��������
							DefaultHttpClient client = new DefaultHttpClient();

							try {
								// jsessionid
								SharedPreferences pref = getSharedPreferences("user",
										Context.MODE_PRIVATE);
								String value = pref.getString("Cookie", "");
								BasicHeader header = new BasicHeader("Cookie", value);
								post.setHeader(header);

								// �������
								List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
								params.add(new BasicNameValuePair("newPassword", edtReNewPassword.getText().toString()));
								params.add(new BasicNameValuePair("action", "update"));

								UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params,
										"UTF-8");
								post.setEntity(entity);

								// ��ʱ����
								client.getParams().setParameter(
										CoreConnectionPNames.CONNECTION_TIMEOUT,
										CONSTANT.REQUEST_TIMEOUT);
								client.getParams().setParameter(
										CoreConnectionPNames.SO_TIMEOUT, CONSTANT.SO_TIMEOUT);

								HttpResponse response = client.execute(post);

								// ������
								if (response.getStatusLine().getStatusCode() == 200) {

									Gson gson = new GsonBuilder().create();
									String result = gson.fromJson(
											EntityUtils.toString(response.getEntity()),
											String.class);
									// ������Ϣ
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
								msg.what = 1; // ���µ�¼
							}

							// ������Ϣ
							handler.sendMessage(msg);

						};
						
					};
				}
					
			
			}
		});
		
		
	}

	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// �رնԻ���
			if (pDialog != null) {
				pDialog.dismiss();
			}
			String result = (String) msg.obj;
			switch (msg.what) {
			case 1:
				if(result.equals("1")){
					Toast.makeText(MyPasswordActivity.this, "�޸ĳɹ�",
							Toast.LENGTH_SHORT).show();
					finish();
				}else if(result.equals("0")){
					Toast.makeText(MyPasswordActivity.this, "�޸�ʧ��",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 2:
				Toast.makeText(MyPasswordActivity.this, "����������������",
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(MyPasswordActivity.this, "�����µ�¼>_<",
						Toast.LENGTH_SHORT).show();
				break;
			}

		};
	};
	
}
