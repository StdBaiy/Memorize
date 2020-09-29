package stdbay.memorize.adapter;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import stdbay.memorize.R;
import stdbay.memorize.model.ProblemItem;

public class ProblemAdapter extends BaseQuickAdapter<ProblemItem, BaseViewHolder> {


    public ProblemAdapter(int layoutResId, @Nullable List<ProblemItem> data) {
        super(layoutResId, data);
    }

    @SuppressLint("ResourceType")
    @Override
    protected void convert(BaseViewHolder helper, ProblemItem item) {
        helper.setText(R.id.name, "题目"+item.getId())
                .setText(R.id.description, "描述"+item.getCreateTime())
                .setBackgroundRes(R.id.list_item, R.drawable.gray_corner);
    }
}
