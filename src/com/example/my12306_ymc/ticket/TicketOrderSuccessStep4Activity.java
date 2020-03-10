package com.example.my12306_ymc.ticket;



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
import com.example.my12306_ymc.my.MyAccountActivity;
import com.example.my12306_ymc.ticket.TicketPassengerListStep3Activity;
import com.example.my12306_ymc.ticket.TicketPassengerListStep3Activity.PassengerListTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.example.my12306_ymc.utils.CONSTANT;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TicketOrderSuccessStep4Activity extends Activity{

	TextView tvTicketOrderSuccessStep4Back;
	TextView tvTicketOrderSuccessStep4Pay;
	ListView lvTicketOrderSuccessStep4;
	TextView tvTicketOrderSuccessStep4Order;
	List<Map<String, Object>> data = null;
	ProgressDialog pDialog = null;
	Order order = null;
	SimpleAdapter adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(com.example.my12306_ymc.R.layout.activity_ticket_order_success_step4);

		tvTicketOrderSuccessStep4Back = (TextView) findViewById(com.example.my12306_ymc.R.id.tvTicketOrderSuccessStep4Back);
		tvTicketOrderSuccessStep4Pay = (TextView) findViewById(com.example.my12306_ymc.R.id.tvTicketOrderSuccessStep4Pay);
		lvTicketOrderSuccessStep4 = (ListView) findViewById(R.id.lvTicketOrderSuccessStep4);
		tvTicketOrderSuccessStep4Order=(TextView) findViewById(R.id.tvTicketOrderSuccessStep4Order);
		
		data = new ArrayList<Map<String, Object>>();

		
		Intent intent = getIntent();
		order = (Order) intent.getSerializableExtra("order");
		
		tvTicketOrderSuccessStep4Order.setText("订单提交成功，您的订单编号为："+order.getId());
		
		List<Passenger> passengers = order.getPassengerList();
		
		for(int i=0;i<passengers.size();i++){
			Map<String, Object> row1 = new HashMap<String, Object>();
			row1.put("name", order.getPassengerList().get(i).getName().toString());
			row1.put("trainNo", order.getTrain().getTrainNo().toString());
			row1.put("startTrainDate", order.getTrain().getStartTrainDate().toString());
			row1.put("seatNum", order.getPassengerList().get(i).getSeat().getSeatNo().toString());
			data.add(row1);
		}
		// 适配器
		// context: 上下文
		// data: 数据
		// resource: 每一行的布局方式
		// from: Map中的key
		// to: 布局中的组件id
		adapter = new SimpleAdapter(this, data, R.layout.item_ticket_order_success_step4,
				new String[] { "name", "trainNo", "startTrainDate","seatNum" }, new int[] {
						R.id.tvOrderSuccessName, R.id.tvOrderSuccessTrainNo,
						R.id.tvOrderSuccessDate,R.id.tvOrderSuccessSeat });

		// 绑定
		lvTicketOrderSuccessStep4.setAdapter(adapter);
		
		
		
		tvTicketOrderSuccessStep4Back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(TicketOrderSuccessStep4Activity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		tvTicketOrderSuccessStep4Pay.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 跳转到下一步
				//Intent intent = new Intent();
				//intent.setClass(TicketOrderSuccessStep4Activity.this, TicketPaySuccessStep5Activity.class);
				//startActivity(intent);
				new payTask().execute();
			}
		});
	
	}//onCreate的
	
	//////////异步任务
	class payTask extends AsyncTask<String, String, String>
	{

		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = ProgressDialog.show(
					TicketOrderSuccessStep4Activity.this, null, "正在加载中...",
					false, true);
		}
		
		
		@Override
		protected String doInBackground(String... p) {
			// TODO Auto-generated method stub
			HttpPost post = new HttpPost(CONSTANT.HOST
					+ "/otn/TicketPassengerList");

			// 发送请求
			DefaultHttpClient client = new DefaultHttpClient();
			String result = "";
			
			try{
				// jsessionid
				SharedPreferences pref = getSharedPreferences("user",
						Context.MODE_PRIVATE);
				String value = pref.getString("Cookie", "");
				// 1
				BasicHeader header = new BasicHeader("Cookie", value);
				post.setHeader(header);
				
				////////////发送消息
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
			
			}//try的
			catch (UnsupportedEncodingException e) {
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
				result = "1"; // 重新登录
			}
			return result;
		}//do in background的


		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if (pDialog != null) {
				pDialog.dismiss();
			}
			
			if(result.equals("1"))
			{
				Toast.makeText(TicketOrderSuccessStep4Activity.this,
						"您已成功支付",Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.putExtra("order", order);
				intent.setClass(TicketOrderSuccessStep4Activity.this, TicketPaySuccessStep5Activity.class);
				startActivity(intent);
				
			}
			
			else{
				Toast.makeText(TicketOrderSuccessStep4Activity.this,
						"您在支付过程出现异常，请重新支付",Toast.LENGTH_SHORT).show();
				Toast.makeText(TicketOrderSuccessStep4Activity.this,
						result,Toast.LENGTH_SHORT).show();
			}
		}//onPostExecute的
		
		
		
		
	}///异步任务的
	
	
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.setClass(TicketOrderSuccessStep4Activity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	
	
}
