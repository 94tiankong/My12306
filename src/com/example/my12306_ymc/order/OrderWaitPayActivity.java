package com.example.my12306_ymc.order;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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


import com.example.my12306_ymc.MainActivity;
import com.example.my12306_ymc.R;
import com.example.my12306_ymc.bean.Order;
import com.example.my12306_ymc.bean.Passenger;
import com.example.my12306_ymc.ticket.TicketOrderSuccessStep4Activity;
import com.example.my12306_ymc.ticket.TicketPaySuccessStep5Activity;
import com.example.my12306_ymc.utils.CONSTANT;
import com.example.my12306_ymc.utils.NetUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class OrderWaitPayActivity extends Activity{

	
	ListView lvOrderWaitPay;
	TextView tvOrderWaitPayBack;
	TextView tvOrderWaitPay;
	TextView tvOrderWaitPayCancel;
	TextView tvOrderWaitPayOrderId;
	ProgressDialog pDialog = null;
	Order order;
	List<Map<String, Object>> data = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_wait_pay);
		
		lvOrderWaitPay = (ListView)findViewById(R.id.lvOrderWaitPay);
		tvOrderWaitPay = (TextView)findViewById(R.id.tvOrderWaitPayPay);
		tvOrderWaitPayBack =(TextView)findViewById(R.id.tvOrderWaitPayBack);
		tvOrderWaitPayCancel=(TextView)findViewById(R.id.tvOrderWaitPayCancel);
		tvOrderWaitPayOrderId=(TextView)findViewById(R.id.tvOrderWaitPayOrderId);
		
		data = new ArrayList<Map<String, Object>>();

		Intent intent = getIntent();
		order = (Order) intent.getSerializableExtra("order");
		
		tvOrderWaitPayOrderId.setText("      ���Ķ�����Ϊ��"+order.getId()
				+"�������������֧������Ȼ��30���Ӻ����Ķ�������ȡ����");
		
		List<Passenger> passengers = order.getPassengerList();
		
		for(int i=0;i<passengers.size();i++){
			Map<String, Object> row1 = new HashMap<String, Object>();
			row1.put("name", order.getPassengerList().get(i).getName());
			row1.put("trainNum", order.getTrain().getTrainNo());
			row1.put("seat", order.getPassengerList().get(i).getSeat().getSeatNo());
			data.add(row1);
		}
		
		tvOrderWaitPay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				new Thread(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						// ��ȡmessage
						Message msg = handler2.obtainMessage();

						HttpPost post = new HttpPost(CONSTANT.HOST
								+ "/otn/Pay");
						
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
							params.add(new BasicNameValuePair("orderId", order.getId()));			
							UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params,
									"UTF-8");
							post.setEntity(entity);
							
							// ��ʱ����
							client.getParams().setParameter(
									CoreConnectionPNames.CONNECTION_TIMEOUT,
									CONSTANT.REQUEST_TIMEOUT);
							client.getParams().setParameter(
									CoreConnectionPNames.SO_TIMEOUT,
									CONSTANT.SO_TIMEOUT);

							HttpResponse response = client.execute(post);

							// ������
							if (response.getStatusLine().getStatusCode() == 200) {

								String json = EntityUtils
										.toString(response.getEntity());

								Gson gson = new GsonBuilder().create();

								String result = gson.fromJson(json,
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
						handler2.sendMessage(msg);
					};			
				}.start();
				// TODO Auto-generated method stub
				//Intent intent = new Intent();
				//intent.putExtra("order", order);
				//intent.setClass(OrderWaitPayActivity.this, TicketPaySuccessStep5Activity.class);
				//startActivity(intent);
			}
		});
		
		tvOrderWaitPayBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(OrderWaitPayActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
		
		
		
		
		/*
		final List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

		// row 1
				Map<String, Object> row = new HashMap<String, Object>();
				row.put("name", "������");
				row.put("trainNum", "D2202");
				row.put("seat", "3��6��B��");
				data.add(row);

				// row 2
				row = new HashMap<String, Object>();
				row.put("name", "���ӵ�");
				row.put("trainNum", "K818");
				row.put("seat", "5��23��");
				data.add(row);

				// row 3
				row = new HashMap<String, Object>();
				row.put("name", "�캺");
				row.put("trainNum", "K817");
				row.put("seat", "8��14��");
				data.add(row);
		*/		
		
		SimpleAdapter adapter = new SimpleAdapter(this, data,
				R.layout.item_order_wait_pay, new String[] { "name", "trainNum",
						"seat" }, new int[] { R.id.tvWaitPayName,
						R.id.tvWaitPayTrainNum, R.id.tvWaitPaySeat });
		
		// ��
		lvOrderWaitPay.setAdapter(adapter);
		
		
		//ȡ������������
		tvOrderWaitPayCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				/*new AlertDialog.Builder(OrderWaitPayActivity.this)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle("���Ķ�����ȡ����")
				.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.setClass(OrderWaitPayActivity.this, MainActivity.class);
						startActivity(intent);
					}
				}).show();*/
				
				
				if (!NetUtils.check(OrderWaitPayActivity.this)) {
					Toast.makeText(OrderWaitPayActivity.this,
							getString(R.string.network_check), Toast.LENGTH_SHORT)
							.show();
					return; // �������벻ִ��
				}
				
				new OrderCancelTask().execute();
				finish();
				
			}
		});
		
	
	
	}//onCreatede 
	
	/////////////////ȡ�������첽������ʵ��
	class OrderCancelTask extends AsyncTask<String, String, String>
	{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = ProgressDialog.show(OrderWaitPayActivity.this,
					null, "���ڼ�����...", false, true);
		}

		@Override
		protected String doInBackground(String... p) {
			// TODO Auto-generated method stub
			HttpPost post = new HttpPost(CONSTANT.HOST + "/otn/Cancel");

			// ��������
			DefaultHttpClient client = new DefaultHttpClient();
			String result = "";

			try {
				// jsessionid
				SharedPreferences pref = getSharedPreferences("user",
						Context.MODE_PRIVATE);
				String value = pref.getString("Cookie", "");
				// 1
				BasicHeader header = new BasicHeader("Cookie", value);
				post.setHeader(header);

				// ����
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				
				params.add(new BasicNameValuePair("orderId", order.getId()));
				
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
					result = gson.fromJson(
							EntityUtils.toString(response.getEntity()),
							String.class);
						////////////////////////////////////
					return result;

				} else {
					result = "2";
				}

				client.getConnectionManager().shutdown();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result = "2";
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result = "2";
			} catch (IOException e) {
				e.printStackTrace();
				result = "2";
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result = "2";
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
				result = "3"; // ���µ�¼
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			

			if (pDialog != null) {
				pDialog.dismiss();
			}
			
			if(result.equals("1"))
			{
				Toast.makeText(OrderWaitPayActivity.this,
						"���ѳɹ�ȡ������",Toast.LENGTH_SHORT).show();
				//finish();
				Intent intent= new Intent();
				intent.setClass(OrderWaitPayActivity.this, MainActivity.class);
				startActivity(intent);
			}
			
			else{
				Toast.makeText(OrderWaitPayActivity.this,
						"����ȡ���������̳����쳣��������ȡ������",Toast.LENGTH_SHORT).show();
				Toast.makeText(OrderWaitPayActivity.this,
						result,Toast.LENGTH_SHORT).show();
			}
			
		}
		
	}///ȡ�������첽����������
	
	Handler handler2 = new Handler() {
		public void handleMessage(Message msg) {
			// �رնԻ���
			if (pDialog != null) {
				pDialog.dismiss();
			}

			String result = (String)msg.obj;
			switch (msg.what) {
			case 1:
				if(result.equals("1")){
					Toast.makeText(OrderWaitPayActivity.this, "֧���ɹ�",
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.putExtra("order", order);
					intent.setClass(OrderWaitPayActivity.this, TicketPaySuccessStep5Activity.class);
					startActivity(intent);
					finish();
				}else if (result.equals("0")) {
					Toast.makeText(OrderWaitPayActivity.this, "֧��ʧ�ܣ����Ժ�����",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 2:
				Toast.makeText(OrderWaitPayActivity.this, "����������������",
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(OrderWaitPayActivity.this, "�����µ�¼",
						Toast.LENGTH_SHORT).show();
				break;
			}

		};
	};
	
}
