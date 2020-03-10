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

import com.example.my12306_ymc.order.OrderPayDoneActivity;
import com.example.my12306_ymc.utils.CONSTANT;
import com.example.my12306_ymc.bean.Order;
import com.example.my12306_ymc.bean.Passenger;
import com.example.my12306_ymc.R;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class OrderPayDoneActivity extends Activity{

	Button btnOrderPayDone;
	TextView tvOrderPayDoneTicketIdShow = null;
	ListView lvOrderPayDone = null;
	List<Map<String, Object>> data = null;
	Order order = null;
	SimpleAdapter adapter;
	ProgressDialog pDialog = null;
	List<Passenger> passengers =null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_pay_done);
		
		btnOrderPayDone = (Button)findViewById(R.id.btnOrderPayDone);
		tvOrderPayDoneTicketIdShow = (TextView) findViewById(R.id.tvOrderPayDoneTicketIdShow);
		lvOrderPayDone = (ListView) findViewById(R.id.lvOrderPayDone);
		
		data = new ArrayList<Map<String, Object>>();
		Intent intent = getIntent();
		order = (Order) intent.getSerializableExtra("order");
		
passengers = order.getPassengerList();
		
		for(int i=0;i<passengers.size();i++){
			Map<String, Object> row1 = new HashMap<String, Object>();
			row1.put("name", order.getPassengerList().get(i).getName().toString());
			row1.put("trainNo", order.getTrain().getTrainNo().toString());
			row1.put("startTrainDate", order.getTrain().getStartTrainDate().toString());
			row1.put("seatNum", order.getPassengerList().get(i).getSeat().getSeatNo().toString());
			data.add(row1);
		}
		
		adapter = new SimpleAdapter(this, data, R.layout.item_ticket_passenger_step4,
				new String[] { "name", "trainNo", "startTrainDate","seatNum" }, new int[] {
						R.id.tvTicketPassengerStep4Name, R.id.tvTicketPassengerStep4TrainNo,
						R.id.tvTicketPassengerStep4Date,R.id.tvTicketPassengerStep4SeatNo });

		lvOrderPayDone.setAdapter(adapter);
		tvOrderPayDoneTicketIdShow.setText("    您的订单编号为："+order.getId());
		
		
		///////////////对列表的监听
		lvOrderPayDone.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position,
					long id) {
				// TODO Auto-generated method stub
				final String[] types = new String[] { "退票", "改签" };
				String key2 = (String) (data.get(position).get("key2"));
				int idx = 0;
				for (int i = 0; i < types.length; i++) {
					if (types[i].equals(key2)) {
						idx = i;
						break;
					}
				}
				//长条点选项
				new AlertDialog.Builder(OrderPayDoneActivity.this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("请选择操作")
					.setSingleChoiceItems(types, idx,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									if(which == 0){
										new Thread() {
											public void run() {
												// 获取message
												Message msg = handler.obtainMessage();
												HttpPost post = new HttpPost(CONSTANT.HOST + "/otn/Refund");
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
													params.add(new BasicNameValuePair("id", passengers.get(position).getId()));
													params.add(new BasicNameValuePair("idType", passengers.get(position).getIdType()));
													
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
										}.start();

									}else if (which == 1) {
										Toast.makeText(OrderPayDoneActivity.this, "改签成功",
												Toast.LENGTH_SHORT).show();
									}
									
									dialog.dismiss();
								}
							}).setNegativeButton("取消", null).show();
			
			}
			
		});
		
		
		
		btnOrderPayDone.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ImageView view = new ImageView(OrderPayDoneActivity.this);
				view.setImageDrawable(getResources().getDrawable(R.drawable.qr));
				
				new AlertDialog.Builder(OrderPayDoneActivity.this)
				.setTitle("查看二维码")
				.setView(view)
				.setPositiveButton("确定", null)
				.show();
			}
		});
	}
	
	
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 关闭对话框
			if (pDialog != null) {
				pDialog.dismiss();
			}

			switch (msg.what) {
			case 1:
				String result = (String) msg.obj;
				if ("1".equals(result)) {

					Toast.makeText(OrderPayDoneActivity.this,"退票成功",
							Toast.LENGTH_SHORT).show();
					finish();
				} else if ("0".equals(result)) {
					Toast.makeText(OrderPayDoneActivity.this,"退票失败，请稍后重试",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 2:
				Toast.makeText(OrderPayDoneActivity.this, "服务器错误，请重试",
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(OrderPayDoneActivity.this, "请重新登录",
						Toast.LENGTH_SHORT).show();
				break;
			}

		};
	};
	
}
