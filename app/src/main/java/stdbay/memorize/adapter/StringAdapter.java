package stdbay.memorize.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import stdbay.memorize.R;

public class StringAdapter extends BaseAdapter {
    private List<String>mString;
    private LayoutInflater layoutInflater;

    public StringAdapter(Context context,List<String>list){
        this.mString=list;
        this.layoutInflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mString.size();
    }

    @Override
    public Object getItem(int i) {
        return mString.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.test_item, null);
            holder = new ViewHolder();
            holder.text = convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String s= mString.get(position);
        if (s != null) {
            holder.text.setText(s);
//            holder.text.setBackgroundResource(provinceBean.getColor());
        }
        return convertView;
    }

    class ViewHolder {
        TextView text;
    }
}
