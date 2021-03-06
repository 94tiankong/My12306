package com.example.my12306_ymc.my;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.example.my12306_ymc.R;
import com.example.my12306_ymc.R.id;
import com.example.my12306_ymc.my.MyContactEditActivity;
import com.example.my12306_ymc.utils.NetUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.example.my12306_ymc.utils.CONSTANT;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.drm.DrmStore.Action;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MyContactEditActivity extends Activity{

	ListView lvMyContactEdit=null;
	Button btnMyContactEditSave=null;
	ProgressDialog pDialog;
	String action="";
	List<Map<String, Object>> data = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_contact_edit);
		
		lvMyContactEdit=(ListView)findViewById(R.id.lvMyContactEdit);
		btnMyContactEditSave=(Button)findViewById(R.id.btnMyContactEditSave);
		
		//接收从MyAccountActivity来的数据
		Intent intent = getIntent();
		Map<String, Object> contact = (Map<String, Object>)intent.getSerializableExtra("row");
		
		data = new ArrayList<Map<String, Object>>();
		
		//row1
		Map<String, Object> row1 = new HashMap<String, Object>();
		String name = (String)contact.get("name");
		row1.put("key1", "姓名");
		row1.put("key2", name.split("\\(")[0]);
		row1.put("key3", R.drawable.forward_25);
		data.add(row1);
		
		//row2
		Map<String, Object> row2 = new HashMap<String, Object>();
		String idCard = (String) contact.get("idCard");
		row2.put("key1", "证件类型");
		row2.put("key2", idCard.split(":")[0]);
		row2.put("key3", null);
		data.add(row2);
		
		//row3
		Map<String, Object> row3 = new HashMap<String, Object>();
		row3.put("key1", "证件号码");
		row3.put("key2", idCard.split(":")[1].split("\\)")[0]);
		row3.put("key3", null);
		data.add(row3);
		
		//row4
		Map<String, Object> row4 = new HashMap<String, Object>();
		row4.put("key1", "乘客类型");
		row4.put("key2", name.split("\\(")[1].split("\\)")[0]);
		row4.put("key3", R.drawable.forward_25);
		data.add(row4);
		
		//row5
		Map<String, Object> row5 = new HashMap<String, Object>();
		String tel = (String) contact.get("tel");
		row5.put("key1", "电话");
		row5.put("key2", tel.split(":")[1]);
		row5.put("key3", R.drawable.forward_25);
		data.add(row5);
	
		
		
		final SimpleAdapter adapter = new SimpleAdapter(MyContactEditActivity.this, data, 
				R.layout.item_my_contact_edit, new String[]{"key1","key2","key3"}, new int[]{
				R.id.tvMyContactEditKey,R.id.tvMyContactEditValue, R.id.imgMyContactEditFlg});
		
		lvMyContactEdit.setAdapter(adapter);
		
		
		lvMyContactEdit.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position,
					long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					
					final EditText edtName = new EditText(
							MyContactEditActivity.this);
					edtName.setText((String) (data.get(position).get("key2")));
					edtName.selectAll(); // 默认选中

					new AlertDialog.Builder(MyContactEditActivity.this)
							.setIcon(android.R.drawable.ic_dialog_info)
							.setTitle("请输入姓名")
							.setView(edtName)
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											// 验证
											String name = edtName.getText()
													.toString();
											if (TextUtils.isEmpty(name)) {
												// 设置对话框不能自动关闭
												setClosable(dialog, false);

												edtName.setError("请输入姓名");
												edtName.requestFocus();

											} else {
												// 设置对话框自动关闭
												setClosable(dialog, true);

												data.get(position).put(
														"key2",
														edtName.getText()
																.toString());
												// 更新ListView
												adapter.notifyDataSetChanged();
											}
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
				
				case 1:
					
					break;
					
				case 2:
					
					break;
					
				case 3:
					
					final String[] types = new String[] { "成人", "学生", "儿童",
					"其他" };
			String key2 = (String) (data.get(position).get("key2"));
			int idx = 0;
			for (int i = 0; i < types.length; i++) {
				if (types[i].equals(key2)) {
					idx = i;
					break;
				}
			}

			new AlertDialog.Builder(MyContactEditActivity.this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("请选择乘客类型")
					.setSingleChoiceItems(types, idx,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									data.get(position).put("key2",
											types[which]);
									adapter.notifyDataSetChanged();

									dialog.dismiss();
								}
							}).setNegativeButton("取消", null).show();

			break;
					
				case 4:
					
					final EditText edtName2 = new EditText(
							MyContactEditActivity.this);
					edtName2.setText((String) (data.get(position).get("key2")));
					edtName2.selectAll(); // 默认选中

					new AlertDialog.Builder(MyContactEditActivity.this)
							.setIcon(android.R.drawable.ic_dialog_info)
							.setTitle("请输入电话")
							.setView(edtName2)
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											// 验证
											String name = edtName2.getText()
													.toString();
											if (TextUtils.isEmpty(name)) {
												// 设置对话框不能自动关闭
												setClosable(dialog, false);

												edtName2.setError("请输入电话号码");
												edtName2.requestFocus();

											} else {
												// 设置对话框自动关闭
												setClosable(dialog, true);

												data.get(position).put(
														"key2",
														edtName2.getText()
																.toString());
												// 更新ListView
												adapter.notifyDataSetChanged();
											}
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
					

				default:
					break;
				}
				
			}
			
			
			
		});
		
		
		btnMyContactEditSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//判断网络状态
				if (!NetUtils.check(MyContactEditActivity.this)) {
					Toast.makeText(MyContactEditActivity.this,
							getString(R.string.network_check),
							Toast.LENGTH_SHORT).show();
					return; // 后续代码不执行
				}
				
				// 进度对话框
				pDialog = ProgressDialog.show(MyContactEditActivity.this, null,
						"正在加载中...", false, true);
				
				
				action="update";
				contactThread.start();
				
				
				
			}
		});
		
		
		
		
		
	}
	
	
	
	Thread contactThread = new Thread() {
		public void run() {
			// 获取message
			Message msg = handler.obtainMessage();

			HttpPost post = new HttpPost(CONSTANT.HOST + "/otn/Passenger");

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
				params.add(new BasicNameValuePair("姓名", (String) data.get(0)
						.get("key2")));
				params.add(new BasicNameValuePair("证件类型", (String) data.get(1)
						.get("key2")));
				params.add(new BasicNameValuePair("证件号码", (String) data.get(2)
						.get("key2")));
				params.add(new BasicNameValuePair("乘客类型", (String) data.get(3)
						.get("key2")));
				params.add(new BasicNameValuePair("电话", (String) data.get(4)
						.get("key2")));
				params.add(new BasicNameValuePair("action", action));

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
				msg.what = 3; // 重新登录
			}

			// 发送消息
			handler.sendMessage(msg);

		};
	};
	
	
	Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (pDialog != null) {
				pDialog.dismiss();
			}
			
			switch (msg.what) {
			case 1:
				String result = (String) msg.obj;
				String info = "修改";
				if ("remove".equals(action)) {
					info = "删除";
				}

				if ("1".equals(result)) {

					Toast.makeText(MyContactEditActivity.this, info + "成功",
							Toast.LENGTH_SHORT).show();
					finish();
				} else if ("-1".equals(result)) {
					Toast.makeText(MyContactEditActivity.this, info + "失败，请重试",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 2:
				Toast.makeText(MyContactEditActivity.this, "服务器错误，请重试",
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(MyContactEditActivity.this, "请重新登录",
						Toast.LENGTH_SHORT).show();
				break;
			}
			
		}
	};
	
	//关闭对话框
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


	
	//删除联系人action_bar
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.my_contact_edit, menu);
		return true;
	}


	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.mn_removeuser_contact:
			action="remove";
			contactThread.start();
			break;

		default:
			break;
		}
		
		return super.onMenuItemSelected(featureId, item);
	}
	
	
	
	
	
	
	

	
}
