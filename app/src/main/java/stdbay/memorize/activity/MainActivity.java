package stdbay.memorize.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.animation.BaseAnimation;

import java.util.ArrayList;
import java.util.List;

import stdbay.memorize.R;
import stdbay.memorize.adapter.BaseItemAdapter;
import stdbay.memorize.db.MemorizeOpenHelper;
import stdbay.memorize.model.BaseItem;
import stdbay.memorize.model.MemorizeDB;
import stdbay.memorize.model.Subject;

public class MainActivity extends Activity{

    private BaseItem nowItem=null;
    private List<BaseItem>data=new ArrayList<BaseItem>();

    private Button addItem;

    private TextView title;

    private Spinner chooseType;
    private EditText newName;

    private RecyclerView rv;//主体view窗口

    private BaseItemAdapter mAdapter;

    private LinearLayoutManager mLayoutManager;
    private MemorizeOpenHelper dbHelper;
    private MemorizeDB memorizeDB;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String[] type = new String[1];

        data.clear();
        for(int i=0;i!=10;++i){
            BaseItem tmp=new BaseItem();
            tmp.setName("item"+(i+1));
            data.add(tmp);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        memorizeDB=MemorizeDB.getInstance(this);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv=(RecyclerView)findViewById(R.id.recycler_view);
        rv.setLayoutManager(layoutManager);
        chooseType=(Spinner)findViewById(R.id.choose_type);
        addItem=(Button)findViewById(R.id.add_item);
        newName=(EditText)findViewById(R.id.new_name);
        title=(TextView) findViewById(R.id.title);
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
                                newName.setFocusable(false);
                                query();
                            }
                        });
                        Looper.prepare();
                        Toast.makeText(MainActivity.this,"bingo!",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @Override
                    public void onError(Exception e) {
                        Looper.prepare();
                        Toast.makeText(MainActivity.this,"failed!",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                });
            }
        });

        mAdapter=new BaseItemAdapter(R.layout.list_item,data);
        mAdapter.openLoadAnimation(new BaseAnimation(){

            @Override
            public Animator[] getAnimators(View view) {

                return new Animator[]{
                        ObjectAnimator.ofFloat(view,"scaleX",1,1.1f,1),
                        ObjectAnimator.ofFloat(view,"scaleY",1,1.1f,1)
                };
            }
        });
        mAdapter.isFirstOnly(false);
        rv.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new BaseItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(MainActivity.this,"点击了"+position,Toast.LENGTH_SHORT).show();
                nowItem=data.get(position);
                query();
            }
        });

        query();
    }

    private void query(){
        if(nowItem==null){
            data.clear();
            List<Subject>tmp=memorizeDB.loadSubject(MemorizeDB.NO_FATHER);
            if(!tmp.isEmpty())
            data.addAll(tmp);
            title.setText("主页");
//            data=memorizeDB.loadSubject(MemorizeDB.NO_FATHER);
        }else{
            switch(nowItem.getType()){
                case BaseItem.SUBJECT_TYPE:
                    break;
                default:
                    break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }
}
