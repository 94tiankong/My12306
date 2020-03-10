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
import com.example.my12306_ymc.utils.CONSTANT;
import com.example.my12306_ymc.utils.NetUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
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

public class MyContactNewActivity extends Activity{

	ListView lvContactNew=null;
	Button btnContactNewSave=null;
	ProgressDialog pDialog;
	String action="";
	List<Map<String, Object>> data = null;
	SimpleAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_contact_new);
		
		lvContactNew=(ListView)findViewById(R.id.lvContactNew);
		btnContactNewSave=(Button)findViewById(R.id.btnContactNewSave);
		
		data = new ArrayList<Map<String, Object>>();
		
		//row1
		Map<String, Object> row1 = new HashMap<String, Object>();
		
		row1.put("key1", "����");
		row1.put("key2", "");
		row1.put("key3", R.drawable.forward_25);
		data.add(row1);
		
		//row2
		Map<String, Object> row2 = new HashMap<String, Object>();
				
		row2.put("key1", "֤������");
		row2.put("key2", "");
		row2.put("key3", R.drawable.forward_25);
		data.add(row2);
				
		//row3
		Map<String, Object> row3 = new HashMap<String, Object>();
		row3.put("key1", "֤������");
		row3.put("key2", "");
		row3.put("key3", R.drawable.forward_25);
		data.add(row3);
				
		//row4
		Map<String, Object> row4 = new HashMap<String, Object>();
		row4.put("key1", "�˿�����");
		row4.put("key2", "");
		row4.put("key3", R.drawable.forward_25);
		data.add(row4);
				
		//row5
		Map<String, Object> row5 = new HashMap<String, Object>();
		
		row5.put("key1", "�绰");
		row5.put("key2", "");
		row5.put("key3", R.drawable.forward_25);
		data.add(row5);
		
		
		 adapter = new SimpleAdapter(MyContactNewActivity.this, data, 
				R.layout.item_my_contact_edit, new String[]{"key1","key2","key3"}, new int[]{
				R.id.tvMyContactEditKey,R.id.tvMyContactEditValue, R.id.imgMyContactEditFlg});
		
		lvContactNew.setAdapter(adapter);
		
		
		lvContactNew.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position,
					long id) {
				// TODO Auto-generated method stub
				
				switch (position) {
				case 0:
					
					final EditText edtName = new EditText(
							MyContactNewActivity.this);
					edtName.setText((String) (data.get(position).get("key2")));
					edtName.selectAll(); // Ĭ��ѡ��

					new AlertDialog.Builder(MyContactNewActivity.this)
							.setIcon(android.R.drawable.ic_dialog_info)
							.setTitle("����������")
							.setView(edtName)
							.setPositiveButton("ȷ��",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											// ��֤
											String name = edtName.getText()
													.toString();
											if (TextUtils.isEmpty(name)) {
												// ���öԻ������Զ��ر�
												setClosable(dialog, false);

												edtName.setError("����������");
												edtName.requestFocus();

											} else {
												// ���öԻ����Զ��ر�
												setClosable(dialog, true);

												data.get(position).put(
														"key2",
														edtName.getText()
																.toString());
												// ����ListView
												adapter.notifyDataSetChanged();
											}
										}

										
									})
							.setNegativeButton("ȡ��",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											// ���öԻ����Զ��ر�
											setClosable(dialog, true);

										}

										
									}).show();
					
					break;
				
				case 1:
					
					final String[] types = new String[] { "���֤", "����", "ѧ��֤",
					"����" };
			String key2 = (String) (data.get(position).get("key2"));
			int idx = 0;
			for (int i = 0; i < types.length; i++) {
				if (types[i].equals(key2)) {
					idx = i;
					break;
				}
			}

			new AlertDialog.Builder(MyContactNewActivity.this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("��ѡ��֤������")
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
							}).setNegativeButton("ȡ��", null).show();
					
					break;
					
				case 2:
					
					final EditText edtName2 = new EditText(
							MyContactNewActivity.this);
					edtName2.setText((String) (data.get(position).get("key2")));
					edtName2.selectAll(); // Ĭ��ѡ��

					new AlertDialog.Builder(MyContactNewActivity.this)
							.setIcon(android.R.drawable.ic_dialog_info)
							.setTitle("������֤������")
							.setView(edtName2)
							.setPositiveButton("ȷ��",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											// ��֤
											String name = edtName2.getText()
													.toString();
											if (TextUtils.isEmpty(name)) {
												// ���öԻ������Զ��ر�
												setClosable(dialog, false);

												edtName2.setError("����������");
												edtName2.requestFocus();

											} else {
												// ���öԻ����Զ��ر�
												setClosable(dialog, true);

												data.get(position).put(
														"key2",
														edtName2.getText()
																.toString());
												// ����ListView
												adapter.notifyDataSetChanged();
											}
										}

										
									})
							.setNegativeButton("ȡ��",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											// ���öԻ����Զ��ر�
											setClosable(dialog, true);

										}

										
									}).show();
					
					break;
					
				case 3:
					
					final String[] types2 = new String[] { "����", "ѧ��", "��ͯ",
					"����" };
			String key22 = (String) (data.get(position).get("key2"));
			int idx2 = 0;
			for (int i = 0; i < types2.length; i++) {
				if (types2[i].equals(key22)) {
					idx2 = i;
					break;
				}
			}

			new AlertDialog.Builder(MyContactNewActivity.this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("��ѡ��˿�����")
					.setSingleChoiceItems(types2, idx2,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									data.get(position).put("key2",
											types2[which]);
									adapter.notifyDataSetChanged();

									dialog.dismiss();
								}
							}).setNegativeButton("ȡ��", null).show();

			break;
					
				case 4:
					
					final EditText edtName3 = new EditText(
							MyContactNewActivity.this);
					edtName3.setText((String) (data.get(position).get("key2")));
					edtName3.selectAll(); // Ĭ��ѡ��

					new AlertDialog.Builder(MyContactNewActivity.this)
							.setIcon(android.R.drawable.ic_dialog_info)
							.setTitle("������绰")
							.setView(edtName3)
							.setPositiveButton("ȷ��",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											// ��֤
											String name = edtName3.getText()
													.toString();
											if (TextUtils.isEmpty(name)) {
												// ���öԻ������Զ��ر�
												setClosable(dialog, false);

												edtName3.setError("������绰����");
												edtName3.requestFocus();

											} else {
												// ���öԻ����Զ��ر�
												setClosable(dialog, true);

												data.get(position).put(
														"key2",
														edtName3.getText()
																.toString());
												// ����ListView
												adapter.notifyDataSetChanged();
											}
										}

										
									})
							.setNegativeButton("ȡ��",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											// ���öԻ����Զ��ر�
											setClosable(dialog, true);

										}

										
									}).show();
					
					break;
					

				default:
					break;
				}
				
			}
		});
		
		
		
		btnContactNewSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!NetUtils.check(MyContactNewActivity.this)) {
					Toast.makeText(MyContactNewActivity.this,
							getString(R.string.network_check),
							Toast.LENGTH_SHORT).show();
					return; // �������벻ִ��
				}
				
				// ���ȶԻ���
				pDialog = ProgressDialog.show(MyContactNewActivity.this, null,
						"���ڼ�����...", false, true);
				
				
				action="new";
				contactThread.start();
			}
		});
		
	
	
	}

	
	
	
	
	
	
	//�رնԻ���
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


		
		
		
		
		
		Thread contactThread = new Thread() {
			public void run() {
				// ��ȡmessage
				Message msg = handler.obtainMessage();

				HttpPost post = new HttpPost(CONSTANT.HOST + "/otn/Passenger");

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
					params.add(new BasicNameValuePair("����", (String) data.get(0)
							.get("key2")));
					params.add(new BasicNameValuePair("֤������", (String) data.get(1)
							.get("key2")));
					params.add(new BasicNameValuePair("֤������", (String) data.get(2)
							.get("key2")));
					params.add(new BasicNameValuePair("�˿�����", (String) data.get(3)
							.get("key2")));
					params.add(new BasicNameValuePair("�绰", (String) data.get(4)
							.get("key2")));
					params.add(new BasicNameValuePair("action", action));

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
					msg.what = 3; // ���µ�¼
				}

				// ������Ϣ
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
					
					if ("1".equals(result)) {

						Toast.makeText(MyContactNewActivity.this,"�����ϵ�˳ɹ�",
								Toast.LENGTH_SHORT).show();
						finish();
					} else if ("-1".equals(result)) {
						Toast.makeText(MyContactNewActivity.this, "�����ϵ��ʧ�ܣ�������",
								Toast.LENGTH_SHORT).show();
					}
					break;
				case 2:
					Toast.makeText(MyContactNewActivity.this, "����������������",
							Toast.LENGTH_SHORT).show();
					break;
				case 3:
					Toast.makeText(MyContactNewActivity.this, "�����µ�¼",
							Toast.LENGTH_SHORT).show();
					break;
				}
				
			}
		};
		
		
		
		
		
		////////////������ϵ��
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.my_contact_new, menu);
			return true;
		}

		@Override
		public boolean onMenuItemSelected(int featureId, MenuItem item) {
			// TODO Auto-generated method stub
			switch (item.getItemId()) {
			case R.id.mn_finduser_contact:

				ContentResolver cr = getContentResolver();
				Cursor c = cr.query(ContactsContract.Contacts.CONTENT_URI,
						new String[] { "_id", "display_name" }, null, null, null);

				List<String> contacts = new ArrayList<String>();
				while (c.moveToNext()) {
					int _id = c.getInt(c
							.getColumnIndex(ContactsContract.Contacts._ID));
					String display_name = c.getString(c
							.getColumnIndex("display_name"));

					// ���ҵ绰
					Cursor c2 = cr.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ "=?", new String[] { _id + "" }, null);
					String number = null;
					while (c2.moveToNext()) {
						number = c2
								.getString(c2
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					}
					c2.close();

					contacts.add(display_name + "("
							+ number.replaceAll(" ", "").replaceAll("-", "") + ")");
				}
				c.close();

				if (contacts.size() == 0) {
					new AlertDialog.Builder(MyContactNewActivity.this)
							.setTitle("��ѡ��").setMessage("ͨѶ¼Ϊ��")
							.setNegativeButton("ȡ��", null).show();
				} else {

					final String[] items = new String[contacts.size()];
					contacts.toArray(items);

					// AlertDialog
					new AlertDialog.Builder(MyContactNewActivity.this)
							.setTitle("��ѡ��")
							.setItems(items, new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									//tvMyContactNew1.setText(items[which]);
									//////////////////////////
									//data = new ArrayList<Map<String, Object>>();
									
									/*//row1
									Map<String, Object> row1 = new HashMap<String, Object>();
								
									row1.put("key1", "����");
									row1.put("key2", items[which].split("\\(")[0]);
									row1.put("key3", R.drawable.forward_25);
									data.add(row1);
									
									//row2
									Map<String, Object> row2 = new HashMap<String, Object>();
									row2.put("key1", "֤������");
									row2.put("key2", "");
									row2.put("key3", R.drawable.forward_25);
									data.add(row2);
									
									//row3
									Map<String, Object> row3 = new HashMap<String, Object>();
									row3.put("key1", "֤������");
									row3.put("key2", "");
									row3.put("key3", R.drawable.forward_25);
									data.add(row3);
									
									//row4
									Map<String, Object> row4 = new HashMap<String, Object>();
									row4.put("key1", "�˿�����");
									row4.put("key2", "");
									row4.put("key3", R.drawable.forward_25);
									data.add(row4);
									
									//row5
									Map<String, Object> row5 = new HashMap<String, Object>();
									row5.put("key1", "�绰");
									row5.put("key2", items[which].split("\\(")[1].split("\\)").clone()[0]);
									row5.put("key3", R.drawable.forward_25);
									data.add(row5);*/
									
									/*final SimpleAdapter adapter = new SimpleAdapter(MyContactNewActivity.this, data, 
											R.layout.item_my_contact_edit, new String[]{"key1","key2","key3"}, new int[]{
											R.id.tvMyContactEditKey,R.id.tvMyContactEditValue, R.id.imgMyContactEditFlg});
									
									lvContactNew.setAdapter(adapter);*/
									/////////////////////////////
									data.get(0).put("key2",items[which].split("\\(")[0]);
									data.get(4).put("key2",items[which].split("\\(")[1].split("\\)")[0]);
									
									adapter.notifyDataSetChanged();
									dialog.dismiss();
								}
							}).setNegativeButton("ȡ��", null).show();
				}

				break;
			}
			return super.onMenuItemSelected(featureId, item);
		}
		
		
		
		
		
		
		

}
