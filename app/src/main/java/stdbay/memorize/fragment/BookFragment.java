package stdbay.memorize.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.animation.BaseAnimation;
//import com.xuexiang.xui.widget.progress.loading.ARCLoadingView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import stdbay.memorize.R;
import stdbay.memorize.adapter.BaseItemAdapter;
import stdbay.memorize.model.BaseItem;
import stdbay.memorize.model.MemorizeDB;

public class BookFragment extends Fragment {
//    @BindView(R.id.arc_loading)
//    ARCLoadingView mLoadingView;


    private RecyclerView rv;

    private TextView prevName;

    private TextView title;

    private  boolean isFromItem;


    private BaseItemAdapter mAdapter;

    private MemorizeDB memorizeDB;

    //当前选定的科目,习题集,知识点或习题,由于主要属性是相似的,用它们的基类来表示
    private BaseItem nowItem=null;
    //前一个选定的,用于简化返回操作
    private BaseItem prevItem=null;

    private int nowPosition=0;
    private int modifiedPosiotion=0;
    private static final int RENAME=-1;

    private List<BaseItem> data= new ArrayList<>();

    private void inputTitleDialog(final int type) {
        final EditText inputServer = new EditText(getActivity());
        inputServer.setFocusable(true);
        String hint="请输入新";
        switch (type){
            case BaseItem.SUBJECT_TYPE:
                hint+="科目";
                break;
            case BaseItem.PROBLEM_SET_TYPE:
                hint+="习题集";
                break;
            case RENAME:
                break;
            default:
        }
        hint+="名称";
        inputServer.setHint(hint);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(" ").setView(inputServer).setNegativeButton(
                getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        String name=inputServer.getText().toString();
                        switch (type){
                            case BaseItem.SUBJECT_TYPE:
                            case BaseItem.PROBLEM_SET_TYPE:
                                memorizeDB.addItem(nowItem, name, type, new MemorizeDB.callBackListener() {
                                    @Override
                                    public void onFinished() {
                                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(),"添加成功",Toast.LENGTH_SHORT).show();
                                                query();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(final Exception e) {
                                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(),"添加失败,请检查是否有同名项",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                                break;
                            case RENAME:
                                memorizeDB.reName(data.get(modifiedPosiotion), name, new MemorizeDB.callBackListener() {
                                    @Override
                                    public void onFinished() {
                                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(),"改名成功",Toast.LENGTH_SHORT).show();
                                                query();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(),"改名失败,请检查是否有同名项",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                                break;
                        }
                    }
                });
        builder.show();
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(isFromItem) {
            menu.add(0, R.id.delete_item, 0, R.string.delete);
            menu.add(0, R.id.rename_item, 0, R.string.rename);
        }else{
            if(nowItem==null){
                menu.add(0, R.id.add_subject, 0, R.string.add_subject);
            }
            else
                switch (nowItem.getType()) {
                    case BaseItem.SUBJECT_TYPE:
                        menu.add(0, R.id.add_subject, 0, R.string.add_subject);
                        menu.add(0, R.id.add_problem_set, 0, R.string.add_problem_set);
                        break;
                    case BaseItem.PROBLEM_SET_TYPE:
                        menu.add(0, R.id.add_problem_set, 0, R.string.add_problem_set);
                        menu.add(0, R.id.add_problem, 0, R.string.add_problem);
                        break;
                }
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        String s = null;
        switch(item.getItemId()){
            case R.id.add_subject:
                s="add";
                inputTitleDialog(BaseItem.SUBJECT_TYPE);
                break;
            case R.id.add_problem_set:
                inputTitleDialog(BaseItem.PROBLEM_SET_TYPE);
                break;
            case R.id.delete_item:
                memorizeDB.deleteItem(data.get(modifiedPosiotion), new MemorizeDB.callBackListener() {
                    @Override
                    public void onFinished() {
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),"删除成功",Toast.LENGTH_SHORT).show();
                                query();
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),"删除失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.d("sql", Objects.requireNonNull(e.getMessage()));
                    }
                });
                s="delete";
                break;
            case R.id.rename_item:
                s="rename";
                inputTitleDialog(RENAME);
                break;
            default:
                s="null";
        }
        Toast.makeText(getActivity(),s+modifiedPosiotion,Toast.LENGTH_SHORT).show();
        return true;
    }


    public static BookFragment getInstance(){
        Bundle bundle = new Bundle();
        BookFragment myFragment = new BookFragment();
        myFragment.setArguments(bundle);
        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.book_fragment, container, false);
        Bundle bundle = getArguments();
        if(bundle != null){
//            mLoadingView=view.findViewById(R.id.arc_loading);
//            mLoadingView.start();
            prevName = view.findViewById(R.id.prev_name);
            LinearLayout back = view.findViewById(R.id.back);
            title = view.findViewById(R.id.title);
            final ImageView menu = view.findViewById(R.id.menu);
            registerForContextMenu(menu);
            rv = view.findViewById(R.id.recycler_view);
            registerForContextMenu(rv);
            memorizeDB=MemorizeDB.getInstance(getActivity());

            rv.setLayoutManager(new LinearLayoutManager(getActivity()));

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
                @SuppressLint("ResourceType")
                @Override
                public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                    modifiedPosiotion=position;
                    isFromItem=true;
                    menu.showContextMenu();
                    return true;
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

            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isFromItem=false;
                    rv.showContextMenu();
                }
            });

            query();
        }
        return view;
    }


    private void query(){
        data.clear();
        data.addAll(memorizeDB.loadData(nowItem));
        if(nowItem==null){
            title.setText(R.string.home);
            prevName.setText("");
        }else{
            title.setText(nowItem.getName());
            if(prevItem!=null)
                prevName.setText(prevItem.getName());
            else
                prevName.setText(R.string.home);
        }
        mAdapter.notifyDataSetChanged();
        rv.scrollToPosition(nowPosition);
//        MoveToPosition(mLayoutManager,nowPosition);
    }


    private void onBackPressed() {
        if(nowItem==null) Objects.requireNonNull(getActivity()).finish();
        else{
            nowItem=prevItem;
            prevItem=memorizeDB.findBackItem(nowItem);
            query();
        }
    }
}
