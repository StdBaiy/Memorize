package stdbay.memorize.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import stdbay.memorize.R;
import stdbay.memorize.model.BaseItem;

public class BaseItemAdapter extends BaseQuickAdapter<BaseItem,BaseViewHolder> {
    public BaseItemAdapter(int layoutResId, @Nullable List<BaseItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BaseItem item) {
        helper.setText(R.id.name,item.getName());
    }


}
