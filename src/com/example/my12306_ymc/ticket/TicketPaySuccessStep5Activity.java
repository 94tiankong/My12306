package com.example.my12306_ymc.ticket;

import com.example.my12306_ymc.MainActivity;
import com.example.my12306_ymc.R;
import com.example.my12306_ymc.bean.Order;
import com.example.my12306_ymc.utils.ZxingUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TicketPaySuccessStep5Activity extends Activity{

	
	Button btnTicketPaySuccessStep5;
	ImageView ivTicketPaySuccessStep5;
	TextView tvTicketPaySuccessStep5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_ticket_pay_success_step5);
		btnTicketPaySuccessStep5 = (Button)findViewById(R.id.btnTicketPaySuccessStep5);
		ivTicketPaySuccessStep5=(ImageView)findViewById(R.id.ImvTicketPaySuccessStep5);
		tvTicketPaySuccessStep5=(TextView)findViewById(R.id.tvTicketPaySuccessStep5);
		Order order = (Order) getIntent().getSerializableExtra("order");
		
		tvTicketPaySuccessStep5.setText("    您的订单"+order.getId()+
				"已经支付成功，可以凭此二维码办理取票业务,也可以在订单中查看相关信息及二维码");
		
		
		ZxingUtils.createQRImage(
				order.getId() + "," + order.getTrain().getTrainNo() + ","
						+ order.getTrain().getStartTrainDate() + ","
						+ order.getPassengerList(), ivTicketPaySuccessStep5,
				200, 200);
		
		btnTicketPaySuccessStep5.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(TicketPaySuccessStep5Activity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
	}
	
	
}
