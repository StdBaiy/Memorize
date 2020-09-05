package stdbay.memorize.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import stdbay.memorize.R;
import stdbay.memorize.model.BaseItem;

public class BaseItemAdapter extends BaseQuickAdapter<BaseItem,BaseViewHolder> {

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public BaseItemAdapter(int layoutResId, @Nullable List<BaseItem> data) {
        super(layoutResId, data);
    }

    @SuppressLint("ResourceType")
    @Override
    protected void convert(BaseViewHolder helper, BaseItem item) {
        switch(item.getType()){
            case BaseItem.SUBJECT_TYPE:
                helper.setBackgroundColor(R.id.list_item,Color.parseColor("#aa5555"));
                break;
            case BaseItem.PROBLEM_SET_TYPE:
                helper.setBackgroundColor(R.id.list_item, Color.parseColor("#55aa55"));
                break;
        }
        helper.setText(R.id.name,item.getName());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener {


        public BaseItem item;
        public TextView name;
        public TextView description;


        public ViewHolder(View v) {
            super(v);
            name=(TextView) v.findViewById(R.id.name);
            description=(TextView)v.findViewById(R.id.description);
            v.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.add_item,
                    Menu.NONE, R.string.add);
            menu.add(Menu.NONE, R.id.delete_item,
                    Menu.NONE, R.string.delete);
            menu.add(Menu.NONE,R.id.rename_item,
                    Menu.NONE,R.string.rename);
        }
    }
}
