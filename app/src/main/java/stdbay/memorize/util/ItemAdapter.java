package stdbay.memorize.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import stdbay.memorize.R;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private LayoutInflater mInflater;
//    private String[] mTitles=null;
    private ArrayList<String> mTitles;
    private OnItemClickListener mListener;
    ItemAdapter(Context context, OnItemClickListener listener){
        this.mInflater=LayoutInflater.from(context);
        this.mListener=listener;
        this.mTitles=new ArrayList<String>();
        for (int i=0;i<20;i++){
            int index=i+1;
            mTitles.add("item"+index);
        }
    }

    //添加数据
    void addItem(String data, int position) {
        mTitles.add(position, data);
        notifyItemInserted(position);
    }
    //删除数据
    void removeItem(int position) {
//        int position = mTitles.indexOf(data);
        mTitles.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=mInflater.inflate(R.layout.list_item,parent,false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener!=null)
                    mListener.onItemClick(view,(int)view.getTag());
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(mTitles.get(position));
        holder.description.setText("这是描述");
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mTitles.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        TextView description;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=(TextView)itemView.findViewById(R.id.name);
            description=(TextView)itemView.findViewById(R.id.description);
        }
    }

    interface OnItemClickListener{
        void onItemClick(View view,int pos);
    }
}
