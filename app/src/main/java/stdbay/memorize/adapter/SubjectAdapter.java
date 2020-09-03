package stdbay.memorize.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import stdbay.memorize.R;
import stdbay.memorize.model.Subject;

public class SubjectAdapter extends BaseItemDraggableAdapter<Subject, BaseViewHolder> {

    public SubjectAdapter(int layoutResId, @Nullable List<Subject> data) {
        super(layoutResId, data);
    }

    //
    @Override
    protected void convert(BaseViewHolder helper, Subject item) {
        helper.setText(R.id.name,item.getName());
        //获取其所在位置
        //int position =helper.getLayoutPosition();
    }
}
