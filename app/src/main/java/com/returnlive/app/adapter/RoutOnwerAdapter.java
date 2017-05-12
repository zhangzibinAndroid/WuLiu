package com.returnlive.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.returnlive.app.R;
import com.returnlive.app.bean.RoutDriver;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.List;

/**
 * Created by 王建法 on 2017/3/14.
 * 实现RecyclerView适配器：
 * 1.先创建自定义ViewHolder的内部类继承ViewHolder
 * 2.自定义RecyclerView适配器继承系统的适配器<VH>：泛型为自定义的内部类ViewHolder
 */

public class RoutOnwerAdapter extends RecyclerView.Adapter<RoutOnwerAdapter.MyViewHolder> {

    private List<RoutDriver> myList;
    private Context context;

    public RoutOnwerAdapter(Context context, List<RoutDriver> list) {
        this.context = context;
        this.myList = list;
    }

    //添加
    public void addDate(int pos ,RoutDriver routObj){
        myList.add(pos,routObj);//pos:指的是在第几个位置添加
        notifyItemInserted(pos);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_rout_listview, parent, false));
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // TODO: 2017/3/15 先对整体布局进行点击监听

        holder.start.setText(myList.get(position).getStartName());
        holder.end.setText(myList.get(position).getEndName());

        holder.total.setText("线路货源  "+myList.get(position).getClunmber()+"单");

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
        return myList.size();
    }

    /**
     * 创建自定义item布局类优化
     */
    class MyViewHolder extends RecyclerView.ViewHolder {
        AutoRelativeLayout linearLayout;
        TextView start;//起始位置
        TextView end;//终点位置
        TextView total;//货源数

        public MyViewHolder(View itemView) {
            super(itemView);
            //对item整体设置监听
            linearLayout = (AutoRelativeLayout) itemView.findViewById(R.id.linear_item_rout);
            start = (TextView) itemView.findViewById(R.id.tv_rout_list_start);
            end = (TextView) itemView.findViewById(R.id.tv_rout_list_end);
            total = (TextView) itemView.findViewById(R.id.tv_rout_list_number);

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
