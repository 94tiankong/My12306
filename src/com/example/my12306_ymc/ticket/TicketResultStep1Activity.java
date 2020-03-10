package com.example.my12306_ymc.ticket;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.example.my12306_ymc.bean.Train;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.example.my12306_ymc.bean.Passenger;
import com.example.my12306_ymc.my.MyContactActivity;
import com.example.my12306_ymc.utils.NetUtils;
import com.example.my12306_ymc.bean.Seat;
import com.example.my12306_ymc.ticket.TicketResultStep1Activity;
import com.example.my12306_ymc.ticket.TicketResultStep1Activity.Step1Task;
import com.example.my12306_ymc.utils.CONSTANT;
import com.example.my12306_ymc.R.color;
import com.example.my12306_ymc.bean.*;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TicketResultStep1Activity extends Activity {
	ListView lvTicketResultStep1 = null;
	TextView tvTicketResultStep1Before = null;
	TextView tvTicketResultStep1After = null;
	TextView tvTicketResultStep1DateTitle = null;
	ProgressDialog pDialog = null;
	TextView tvTicketResultStep1StationTitle=null;
	List<Map<String, Object>> data = null;
	SimpleAdapter adapter = null;
	Train[] trains = null;
	
	

	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ticket_result_step1);
		////////////
		//Intent intent = getIntent();
		//Toast.makeText(TicketResultStep1Activity.this, intent
		//				.getStringExtra("toStationName"),
		//		Toast.LENGTH_SHORT).show();
