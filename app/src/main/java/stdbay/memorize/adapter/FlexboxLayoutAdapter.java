package stdbay.memorize.adapter;

import android.util.SparseBooleanArray;

import androidx.annotation.NonNull;

import com.xuexiang.xui.adapter.recyclerview.BaseRecyclerAdapter;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import stdbay.memorize.R;
import stdbay.memorize.model.BaseItem;

//import com.xuexiang.xuidemo.R;

/**
 * @author xuexiang
 * @since 2019-11-23 01:32
 */
public class FlexboxLayoutAdapter extends BaseRecyclerAdapter<BaseItem> {

    private boolean mIsMultiSelectMode;
    private boolean mCancelable;

    private SparseBooleanArray mSparseArray = new SparseBooleanArray();

    public FlexboxLayoutAdapter(List<BaseItem> data) {
        super(data);
    }



    public void setIsMultiSelectMode(boolean isMultiSelectMode) {
        mIsMultiSelectMode = isMultiSelectMode;
    }

    public void setCancelable(boolean cancelable) {
        mCancelable = cancelable;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.adapter_flexbox_layout_item;
    }

    @Override
    protected void bindData(@NonNull RecyclerViewHolder holder, int position, BaseItem item) {
        holder.text(R.id.tv_tag, item.getName());
        if (mIsMultiSelectMode) {
            holder.select(R.id.tv_tag, mSparseArray.get(position));
        } else {
            holder.select(R.id.tv_tag, getSelectPosition() == position);
        }
    }

    /**
     * 选择
     *
     * @param position
     */
    public void select(int position) {
        if (mIsMultiSelectMode) {
            multiSelect(position);
        } else {
            singleSelect(position);
        }
    }

    /**
     * 多选
     *
     * @param positions
     */
    public void multiSelect(int... positions) {
        if (!mIsMultiSelectMode) {
            return;
        }
        for (int position : positions) {
            multiSelect(position);
        }
    }

    public boolean isSelected(int position){
        return mSparseArray.get(position);
    }

    /**
     * 多选
     *
     * @param position
     */
    public void multiSelect(int position) {
        if (!mIsMultiSelectMode) {
            return;
        }
        mSparseArray.append(position, !mSparseArray.get(position));
        notifyItemChanged(position);
    }

    /**
     * 单选
     *
     * @param position
     */
    public void singleSelect(int position) {
        singleSelect(position, mCancelable);
    }

    /**
     * 单选
     *
     * @param position
     * @param cancelable
     */
    public boolean singleSelect(int position, boolean cancelable) {
        if (position == getSelectPosition()) {
            if (cancelable) {
                setSelectPosition(-1);
                return true;
            }
        } else {
            setSelectPosition(position);
            return true;
        }
        return false;
    }


    /**
     * @return 获取选中的内容
     */
    public BaseItem getSelectContent() {
        return getSelectItem();
    }


    /**
     * @return 获取多选的内容
     */
    public List<BaseItem> getMultiContent() {
        List<BaseItem> list = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++) {
            if (mSparseArray.get(i)) {
                list.add(getItem(i));
            }
        }
        return list;
    }
}
