package com.returnlive.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.returnlive.app.R;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.List;

/**
 * Created by 王建法 on 2017/3/14.
 * 实现RecyclerView适配器：
 * 1.先创建自定义ViewHolder的内部类继承ViewHolder
 * 2.自定义RecyclerView适配器继承系统的适配器<VH>：泛型为自定义的内部类ViewHolder
 */

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.MyViewHolder> {

    private List<String> list;
    private Context context;

    public GoodsAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_goods_listview, parent, false));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // TODO: 2017/3/15 先对整体布局进行点击监听

        holder.name.setText("司机版-"+list.get(position));

        if (myListener != null) {
            //点击监听
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myListener.onNewItemClick(v, position);
                }
            });
            //长按监听
            holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    myListener.OnNewItemLongClick(v, position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 创建自定义item布局类优化
     */
    class MyViewHolder extends RecyclerView.ViewHolder {
        AutoLinearLayout linearLayout;
        ImageView icon;//头像
        TextView releaseTime;//发布时间
        TextView sendNumber;//发货数量
        TextView dealNumber;//交易数量
        ImageView callPhone;//头像
        TextView name;//姓名
        TextView start;//起始位置
        TextView end;//终点位置
        TextView remark;//备注说明
        TextView carType;//货车类型
        TextView weight;//货物的重量
        TextView traveltime;//装货的时间

        public MyViewHolder(View itemView) {
            super(itemView);
            //对item整体设置监听
            linearLayout = (AutoLinearLayout) itemView.findViewById(R.id.linear_item_goods);
            icon = (ImageView) itemView.findViewById(R.id.img_goods_icon);
            releaseTime = (TextView) itemView.findViewById(R.id.tv_goods_release_time);
            sendNumber = (TextView) itemView.findViewById(R.id.tv_goods_send_number);
            dealNumber = (TextView) itemView.findViewById(R.id.tv_deal_number);
            callPhone = (ImageView) itemView.findViewById(R.id.img_goods_callphone);
            name = (TextView) itemView.findViewById(R.id.tv_goods_name);
            start = (TextView) itemView.findViewById(R.id.tv_goods_start);
            end = (TextView) itemView.findViewById(R.id.tv_goods_end);
            remark = (TextView) itemView.findViewById(R.id.tv_goods_remark);
            carType = (TextView) itemView.findViewById(R.id.tv_goods_cartype);
            weight = (TextView) itemView.findViewById(R.id.tv_goods_weight);
            traveltime = (TextView) itemView.findViewById(R.id.tv_goods_traveltime);
        }
    }
    /**
     * Recycler的适配器中没有Item的监听方法，因此需要自己定义监听接口去实现监听效果
     * 点击监听接口
     */
    public interface onNewItemClickListener {
        void onNewItemClick(View view, int postion); //点击时的方法

        void OnNewItemLongClick(View view, int postion);
    }

    //声明监听接口对象
    public onNewItemClickListener myListener;

    //对外提供一个监听方法
    public void setonNewItemClickListener(onNewItemClickListener listener) {
        this.myListener = listener;
    }
}
