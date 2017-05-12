package com.returnlive.app.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.returnlive.app.utils.LogUtils;


/**
 * @author 张梓彬
 * */
public class BaseActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LogUtils.Logd("onCreate执行了");
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		LogUtils.Logd("onStart执行了");

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LogUtils.Logd("onResume执行了");

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		LogUtils.Logd("onPause执行了");

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		LogUtils.Logd("onStop执行了");

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LogUtils.Logd("onDestroy执行了");

	}

	/**
	 * 页面跳转
	 */
	public void pageJumpWithData(Class<?> cls,int requestCode) {
		Intent intent = new Intent(getApplicationContext(), cls);
		startActivityForResult(intent,requestCode);
	}

	public void pageJump(Class<?> cls) {
		Intent intent = new Intent(getApplicationContext(), cls);
		startActivity(intent);
	}


}