////////////////////
		lvTicketResultStep1 = (ListView) findViewById(R.id.lvTicketResultStep1);
		tvTicketResultStep1Before = (TextView) findViewById(R.id.tvTicketResultStep1Before);
		tvTicketResultStep1After = (TextView) findViewById(R.id.tvTicketResultStep1After);
		tvTicketResultStep1DateTitle = (TextView) findViewById(R.id.tvTicketResultStep1DateTitle);
		tvTicketResultStep1StationTitle=(TextView)findViewById(R.id.tvTicketResultStep1StationTitle);
		
		tvTicketResultStep1Before.setOnClickListener(new HandlerTicketResultStep1());
		tvTicketResultStep1After.setOnClickListener(new HandlerTicketResultStep1());

		data = new ArrayList<Map<String, Object>>();
		
		adapter=new SimpleAdapter(this, data,
				R.layout.item_ticket_result_step1, new String[] { "trainNo",
						"flg1", "flg2", "timeFrom", "timeTo", "seat1", "seat2",
						"seat3", "seat4" }, new int[] {
						R.id.tvTicketResultStep1TrainNo,
						R.id.imgTicketResultStep1Flg1,
						R.id.imgTicketResultStep1Flg2,
						R.id.tvTicketResultStep1TimeFrom,
						R.id.tvTicketResultStep1TimeTo,
						R.id.tvTicketResultStep1Seat1,
						R.id.tvTicketResultStep1Seat2,
						R.id.tvTicketResultStep1Seat3,
						R.id.tvTicketResultStep1Seat4, });
		
		// ����
		tvTicketResultStep1DateTitle.setText(getIntent().getStringExtra(
				"startTrainDate"));
		// from - to
		// ...
		tvTicketResultStep1StationTitle.setText(getIntent()
				.getStringExtra("fromStationName")+"-"
				+getIntent().getStringExtra("toStationName"));
		
		// ����
		
		/*
		Map<String, Object> row1 = new HashMap<String, Object>();
		row1.put("trainNo", "G2202");
		row1.put("flg1", R.drawable.flg_shi);
		row1.put("flg2", R.drawable.flg_guo);
		row1.put("timeFrom", "07:00");
		row1.put("timeTo", "14:39(0��)");
		row1.put("seat1", "�߼�����:650");
		row1.put("seat2", "Ӳ��:200");
		row1.put("seat3", "һ����:420");
		row1.put("seat4", "������:800");
		data.add(row1);

		Map<String, Object> row2 = new HashMap<String, Object>();
		row2.put("trainNo", "K386");
		row2.put("flg1", R.drawable.flg_shi);
		row2.put("flg2", R.drawable.flg_zhong);
		row2.put("timeFrom", "09:00");
		row2.put("timeTo", "12:39(0��)");
		row2.put("seat1", "�߼�����:100");
		row2.put("seat2", "Ӳ��:8");
		row2.put("seat3", "һ����:20");
		row2.put("seat4", "������:200");
		data.add(row2);

		Map<String, Object> row3 = new HashMap<String, Object>();
		row3.put("trainNo", "K817");
		row3.put("flg1", R.drawable.flg_guo);
		row3.put("flg2", R.drawable.flg_guo);
		row3.put("timeFrom", "15:00");
		row3.put("timeTo", "12:39(1��)");
		row3.put("seat1", "�߼�����:55");
		row3.put("seat2", "Ӳ��:77");
		row3.put("seat3", "һ����:33");
		data.add(row3);
		 */
		lvTicketResultStep1.setAdapter(adapter);
		
		lvTicketResultStep1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(TicketResultStep1Activity.this, TicketDetailsStep2Activity.class);
				intent.putExtra("train", (Serializable)trains[position]);
				intent.putExtra("tvTicketResultStep1StationTitle", tvTicketResultStep1StationTitle.getText()
						.toString());
				intent.putExtra("startTrainDate", tvTicketResultStep1DateTitle.getText()
						.toString());
				startActivity(intent);
			}
		});
		
		new Thread() {
			public void run() {
				// ��ȡmessage
				Message msg = handler.obtainMessage();

				HttpPost post = new HttpPost(CONSTANT.HOST
						+ "/otn/TrainList");

				// ��������
				DefaultHttpClient client = new DefaultHttpClient();

				try {
					// jsessionid
					SharedPreferences pref = getSharedPreferences("user",
							Context.MODE_PRIVATE);
					String value = pref.getString("Cookie", "");
					BasicHeader header = new BasicHeader("Cookie", value);
					post.setHeader(header);
					
					
					Intent intent = getIntent();

					List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("fromStationName", intent
							.getStringExtra("fromStationName")));
					params.add(new BasicNameValuePair("toStationName", intent
							.getStringExtra("toStationName")));
					params.add(new BasicNameValuePair("startTrainDate",
							tvTicketResultStep1DateTitle.getText().toString()
									.split(" ")[0]));
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

					// �������
					if (response.getStatusLine().getStatusCode() == 200) {

						
						String json = EntityUtils.toString(response.getEntity());
						Gson gson = new GsonBuilder().create();
						Train[] trains = gson.fromJson(json, Train[].class);

						// ������Ϣ
						msg.obj = trains;
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
		}.start();
		
		new Step1Task().execute();
	}

	class HandlerTicketResultStep1 implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Calendar c = Calendar.getInstance();

			// ��ȡѡ������
			String oldDateFrom = tvTicketResultStep1DateTitle.getText()
					.toString();
			int oldYear = Integer
					.parseInt(oldDateFrom.split(" ")[0].split("-")[0]);
			int oldMonthOfYear = Integer.parseInt(oldDateFrom.split(" ")[0]
					.split("-")[1]) - 1;
			int oldDayOfMonth = Integer.parseInt(oldDateFrom.split(" ")[0]
					.split("-")[2]);
			c.set(oldYear, oldMonthOfYear, oldDayOfMonth);

			switch (v.getId()) {
			case R.id.tvTicketResultStep1Before:
				// ǰһ��
				c.add(Calendar.DAY_OF_MONTH, -1);
				break;
			case R.id.tvTicketResultStep1After:
				// ��һ��
				c.add(Calendar.DAY_OF_MONTH, 1);
				break;
			}

			// ����ѡ������
			String weekDay = DateUtils.formatDateTime(
					TicketResultStep1Activity.this, c.getTimeInMillis(),
					DateUtils.FORMAT_SHOW_WEEKDAY
							| DateUtils.FORMAT_ABBREV_WEEKDAY);

			tvTicketResultStep1DateTitle.setText(c.get(Calendar.YEAR) + "-"
					+ (c.get(Calendar.MONTH) + 1) + "-"
					+ c.get(Calendar.DAY_OF_MONTH) + " " + weekDay);
			
			// �����첽����
			new Step1Task().execute();
		}

	}



	class Step1Task extends AsyncTask<String, String, Object> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = ProgressDialog.show(TicketResultStep1Activity.this, null,
					"���ڼ�����...", false, true);
		}

		@Override
		protected Object doInBackground(String... p) {
			// TODO Auto-generated method stub
			HttpPost post = new HttpPost(CONSTANT.HOST + "/otn/TrainList");

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

				// �������
				Intent intent = getIntent();

				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("fromStationName", intent
						.getStringExtra("fromStationName")));
				params.add(new BasicNameValuePair("toStationName", intent
						.getStringExtra("toStationName")));
				params.add(new BasicNameValuePair("startTrainDate",
						tvTicketResultStep1DateTitle.getText().toString()
								.split(" ")[0]));
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

				// �������
				if (response.getStatusLine().getStatusCode() == 200) {

					String json = EntityUtils.toString(response.getEntity());
					Gson gson = new GsonBuilder().create();
					Train[] trains = gson.fromJson(json, Train[].class);

					return trains;

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
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			data.clear();

			if (pDialog != null) {
				pDialog.dismiss();
			}

			if (result instanceof Train[]) {

				trains = (Train[]) result;
				if (trains.length == 0) {
					Toast.makeText(TicketResultStep1Activity.this, "û��ѯ����Ӧ�ĳ���",
							Toast.LENGTH_SHORT).show();
				} else {

					for (Train train : trains) {
						Map<String, Object> row1 = new HashMap<String, Object>();
						row1.put("trainNo", train.getTrainNo());
						
						if (train.getStartStationName().equals(
								train.getFromStationName())) {
							row1.put("flg1", R.drawable.flg_shi);
						} else {
							row1.put("flg1", R.drawable.flg_guo);
						} 
						
						if (train.getEndStationName().equals(
								train.getToStationName())) {
							row1.put("flg2", R.drawable.flg_zhong);
						} else {
							row1.put("flg2", R.drawable.flg_guo);
						}

						row1.put("timeFrom", train.getStartTime());
						row1.put(
								"timeTo",
								train.getArriveTime() + "("
										+ train.getDayDifference() + "��)");

						String[] seatKey = { "seat1", "seat2", "seat3", "seat4" };
						Map<String, Seat> seats = train.getSeats();
						int i = 0;
						for (String key : seats.keySet()) {
							Seat seat = seats.get(key);
							row1.put(seatKey[i++], seat.getSeatName() + ":"
									+ seat.getSeatNum());
						}

						data.add(row1);
					}
				}

				adapter.notifyDataSetChanged();

			} else if (result instanceof String) {
				if ("3".equals(result.toString())) {
					Toast.makeText(TicketResultStep1Activity.this, "�����µ�¼",
							Toast.LENGTH_SHORT).show();

				} else {
					Toast.makeText(TicketResultStep1Activity.this, "����������������",
							Toast.LENGTH_SHORT).show();
				}
			}

		}

	}

