package stdbay.memorize.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.animation.BaseAnimation;

import java.util.ArrayList;
import java.util.List;

import stdbay.memorize.R;
import stdbay.memorize.adapter.BaseItemAdapter;
import stdbay.memorize.model.BaseItem;
import stdbay.memorize.model.MemorizeDB;

public class MainActivity extends Activity{
    private RecyclerView rv;

    private TextView prevName;

    private Button addItem;

    private LinearLayout back;


    private TextView title;

    private Spinner chooseType;

    private EditText newName;

    private BaseItemAdapter mAdapter;

    private MemorizeDB memorizeDB;

    private final String[] type = new String[1];

    private BaseItem nowItem=null;
    private BaseItem prevItem=null;
    private int nowPosition=0;
    private int modifiedPosiotion=0;

    private List<BaseItem>data= new ArrayList<>();


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.modify_item, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        String s;
        switch(item.getItemId()){
            case R.id.add_item:
                s="add";
                break;
            case R.id.delete_item:
                s="delete";
                break;
            case R.id.rename_item:
                s="rename";
                break;
            default:
                s="null";
        }
        Toast.makeText(MainActivity.this,s+modifiedPosiotion,Toast.LENGTH_SHORT).show();
        return true;
    }

    //主体view窗口
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prevName = findViewById(R.id.prev_name);
        addItem = findViewById(R.id.add_item);
        back = findViewById(R.id.back);
        title = findViewById(R.id.title);
        chooseType = findViewById(R.id.choose_type);
        newName = findViewById(R.id.new_name);
        rv = findViewById(R.id.recycler_view);
        registerForContextMenu(rv);
        memorizeDB=MemorizeDB.getInstance(this);

        rv.setLayoutManager(new LinearLayoutManager(this));

        mAdapter=new BaseItemAdapter(R.layout.list_item,data);
        mAdapter.isFirstOnly(false);
        mAdapter.openLoadAnimation(new BaseAnimation() {
            @Override
            public Animator[] getAnimators(View view) {
                return new Animator[]{
                    ObjectAnimator.ofFloat(view,"scaleX",1,1.05f,1)
                };
            }
        });

        mAdapter.setOnItemClickListener(new BaseItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                prevItem=nowItem;
                nowItem=data.get(position);
                query();
                nowPosition=position;
            }
        });

        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                modifiedPosiotion=position;
                return rv.showContextMenu();
            }
        });


        rv.setAdapter(mAdapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rv.smoothScrollToPosition(0);
            }
        });

        chooseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                type[0] =(String)chooseType.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=newName.getText().toString();
                memorizeDB.addItem(nowItem, name, type[0], new MemorizeDB.callBackListener() {
                    @Override
                    public void onFinished() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                newName.setText("");
                                Toast.makeText(MainActivity.this,"bingo!",Toast.LENGTH_SHORT).show();
                                query();
                            }
                        });
                    }

                    @Override
                    public void onError(final Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"failed!",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });


        query();
    }

    private void query(){
        data.clear();
        data.addAll(memorizeDB.loadData(nowItem));
        if(nowItem==null){
            title.setText("主页");
            prevName.setText("");
        }else{
            title.setText(nowItem.getName());
            if(prevItem!=null)
                prevName.setText(prevItem.getName());
            else
                prevName.setText("主页");
        }
        mAdapter.notifyDataSetChanged();
        rv.scrollToPosition(nowPosition);
//        MoveToPosition(mLayoutManager,nowPosition);
    }

    @Override
    public void onBackPressed() {
        if(nowItem==null)finish();
        else{
            nowItem=prevItem;
            prevItem=memorizeDB.findBackItem(nowItem);
            query();
        }
    }
}
