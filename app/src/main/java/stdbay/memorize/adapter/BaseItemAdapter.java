package stdbay.memorize.adapter;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;
import java.util.Map;

import stdbay.memorize.R;
import stdbay.memorize.model.BaseItem;

public class BaseItemAdapter extends BaseQuickAdapter<BaseItem,BaseViewHolder> {
    public BaseItemAdapter(int layoutResId, @Nullable List<BaseItem> data) {
        super(layoutResId, data);
    }

    @SuppressLint("ResourceType")
    @Override
    protected void convert(BaseViewHolder helper, BaseItem item) {
        switch(item.getType()){
            case BaseItem.SUBJECT_TYPE:
                helper.setBackgroundRes(R.id.list_item,R.drawable.red_corner);
                break;
            case BaseItem.PROBLEM_SET_TYPE:
                helper.setBackgroundRes(R.id.list_item, R.drawable.green_corner);
                break;
        }
        Map<String, Integer>map=item.getChildrenData();
        StringBuilder description= new StringBuilder();
        for(Map.Entry<String,Integer>entry:map.entrySet()){
            description.append(entry.getValue());
            description.append("个");
            description.append(entry.getKey());
            description.append("  ");
        }
        helper.setText(R.id.name,item.getName())
                .setText(R.id.description,description);
    }
}
