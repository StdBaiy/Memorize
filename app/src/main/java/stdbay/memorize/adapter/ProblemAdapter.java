package stdbay.memorize.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import stdbay.memorize.model.ProblemItem;

public class ProblemAdapter extends BaseQuickAdapter<ProblemItem, BaseViewHolder> {

    public ProblemAdapter(int layoutResId, @Nullable List<ProblemItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ProblemItem item) {

    }
}