///////////////resume

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//if (!NetUtils.check(TicketResultStep1Activity.this)) {
		//	Toast.makeText(TicketResultStep1Activity.this,
		//			getString(R.string.network_check), Toast.LENGTH_SHORT)
		//			.show();
		//	return; // �������벻ִ��
		//}
//
		// ���ȶԻ���
		//pDialog = ProgressDialog.show(TicketResultStep1Activity.this, null, "���ڼ�����...",
		//		false, true);
		
		
		
	}


	Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (pDialog != null) {
				pDialog.dismiss();
			}
			
			switch (msg.what) {
			case 1:
				// Passenger[] => data
				Train[] trains = (Train[]) msg.obj;
				if (trains.length == 0) {
					Toast.makeText(TicketResultStep1Activity.this, "û��ѯ����Ӧ�ĳ���",
							Toast.LENGTH_SHORT).show();
				} else {

					for (Train train : trains) {
						Map<String, Object> row1 = new HashMap<String, Object>();
						row1.put("trainNo", train.getTrainNo());
						
						if (train.getStartStationName().equals(
								train.getFromStationName())) {
							row1.put("flg1", R.drawable.flg_shi);
						} else {
							row1.put("flg1", R.drawable.flg_guo);
						} 
						
						if (train.getEndStationName().equals(
								train.getToStationName())) {
							row1.put("flg2", R.drawable.flg_zhong);
						} else {
							row1.put("flg2", R.drawable.flg_guo);
						}

						row1.put("timeFrom", train.getStartTime());
						row1.put(
								"timeTo",
								train.getArriveTime() + "("
										+ train.getDayDifference() + "��)");

						String[] seatKey = { "seat1", "seat2", "seat3", "seat4" };
						Map<String, Seat> seats = train.getSeats();
						int i = 0;
						for (String key : seats.keySet()) {
							Seat seat = seats.get(key);
							row1.put(seatKey[i++], seat.getSeatName() + ":"
									+ seat.getSeatNum());
						}

						data.add(row1);
					}
				}
				adapter.notifyDataSetChanged();

				break;
			case 2:
				Toast.makeText(TicketResultStep1Activity.this, "����������������",
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(TicketResultStep1Activity.this, "�����µ�¼",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
	
	
	
	
	
	

}

/*public class TicketResultStep1Activity extends Activity
{

	ListView lvTicketResultStep1 = null;
	TextView tvTicketResultStep1Before = null;
	TextView tvTicketResultStep1After = null;
	TextView tvTicketResultStep1DateTitle = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		lvTicketResultStep1=(ListView)findViewById(R.id.lvTicketResultStep1);
		tvTicketResultStep1Before = (TextView) findViewById(R.id.tvTicketResultStep1Before);
		tvTicketResultStep1After = (TextView) findViewById(R.id.tvTicketResultStep1After);
		tvTicketResultStep1DateTitle = (TextView) findViewById(R.id.tvTicketResultStep1DateTitle);
		
		tvTicketResultStep1Before.setOnClickListener(new HandlerTicketResultStep1());
		tvTicketResultStep1After.setOnClickListener(new HandlerTicketResultStep1());
		
		// ����
				List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

				Map<String, Object> row1 = new HashMap<String, Object>();
				row1.put("trainNo", "G2202");
				row1.put("flg1", R.drawable.flg_shi);
				row1.put("flg2", R.drawable.flg_guo);
				row1.put("timeFrom", "07:00");
				row1.put("timeTo", "14:39(0��)");
				row1.put("seat1", "�߼�����:650");
				row1.put("seat2", "Ӳ��:200");
				row1.put("seat3", "һ����:420");
				row1.put("seat4", "������:800");
				data.add(row1);

				Map<String, Object> row2 = new HashMap<String, Object>();
				row2.put("trainNo", "K386");
				row2.put("flg1", R.drawable.flg_shi);
				row2.put("flg2", R.drawable.flg_zhong);
				row2.put("timeFrom", "09:00");
				row2.put("timeTo", "12:39(0��)");
				row2.put("seat1", "�߼�����:100");
				row2.put("seat2", "Ӳ��:8");
				row2.put("seat3", "һ����:20");
				row2.put("seat4", "������:200");
				data.add(row2);

				Map<String, Object> row3 = new HashMap<String, Object>();
				row3.put("trainNo", "K817");
				row3.put("flg1", R.drawable.flg_guo);
				row3.put("flg2", R.drawable.flg_guo);
				row3.put("timeFrom", "15:00");
				row3.put("timeTo", "12:39(1��)");
				row3.put("seat1", "�߼�����:55");
				row3.put("seat2", "Ӳ��:77");
				row3.put("seat3", "һ����:33");
				data.add(row3);

		SimpleAdapter adapter = new SimpleAdapter(this, data,
				R.layout.item_ticket_result_step1, new String[] { "trainNo",
						"flg1", "flg2", "timeFrom", "timeTo", "seat1", "seat2",
						"seat3", "seat4" }, new int[] {
						R.id.tvTicketResultStep1TrainNo,
						R.id.imgTicketResultStep1Flg1,
						R.id.imgTicketResultStep1Flg2,
						R.id.tvTicketResultStep1TimeFrom,
						R.id.tvTicketResultStep1TimeTo,
						R.id.tvTicketResultStep1Seat1,
						R.id.tvTicketResultStep1Seat2,
						R.id.tvTicketResultStep1Seat3,
						R.id.tvTicketResultStep1Seat4, });

		lvTicketResultStep1.setAdapter(adapter);
		
		lvTicketResultStep1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(TicketResultStep1Activity.this, TicketDetailsStep2Activity.class);
				startActivity(intent);
			}
		});
		
	}
	
	class HandlerTicketResultStep1 implements OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Calendar c = Calendar.getInstance();
			
			String oldDateFrom = tvTicketResultStep1DateTitle.getText().toString();
			int oldYear = Integer.parseInt(oldDateFrom.split("")[0].split("-")[0]);
			int oldMonthOfYear = Integer.parseInt(oldDateFrom.split(" ")[0]
							.split("-")[1]) - 1;
			int oldDayOfMonth = Integer.parseInt(oldDateFrom.split(" ")[0]
							.split("-")[2]);
			
			c.set(oldYear, oldMonthOfYear, oldDayOfMonth);

			switch (v.getId()) {
			case R.id.tvTicketResultStep1Before:
				// ǰһ��
				c.add(Calendar.DAY_OF_MONTH, -1);
				break;
			case R.id.tvTicketResultStep1After:
				// ��һ��
				c.add(Calendar.DAY_OF_MONTH, 1);
				break;
			
		}
			// ����ѡ������
				String weekDay = DateUtils.formatDateTime(
					TicketResultStep1Activity.this, c.getTimeInMillis(),
					DateUtils.FORMAT_SHOW_WEEKDAY
							| DateUtils.FORMAT_ABBREV_WEEKDAY);

				tvTicketResultStep1DateTitle.setText(c.get(Calendar.YEAR) + "-"
						+ (c.get(Calendar.MONTH) + 1) + "-"
						+ c.get(Calendar.DAY_OF_MONTH) + " " + weekDay);
		
	}
	
}
}*/
