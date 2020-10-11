package stdbay.memorize.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.xuexiang.xui.adapter.listview.BaseListAdapter;

import stdbay.memorize.R;


public class ListDropDownAdapter extends BaseListAdapter<String, ListDropDownAdapter.ViewHolder> {

    public ListDropDownAdapter(Context context, String[] data) {
        super(context, data);
    }

    @Override
    protected ViewHolder newViewHolder(View convertView) {
        return new ViewHolder(convertView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.adapter_drop_down_list_item;
    }

    @Override
    protected void convert(ViewHolder holder, String item, int position) {
        holder.mText.setText(item);
        if (mSelectPosition != -1) {
            if (mSelectPosition == position) {
                holder.mText.setSelected(true);
                holder.mText.setBackgroundResource(R.color.check_bg);
            } else {
                holder.mText.setSelected(false);
                holder.mText.setBackgroundResource(R.color.picture_color_white);
            }
        }
    }

    static class ViewHolder {
        TextView mText;

        ViewHolder(View view) {
            mText=view.findViewById(R.id.text);
            mText.setGravity(Gravity.CENTER);
        }
    }
}
