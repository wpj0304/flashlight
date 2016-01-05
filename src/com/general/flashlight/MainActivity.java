package com.general.flashlight;



import com.general.waps.Advertisement;
import com.general.waps.QuitPopAd;
import com.umeng.analytics.MobclickAgent;
import com.umeng.onlineconfig.OnlineConfigAgent;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity implements View.OnClickListener
{

	private LinearLayout lLayout;
	private int NumBut01 = 0;
	private Button but01;
	private WindowManager.LayoutParams layoutParams;
	private Camera camera;
	private Parameters parameter;
	private boolean advState = false;
	private int checkCount = 0;// 检查广告是否开启次数
	private final int CC_AD_MAXC = 3; // 检查次数
	private Handler handler;

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
		Looper looper = Looper.myLooper();
		handler = new MessageHandler(looper);
		showAd();
		
	}
	
	private void showAd()
	{
		// 友盟在线参数
		OnlineConfigAgent.getInstance().updateOnlineConfig(this);
		String showAdc = OnlineConfigAgent.getInstance().getConfigParams(this, "show_adv"); // 是否显示广告
		// 广告 ------------
		if("0".equals(showAdc))
		{
			advState = true;
		}
		if(advState)
		{
			// 悬浮窗 开关
			String floatValue = OnlineConfigAgent.getInstance().getConfigParams(this, "float_view");// 是否显示积分墙
			boolean floatState = false;
			if ("0".equals(floatValue))
			{
				floatState = true;
			}
			Advertisement.getInstance(this).showAdSelectad(true, floatState, true);
		}
		if(!advState && checkCount < CC_AD_MAXC)
		{
			// 广告未加载再次检查
			checkCount++;
			
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						Thread.sleep(2000);
					} catch (Exception e)
					{}
					 Message message = Message.obtain();
					 handler.sendMessage(message);
				}
			}).start();
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
	}
	

	@Override
	public void onClick(View v)
	{
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
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			if(advState)
			{
				QuitPopAd.getInstance().show(this);
			}
			else
			{
				return super.onKeyDown(keyCode, event);
			}
			
		}
		return false;
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		camera.stopPreview();
		camera.release();
		Advertisement.getInstance(this).close();
	}
	
	 //子类化一个Handler
    class MessageHandler extends Handler {
        public MessageHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
        	showAd();
        }
    }

}
