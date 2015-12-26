package com.general.flashlight;


import com.general.waps.Advertisement;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity implements View.OnClickListener
{

	private LinearLayout lLayout;
	int NumBut01 = 0;
	private Button but01;
	WindowManager.LayoutParams layoutParams;
	Camera camera;
	Parameters parameter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		camera = Camera.open();
		parameter = camera.getParameters();

		layoutParams = getWindow().getAttributes();
		lLayout = (LinearLayout) findViewById(R.id.lLayout);
		but01 = (Button) findViewById(R.id.but01);

		but01.setOnClickListener(this);
		
		// 广告 ------------
		new Advertisement(this).showAdSelectad(true, true, true);
		
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if (v.getId() == R.id.but01)
		{
			but01Achieve();
		}
	}

	public void but01Achieve()
	{

		if (NumBut01 == 0)
		{
			NumBut01 = 1;
			but01.setBackgroundResource(R.drawable.image_but01_c);// 切换按钮背景图片
			lLayout.setBackgroundColor(Color.WHITE);// 切换层背景色

			// LED
			camera.startPreview();
			parameter.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(parameter);

			layoutParams.screenBrightness = 1.0f; // 设置屏幕亮度
			getWindow().setAttributes(layoutParams);
			layoutParams = getWindow().getAttributes();

		} else
		{
			NumBut01 = 0;
			but01.setBackgroundResource(R.drawable.image_but01);//
			lLayout.setBackgroundResource(R.drawable.background); //

			// LED
			camera.startPreview();
			parameter.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(parameter);

			layoutParams.screenBrightness = 0.4f; // 设置屏幕亮度 0.4f
			getWindow().setAttributes(layoutParams);
			layoutParams = getWindow().getAttributes();
		}
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		camera.stopPreview();
		camera.release();
	}

}
