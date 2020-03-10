package com.example.my12306_ymc.ticket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.View.OnClickListener;

import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my12306_ymc.R;
import com.example.my12306_ymc.ticket.TicketDetailsStep2Activity;
import com.example.my12306_ymc.bean.Train;
import com.example.my12306_ymc.bean.Seat;

public class TicketDetailsStep2Activity extends Activity {

	ListView lvTicketDetailsStep2;
	List<Map<String, Object>> data = null;
	TextView tvTicketDetailsStep2TrainNo;
	TextView tvTicketDetailsStep2DurationTime;
	TextView tvTicketDetailsStep2After;
	TextView tvTicketDetailsStep2Before;
	ProgressDialog pDialog = null;
	Train train = null;
	TextView tvTicketDetailsStep2DateTitle=null;
	TextView tvTicketDetailsStep2StationTitle=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ticket_details_step2);

		lvTicketDetailsStep2 = (ListView) findViewById(R.id.lvTicketDetailsStep2);
		tvTicketDetailsStep2TrainNo = (TextView) findViewById(R.id.tvTicketDetailsStep2TrainNo);
		tvTicketDetailsStep2DurationTime = (TextView) findViewById(R.id.tvTicketDetailsStep2DurationTime);
		tvTicketDetailsStep2Before = (TextView) findViewById(R.id.tvTicketDetailsStep2Before);
		tvTicketDetailsStep2After = (TextView) findViewById(R.id.tvTicketDetailsStep2After);

		tvTicketDetailsStep2DateTitle=(TextView)findViewById(R.id.tvTicketDetailsStep2DateTitle);
		tvTicketDetailsStep2StationTitle=(TextView)findViewById(R.id.tvTicketDetailsStep2StationTitle);
		// ����
		tvTicketDetailsStep2DateTitle.setText(getIntent().getStringExtra(
				"startTrainDate"));
		// from - to
		// ...
		tvTicketDetailsStep2StationTitle.setText(getIntent()
				.getStringExtra("tvTicketResultStep1StationTitle"));
		
		
		train = (Train) getIntent().getSerializableExtra("train");
		tvTicketDetailsStep2TrainNo.setText(train.getTrainNo());
		tvTicketDetailsStep2DurationTime.setText(train.getStartTime() + " - "
				+ train.getArriveTime() + ", ��ʱ" + train.getDurationTime());
		
		data = new ArrayList<Map<String, Object>>();

		/*
		Map<String, Object> row1 = new HashMap<String, Object>();
		row1.put("seatName", "�߼�����");
		row1.put("seatNum", "200��");
		row1.put("seatPrice", "��188.5");
		data.add(row1);

		Map<String, Object> row2 = new HashMap<String, Object>();
		row2.put("seatName", "Ӳ��");
		row2.put("seatNum", "88��");
		row2.put("seatPrice", "��148.5");
		data.add(row2);

		Map<String, Object> row3 = new HashMap<String, Object>();
		row3.put("seatName", "����");
		row3.put("seatNum", "100��");
		row3.put("seatPrice", "��148.5");
		data.add(row3);
		*/
		Map<String, Seat> seats = train.getSeats();
		for (String key : seats.keySet()) {
			Seat seat = seats.get(key);

			Map<String, Object> row1 = new HashMap<String, Object>();
			row1.put("seatName", seat.getSeatName());
			row1.put("seatNum", seat.getSeatNum() + "��");
			row1.put("seatPrice", "��" + seat.getSeatPrice());
			data.add(row1);
		}

		lvTicketDetailsStep2.setAdapter(new TicketDetailsStep2Adapter());
		
		// ǰһ��
		tvTicketDetailsStep2Before.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// ��
			}
		});

		// ��һ��
		tvTicketDetailsStep2After.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// ��
			}
		});		
		

	}//onCreate��

	class TicketDetailsStep2Adapter extends BaseAdapter {
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			// ���������󲻴��ڣ����������ڣ�ȡ�����У�
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();

				// ����convertView(�����в���)
				convertView = LayoutInflater.from(
						TicketDetailsStep2Activity.this).inflate(
						R.layout.item_ticket_details_step2, null);

				holder.tvTicketDetailsStep2SeatName = (TextView) convertView
						.findViewById(R.id.tvTicketDetailsStep2SeatName);
				holder.tvTicketDetailsStep2SeatNum = (TextView) convertView
						.findViewById(R.id.tvTicketDetailsStep2SeatNum);
				holder.tvTicketDetailsStep2SeatPrice = (TextView) convertView
						.findViewById(R.id.tvTicketDetailsStep2SeatPrice);
				holder.btnTicketDetailsStep2Order = (Button) convertView
						.findViewById(R.id.btnTicketDetailsStep2Order);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// ��ֵ
			holder.tvTicketDetailsStep2SeatName.setText(data.get(position)
					.get("seatName").toString());
			holder.tvTicketDetailsStep2SeatNum.setText(data.get(position)
					.get("seatNum").toString());
			holder.tvTicketDetailsStep2SeatPrice.setText(data.get(position)
					.get("seatPrice").toString());
			holder.btnTicketDetailsStep2Order
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent intent = new Intent();
							intent.setClass(TicketDetailsStep2Activity.this,
									TicketPassengerStep3Activity.class);
							intent.putExtra("train", train);
							String key = data.get(position).get("seatName").toString();
							intent.putExtra("seat", train.getSeats().get(key));
							startActivity(intent);
						}
					});

			return convertView;
		}

	}

	class ViewHolder {
		TextView tvTicketDetailsStep2SeatName;
		TextView tvTicketDetailsStep2SeatNum;
		TextView tvTicketDetailsStep2SeatPrice;
		Button btnTicketDetailsStep2Order;
	}


}
