package com.example.my12306_ymc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.content.SharedPreferences;


import com.example.my12306_ymc.my.MyAccountActivity;
import com.example.my12306_ymc.my.MyContactActivity;
import com.example.my12306_ymc.my.MyContactEditActivity;
import com.example.my12306_ymc.my.MyPasswordActivity;
import com.example.my12306_ymc.utils.CONSTANT;
import com.example.my12306_ymc.utils.Md5Utils;
import com.example.my12306_ymc.utils.NetUtils;
import com.example.my12306_ymc.LoginActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyFragment extends android.support.v4.app.Fragment{
	
	Button btnLogout = null;
	ListView lvMyList = null;
	ProgressDialog pDialog = null;
	String action="query";
	//////////？？？？？？？？？？？？？？？
	public MyFragment(){
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_my, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		btnLogout=(Button)getActivity().findViewById(R.id.btnLogout);
		lvMyList=(ListView)getActivity().findViewById(R.id.lvMyList);
		
        String []data = getResources().getStringArray(R.array.my_list_data);
		
		//shipeiqi
		ArrayAdapter<String> adapter = new ArrayAdapter<String>
		(getActivity(), android.R.layout.simple_list_item_1,data );
		
		//bangding
		lvMyList.setAdapter(adapter);
		
		lvMyList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();

				switch (position) {
				case 0:
					intent.setClass(getActivity(), MyContactActivity.class);
					startActivity(intent);
					break;
				case 1:
					intent.setClass(getActivity(), MyAccountActivity.class);
					startActivity(intent);
					break;

				case 2:

					final EditText edtPassword = new EditText(getActivity());

					edtPassword.selectAll(); // 默认选中

					new AlertDialog.Builder(getActivity())
							.setIcon(android.R.drawable.ic_dialog_info)
							.setTitle("请输入密码")
							.setView(edtPassword)
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											// 验证
											String name = edtPassword.getText()
													.toString();
											if (TextUtils.isEmpty(name)) {
												// 设置对话框不能自动关闭
												setClosable(dialog, false);

												edtPassword.setError("请输入密码");
												edtPassword.requestFocus();

											} 
											
											else {
												
												if (!NetUtils.check(getActivity())) {
													Toast.makeText(getActivity(),
															getString(R.string.network_check),
															Toast.LENGTH_SHORT).show();
													return; // 后续代码不执行
												}
												
												pDialog = ProgressDialog.show(getActivity(), null,
														"正在加载中...", false, true);
												
												new Thread(){
													public void run(){
														
														
																Message msg = handler.obtainMessage();

																// 访问服务器端，验证用户名/密码
																HttpPost post = new HttpPost(CONSTANT.HOST
																		+ "/otn/AccountPassword");
																

																UrlEncodedFormEntity entity;
																try {
																	
																	// jsessionid
																	SharedPreferences pref = getActivity().getSharedPreferences("user",
																			Context.MODE_PRIVATE);
																	String value = pref.getString("Cookie", "");
																	BasicHeader header = new BasicHeader("Cookie", value);
																	post.setHeader(header);
																	
																	
																	// 设置参数
																	List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
																	params.add(new BasicNameValuePair("oldPassword",
																			edtPassword.getText().toString()));
																	params.add(new BasicNameValuePair("action", "query"));
																	
																	entity = new UrlEncodedFormEntity(params,
																			"UTF-8");

																	post.setEntity(entity);
																	
																	// 发送请求
																	DefaultHttpClient client = new DefaultHttpClient();
																	// 超时设置
																	client.getParams()
																			.setParameter(
																					CoreConnectionPNames.CONNECTION_TIMEOUT,
																					CONSTANT.REQUEST_TIMEOUT);
																	client.getParams().setParameter(
																			CoreConnectionPNames.SO_TIMEOUT,
																			CONSTANT.SO_TIMEOUT);
																	HttpResponse response = client.execute(post);
																	
																	// 处理结果
																	if (response.getStatusLine().getStatusCode() == 200) {
																		// 打印响应的结果
																		// Log.v("My12306",
																		// EntityUtils.toString(response.getEntity()));

																		// xml解析
																		Gson gson = new GsonBuilder().create();
																		String result = gson.fromJson(
																				EntityUtils.toString(response.getEntity()),
																				String.class);
																		// 发送消息
																		msg.obj = result;
																		
																		
																		// 发送消息
																		msg.what = 1;
																		
																		
																	} else {
																		msg.what = 2;
																	}

																	// 关闭连接
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
																	// TODO Auto-generated catch block
																	e.printStackTrace();
																	msg.what = 2;
																} catch (IllegalStateException e) {
																	// TODO Auto-generated catch block
																	e.printStackTrace();
																	msg.what = 2;
																} catch (JsonSyntaxException e) {
																	// TODO Auto-generated catch block
																	e.printStackTrace();
																	msg.what = 2;
																}

																handler.sendMessage(msg);
															
														
													}
////							///////////////////////////////////
												//	private SharedPreferences getSharedPreferences(
												//			String string,
												//			int modePrivate) {
														// TODO Auto-generated method stub
												//		return null;
												//	}
												///////////////////////////////////////////	
												}.start();//Thread
												
												
											}//////////else的
											/*else {
												// 设置对话框自动关闭
												Intent intent1 = new Intent();
												intent1.setClass(
														getActivity(),
														MyPasswordActivity.class);
												startActivity(intent1);
												setClosable(dialog, true);
											}*/

										}

									})
							.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											// 设置对话框自动关闭
											setClosable(dialog, true);

										}

									}).show();
					break;
				}
				
				
			}
		});
		
		MyButtonListener listener = new MyButtonListener();
		btnLogout.setOnClickListener(listener);
	}

	
	class MyButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// 区分按钮
			Intent intent3 = new Intent();
			intent3.setClass(getActivity(), LoginActivity.class);
			startActivity(intent3);
			getActivity().finish();

		}
		
	

}
	
	
//AsyncTask

	class LogoutTask extends AsyncTask<String, String, String>
	{

	@Override
		protected String doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		
		String result = null;

		HttpPost post = new HttpPost(CONSTANT.HOST + "/otn/Logout");
		
		DefaultHttpClient client = new DefaultHttpClient();
		
		// 设置jsessionid
		SharedPreferences pref = getActivity().getSharedPreferences(
				"user", Context.MODE_PRIVATE);
		String value = pref.getString("Cookie", "");
		BasicHeader header = new BasicHeader("Cookie", value);
		post.setHeader(header);
		
		// 超时设置
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT,
				CONSTANT.REQUEST_TIMEOUT);
		client.getParams().setParameter(
				CoreConnectionPNames.SO_TIMEOUT, CONSTANT.SO_TIMEOUT);

		
		HttpResponse response = null;
		try {
			response = client.execute(post);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 处理结果
		if (response.getStatusLine().getStatusCode() == 200) {
			try {
				result = EntityUtils.toString(response.getEntity());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("My12306", "Logout:" + result);
		}

		client.getConnectionManager().shutdown();
		
		return result;
		
	}

	@Override
		protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		if (pDialog != null)
			pDialog.dismiss();
		
		// "1"
		if ("\"1\"".equals(result)) {
			Toast.makeText(getActivity(), "退出成功", Toast.LENGTH_SHORT)
					.show();

			Intent intent = new Intent();
			intent.setClass(getActivity(), LoginActivity.class);
			startActivity(intent);
			getActivity().finish();
		} else if ("\"0\"".equals(result)) {
			Toast.makeText(getActivity(), "退出登录失败", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(getActivity(), "服务器错误，请重试", Toast.LENGTH_SHORT)
					.show();
			Intent intent = new Intent();
			intent.setClass(getActivity(), LoginActivity.class);
			startActivity(intent);
			getActivity().finish();
		}
	}

	@Override
		protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		
		pDialog = ProgressDialog.show(getActivity(), null, "正在加载中...",
				false, true);
		}

	
		
	
	
	}
	
	

	
	
	
	
		private void setClosable(DialogInterface dialog, boolean b) {
				Field field;
				try {
					field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, b);
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}


		
		//////////////////Handler
		Handler handler = new Handler()
		{
			
			public void handleMessage(android.os.Message msg) {
				// 关闭对话框
				if (pDialog != null) {
					pDialog.dismiss();
				}

				switch (msg.what) {
				case 1:
					String result = (String) msg.obj;

					if ("0".equals(result)) {
						
						Toast.makeText(getActivity(), "密码错误",
								Toast.LENGTH_SHORT).show();
						
					} else if ("1".equals(result)) {
						

						/*// 显示意图
						Intent intent = new Intent(getActivity(),
								MainActivity.class);
						startActivity(intent);

						// 关闭
						getActivity().finish();*/
						
						Intent intent1 = new Intent();
						intent1.setClass(
								getActivity(),
								MyPasswordActivity.class);
						startActivity(intent1);
						
					}

					break;
				case 2:
					Toast.makeText(getActivity(), "服务器错误，请重试!",
							Toast.LENGTH_SHORT).show();
					break;
				}
			};
			
		};
		

}
