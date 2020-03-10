package com.example.my12306_ymc;



import java.io.IOException;
import java.io.Serializable;
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


import com.example.my12306_ymc.bean.Order;
import com.example.my12306_ymc.bean.Passenger;
import com.example.my12306_ymc.my.MyContactActivity;
import com.example.my12306_ymc.order.OrderPayDoneActivity;
import com.example.my12306_ymc.order.OrderWaitPayActivity;
import com.example.my12306_ymc.ticket.TicketOrderSuccessStep4Activity;
import com.example.my12306_ymc.ticket.TicketPassengerStep3Activity;
import com.example.my12306_ymc.utils.CONSTANT;
import com.example.my12306_ymc.utils.NetUtils;


import com.example.my12306_ymc.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OrderFragment extends android.support.v4.app.Fragment {

	TextView tvOrderWaitPay;
	TextView tvOrderAll;
	ListView lvOrder;
	List<Map<String, Object>> data;
	Order[] orders = null;
	OrderAdapter adapter = null;
	ProgressDialog pDialog = null;
	
	public OrderFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_order, container, false);
	}

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		tvOrderWaitPay = (TextView) getActivity().findViewById(
				R.id.tvOrderWaitPay);
		tvOrderAll = (TextView) getActivity().findViewById(R.id.tvOrderAll);
		lvOrder = (ListView) getActivity().findViewById(R.id.lvOrder);

		tvOrderWaitPay.setOnClickListener(new OrderHandler());
		tvOrderAll.setOnClickListener(new OrderHandler());

		data = new ArrayList<Map<String, Object>>();

		adapter = new OrderAdapter();
		lvOrder.setAdapter(adapter);

		lvOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String orderStatus = (String) data.get(position).get(
						"orderStatus");

				//////////////test
				//for(Order order : orders){
					Log.d("XXXXX",orders.toString());
				//}
				
				if ("已支付".equals(orderStatus)) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), OrderPayDoneActivity.class);
					intent.putExtra("order", (Serializable)orders[position]);
					startActivity(intent);
				} else if ("未支付".equals(orderStatus)) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), OrderWaitPayActivity.class);
					intent.putExtra("order", (Serializable)orders[position]);
					startActivity(intent);
				}

			}
		});
		
	}
	
	class OrderAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if(convertView == null){
				holder = new ViewHolder();
				
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.item_order_list, null);
				
				holder.tvOrderId = (TextView) convertView
						.findViewById(R.id.tvOrderId);
				holder.tvOrderStatus = (TextView) convertView
						.findViewById(R.id.tvOrderStatus);
				holder.tvOrderTrainNo = (TextView) convertView
						.findViewById(R.id.tvOrderTrainNo);
				holder.tvOrderDateFrom = (TextView) convertView
						.findViewById(R.id.tvOrderDateFrom);
				holder.tvOrderStationFrom = (TextView) convertView
						.findViewById(R.id.tvOrderStationFrom);
				holder.tvOrderPrice = (TextView) convertView
						.findViewById(R.id.tvOrderPrice);
				holder.imgOrderFlg = (ImageView) convertView
						.findViewById(R.id.imgOrderFlg);
				
				convertView.setTag(holder);
				
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			//赋值
			holder.tvOrderId.setText(data.get(position).get("orderId").toString());
			
			holder.tvOrderStatus.setText(data.get(position).get("orderStatus")
					.toString());
			if((holder.tvOrderStatus.getText().toString()).equals("未支付")){
				holder.tvOrderStatus.setTextColor(getResources().getColor(
						R.color.orange));
			}else if ((holder.tvOrderStatus.getText().toString()).equals("已取消")) {
				holder.tvOrderStatus.setTextColor(getResources().getColor(
						R.color.grey));
			}else if ((holder.tvOrderStatus.getText().toString()).equals("已支付")) {
				holder.tvOrderStatus.setTextColor(getResources().getColor(
						R.color.blue));
			}
			
			holder.tvOrderTrainNo.setText(data.get(position)
					.get("orderTrainNo").toString());
			holder.tvOrderDateFrom.setText(data.get(position)
					.get("orderDateFrom").toString());
			holder.tvOrderStationFrom.setText(data.get(position)
					.get("orderStationFrom").toString());
			holder.tvOrderPrice.setText(data.get(position).get("orderPrice")
					.toString());
			Integer resId = (Integer) (data.get(position).get("orderFlg"));
			if (resId != null) {
				holder.imgOrderFlg.setImageDrawable(getResources().getDrawable(
						resId));
			} else {
				holder.imgOrderFlg.setImageDrawable(null);
			}

			return convertView;
		}
		
	}
	
	class ViewHolder {
		TextView tvOrderId;
		TextView tvOrderStatus;
		TextView tvOrderTrainNo;
		TextView tvOrderDateFrom;
		TextView tvOrderStationFrom;
		TextView tvOrderPrice;
		ImageView imgOrderFlg;
	}
	
	class OrderHandler implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			data.clear();
			switch (v.getId()) {
			case R.id.tvOrderWaitPay:
				tvOrderWaitPay
						.setBackgroundResource(R.drawable.cab_background_top_mainbar);
				tvOrderAll.setBackgroundResource(0);
				
				new OrderPayWaitTask().execute();
				
				break;
			case R.id.tvOrderAll:
				tvOrderAll
						.setBackgroundResource(R.drawable.cab_background_top_mainbar);
				tvOrderWaitPay.setBackgroundResource(0);
				
				new OrderPayDoneTask().execute();

				break;
			}
		}

	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// 访问服务器 ，更新数据
		//Toast.makeText(MyFriendActivity.this, "刷新", Toast.LENGTH_SHORT).show();
		
		if (!NetUtils.check(getActivity())) {
			Toast.makeText(getActivity(),
					getString(R.string.network_check), Toast.LENGTH_SHORT)
					.show();
			return; // 后续代码不执行
		}

		// 进度对话框
		//pDialog = ProgressDialog.show(getActivity(), null, "正在加载中...",false, true);
		new OrderPayWaitTask().execute();
		
	}
	
	class OrderPayWaitTask extends AsyncTask<String, String, Object> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = ProgressDialog.show(
					getActivity(), null, "正在加载...",
					false, true);
		}

		@Override
		protected Object doInBackground(String... p) {
			// TODO Auto-generated method stub
			HttpPost post = new HttpPost(CONSTANT.HOST
					+ "/otn/OrderList");

			// 发送请求
			DefaultHttpClient client = new DefaultHttpClient();
			String result = "";

			try {
				// jsessionid
				SharedPreferences pref = getActivity().getSharedPreferences("user",
						Context.MODE_PRIVATE);
				String value = pref.getString("Cookie", "");
				// 1
				BasicHeader header = new BasicHeader("Cookie", value);
				post.setHeader(header);
				
				// 请求参数
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("status", "0"));			
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

					String json = EntityUtils.toString(response.getEntity());
					Gson gson = new GsonBuilder().create();
					orders = gson.fromJson(json,Order[].class);
					
					return orders;

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
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (pDialog != null) {
				pDialog.dismiss();
			}
			if (result instanceof Order[]) {
				orders = (Order[]) result;
				for(Order order:orders)
					{Log.d("XXXXX",order.toString());}
				
				data.clear();
				for(int i=0;i<orders.length;i++){
					Map<String, Object> row = new HashMap<String, Object>();
					row.put("orderId", "订单编号"+orders[i].getId());
					row.put("orderStatus", "未支付");
					row.put("orderTrainNo", orders[i].getTrain().getTrainNo());
					row.put("orderDateFrom", orders[i].getTrain().getStartTrainDate());
					row.put("orderStationFrom", orders[i].getTrain()
							.getFromStationName()+"-"+orders[i].getTrain()
							.getToStationName()+" "+orders[i].getPassengerList()
							.size()+"人");
					row.put("orderPrice", orders[i].getOrderPrice());
					row.put("orderFlg", R.drawable.forward_25);
					data.add(row);
				}

			} else if (result instanceof String) {
				if ("3".equals(result.toString())) {
					Toast.makeText(getActivity(), "请重新登录",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(),
							"服务器错误，请重试", Toast.LENGTH_SHORT).show();
				}
			}
			adapter.notifyDataSetChanged();
		}
	}
	
	class OrderPayDoneTask extends AsyncTask<String, String, Object> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = ProgressDialog.show(
					getActivity(), null, "正在加载...",
					false, true);
		}

		@Override
		protected Object doInBackground(String... p) {
			// TODO Auto-generated method stub
			HttpPost post = new HttpPost(CONSTANT.HOST
					+ "/otn/OrderList");

			// 发送请求
			DefaultHttpClient client = new DefaultHttpClient();
			String result = "";

			try {
				// jsessionid
				SharedPreferences pref = getActivity().getSharedPreferences("user",
						Context.MODE_PRIVATE);
				String value = pref.getString("Cookie", "");
				// 1
				BasicHeader header = new BasicHeader("Cookie", value);
				post.setHeader(header);
				
				// 请求参数
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("status", "1"));			
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

					String json = EntityUtils.toString(response.getEntity());
					Gson gson = new GsonBuilder().create();
					orders = gson.fromJson(json,Order[].class);
					
					return orders;

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
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (pDialog != null) {
				pDialog.dismiss();
			}
			if (result instanceof Order[]) {
				orders = (Order[]) result;
				
				for(Order order:orders)
				{Log.d("XXXXX",order.toString());}
				
				data.clear();
				for(int i=0;i<orders.length;i++){
					Map<String, Object> row = new HashMap<String, Object>();
					row.put("orderId", "订单编号"+orders[i].getId());
					if(orders[i].getStatus() == 0){
						row.put("orderStatus", "未支付");
						row.put("orderFlg", R.drawable.forward_25);
					}else if(orders[i].getStatus() == 1){
						row.put("orderStatus", "已支付");
						row.put("orderFlg", R.drawable.forward_25);
					}else if(orders[i].getStatus() == 2){
						row.put("orderStatus", "已取消");
						row.put("orderFlg", null);
					}
					row.put("orderTrainNo", orders[i].getTrain().getTrainNo());
					row.put("orderDateFrom", orders[i].getTrain().getStartTrainDate());
					row.put("orderStationFrom", orders[i].getTrain()
							.getFromStationName()+"-"+orders[i].getTrain()
							.getToStationName()+" "+orders[i].getPassengerList()
							.size()+"人");
					row.put("orderPrice", orders[i].getOrderPrice());
					
					data.add(row);
				}
			} else if (result instanceof String) {
				if ("3".equals(result.toString())) {
					Toast.makeText(getActivity(), "请重新登录",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(),
							"服务器错误，请重试", Toast.LENGTH_SHORT).show();
				}
			}
			adapter.notifyDataSetChanged();
		}
	}
}

