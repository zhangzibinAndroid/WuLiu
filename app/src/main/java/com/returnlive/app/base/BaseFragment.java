package com.returnlive.app.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.returnlive.app.utils.LogUtils;


/**
 * @author 张梓彬
 * */
public class BaseFragment extends Fragment{
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		LogUtils.Logi("onAttach执行了");


	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LogUtils.Logi("onCreate执行了");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		LogUtils.Logi("onCreateView执行了");

		return super.onCreateView(inflater, container, savedInstanceState);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		LogUtils.Logi("onActivityCreated执行了");

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		LogUtils.Logi("onStart执行了");

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LogUtils.Logi("onResume执行了");

	}


	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		LogUtils.Logi("onPause执行了");

	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		LogUtils.Logi("onStop执行了");

	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		LogUtils.Logi("onDestroyView执行了");

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LogUtils.Logi("onDestroy执行了");

	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		LogUtils.Logi("onDetach执行了");

	}


}
