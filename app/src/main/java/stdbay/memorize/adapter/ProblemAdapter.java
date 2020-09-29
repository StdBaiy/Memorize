package stdbay.memorize.adapter;

<<<<<<< HEAD
<<<<<<< HEAD
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
=======
import android.annotation.SuppressLint;

import androidx.annotation.Nullable;
>>>>>>> f71716a... 完成相册功能
=======
import android.annotation.SuppressLint;

import androidx.annotation.Nullable;
>>>>>>> f71716a... 完成相册功能

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

<<<<<<< HEAD
public class ProblemAdapter extends BaseQuickAdapter {
    public ProblemAdapter(int layoutResId, @Nullable List data) {
=======
import stdbay.memorize.R;
import stdbay.memorize.model.ProblemItem;

public class ProblemAdapter extends BaseQuickAdapter<ProblemItem, BaseViewHolder> {


    public ProblemAdapter(int layoutResId, @Nullable List<ProblemItem> data) {
<<<<<<< HEAD
>>>>>>> f71716a... 完成相册功能
=======
>>>>>>> f71716a... 完成相册功能
        super(layoutResId, data);
    }

    @SuppressLint("ResourceType")
    @Override
<<<<<<< HEAD
<<<<<<< HEAD
    protected void convert(BaseViewHolder helper, Object item) {

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }
=======
    protected void convert(BaseViewHolder helper, ProblemItem item) {
        helper.setText(R.id.name, "题目"+item.getId())
                .setText(R.id.description, "描述"+item.getCreateTime())
                .setBackgroundRes(R.id.list_item, R.drawable.gray_corner);
    }
>>>>>>> f71716a... 完成相册功能
=======
    protected void convert(BaseViewHolder helper, ProblemItem item) {
        helper.setText(R.id.name, "题目"+item.getId())
                .setText(R.id.description, "描述"+item.getCreateTime())
                .setBackgroundRes(R.id.list_item, R.drawable.gray_corner);
    }
>>>>>>> f71716a... 完成相册功能
}
