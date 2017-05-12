package com.returnlive.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.returnlive.app.R;
import com.returnlive.app.bean.AreaBean;

import java.util.ArrayList;
import java.util.List;

public class PcdGoodsAdapter extends BaseAdapter {
	private Context mContext;
	private List<AreaBean> mInfos;
	List<String> list=new ArrayList<>();

	public PcdGoodsAdapter(Context context, List<AreaBean> infos) {
		this.mContext = context;
		this.mInfos = infos;
	}
	//第一个位置添加“不限”
	public void addDate(int index,AreaBean bean){
		mInfos.add(index,bean);
	}

	@Override
	public int getCount() {
		return mInfos.size();
	}
	@Override
	public AreaBean getItem(int position) {
		return mInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_goods_pcd, parent,false);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		AreaBean info = getItem(position);

		// 根据是否被选择来切换item的背景
		if(info.isChoose) {
			viewHolder.rl_name.setBackgroundResource(R.mipmap.notification_green);
		}else {
			viewHolder.rl_name.setBackgroundColor(Color.parseColor("#FF8C00"));
		}
		
		viewHolder.tv_name.setText(info.name);
		
		return convertView;
	}
	
	private class ViewHolder {
		private TextView tv_name;
		private RelativeLayout rl_name;

		public ViewHolder(View view) {
			tv_name = (TextView) view.findViewById(R.id.tv_name);
			rl_name = (RelativeLayout) view.findViewById(R.id.rl_name);
		}
	}

}