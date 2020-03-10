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
		
		tvOrderWaitPayOrderId.setText("      您的订单号为："+order.getId()
				+"，请您尽快进行支付，不然在30分钟后您的订单将会取消！");
		
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
						// 获取message
						Message msg = handler2.obtainMessage();

						HttpPost post = new HttpPost(CONSTANT.HOST
								+ "/otn/Pay");
						
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
							params.add(new BasicNameValuePair("orderId", order.getId()));			
							UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params,
									"UTF-8");
							post.setEntity(entity);
							
							// 超时设置
							client.getParams().setParameter(
									CoreConnectionPNames.CONNECTION_TIMEOUT,
									CONSTANT.REQUEST_TIMEOUT);
							client.getParams().setParameter(
									CoreConnectionPNames.SO_TIMEOUT,
									CONSTANT.SO_TIMEOUT);

							HttpResponse response = client.execute(post);

							// 处理结果
							if (response.getStatusLine().getStatusCode() == 200) {

								String json = EntityUtils
										.toString(response.getEntity());

								Gson gson = new GsonBuilder().create();

								String result = gson.fromJson(json,
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
				row.put("name", "李云阴");
				row.put("trainNum", "D2202");
				row.put("seat", "3车6号B座");
				data.add(row);

				// row 2
				row = new HashMap<String, Object>();
				row.put("name", "王从单");
				row.put("trainNum", "K818");
				row.put("seat", "5车23号");
				data.add(row);

				// row 3
				row = new HashMap<String, Object>();
				row.put("name", "徐汉");
				row.put("trainNum", "K817");
				row.put("seat", "8车14号");
				data.add(row);
		*/		
		
		SimpleAdapter adapter = new SimpleAdapter(this, data,
				R.layout.item_order_wait_pay, new String[] { "name", "trainNum",
						"seat" }, new int[] { R.id.tvWaitPayName,
						R.id.tvWaitPayTrainNum, R.id.tvWaitPaySeat });
		
		// 绑定
		lvOrderWaitPay.setAdapter(adapter);
		
		
		//取消订单弹出框
		tvOrderWaitPayCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				/*new AlertDialog.Builder(OrderWaitPayActivity.this)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle("您的订单已取消！")
				.setPositiveButton("确定",new DialogInterface.OnClickListener() {
					
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
					return; // 后续代码不执行
				}
				
				new OrderCancelTask().execute();
				finish();
				
			}
		});
		
	
	
	}//onCreatede 
	
	/////////////////取消订单异步任务函数实现
	class OrderCancelTask extends AsyncTask<String, String, String>
	{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = ProgressDialog.show(OrderWaitPayActivity.this,
					null, "正在加载中...", false, true);
		}

		@Override
		protected String doInBackground(String... p) {
			// TODO Auto-generated method stub
			HttpPost post = new HttpPost(CONSTANT.HOST + "/otn/Cancel");

			// 发送请求
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

				// 参数
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				
				params.add(new BasicNameValuePair("orderId", order.getId()));
				
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
				result = "3"; // 重新登录
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
						"您已成功取消订单",Toast.LENGTH_SHORT).show();
				//finish();
				Intent intent= new Intent();
				intent.setClass(OrderWaitPayActivity.this, MainActivity.class);
				startActivity(intent);
			}
			
			else{
				Toast.makeText(OrderWaitPayActivity.this,
						"您在取消订单过程出现异常，请重新取消订单",Toast.LENGTH_SHORT).show();
				Toast.makeText(OrderWaitPayActivity.this,
						result,Toast.LENGTH_SHORT).show();
			}
			
		}
		
	}///取消订单异步任务函数结束
	
	Handler handler2 = new Handler() {
		public void handleMessage(Message msg) {
			// 关闭对话框
			if (pDialog != null) {
				pDialog.dismiss();
			}

			String result = (String)msg.obj;
			switch (msg.what) {
			case 1:
				if(result.equals("1")){
					Toast.makeText(OrderWaitPayActivity.this, "支付成功",
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.putExtra("order", order);
					intent.setClass(OrderWaitPayActivity.this, TicketPaySuccessStep5Activity.class);
					startActivity(intent);
					finish();
				}else if (result.equals("0")) {
					Toast.makeText(OrderWaitPayActivity.this, "支付失败，请稍后重试",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 2:
				Toast.makeText(OrderWaitPayActivity.this, "服务器错误，请重试",
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(OrderWaitPayActivity.this, "请重新登录",
						Toast.LENGTH_SHORT).show();
				break;
			}

		};
	};
	
}
