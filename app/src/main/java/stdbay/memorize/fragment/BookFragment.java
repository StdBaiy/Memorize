package stdbay.memorize.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.animation.BaseAnimation;
import com.xuexiang.xui.adapter.simple.AdapterItem;
import com.xuexiang.xui.adapter.simple.XUISimpleAdapter;
import com.xuexiang.xui.utils.DensityUtils;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.popupwindow.popup.XUISimplePopup;

import org.greenrobot.eventbus.EventBus;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import stdbay.memorize.R;
import stdbay.memorize.adapter.BaseItemAdapter;
import stdbay.memorize.model.BaseItem;
import stdbay.memorize.model.MemorizeDB;
import stdbay.memorize.util.MessageEvent;

//import com.xuexiang.xui.widget.progress.loading.ARCLoadingView;

public class BookFragment extends Fragment {

    private static final int TAKE_PHOTO=1;
    private static final int CROP_PHOTO=2;



    private XUISimplePopup popup;

    private RecyclerView rv;

    private TextView prevName;

    private TextView title;

    private TextView notice;

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
    private Uri imgUri;
    private ImageView picture;

    public BookFragment() {
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
            bindViews(view);
            query();
        }
        return view;
    }

    private void bindViews(View view){
        initListPopup();
        notice=view.findViewById(R.id.notice);
        prevName = view.findViewById(R.id.prev_name);
        LinearLayout back = view.findViewById(R.id.back);
        title = view.findViewById(R.id.title);
        final ImageView menu = view.findViewById(R.id.menu);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFromItem=false;
                initListPopup();
                popup.showDown(view);
            }
        });

        rv = view.findViewById(R.id.recycler_view);
        registerForContextMenu(rv);
        memorizeDB=MemorizeDB.getInstance(getActivity());

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter=new BaseItemAdapter(R.layout.list_item,data);
        mAdapter.isFirstOnly(false);
        mAdapter.setDuration(500);
        mAdapter.openLoadAnimation(new BaseAnimation() {
            @Override
            public Animator[] getAnimators(View view) {
                return new Animator[]{
                        ObjectAnimator.ofFloat(view,"scaleX",1,1.07f,1)
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
//                    menu.showContextMenu();
                initListPopup();
                popup.showDown(view);
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
    }

    private void query(){
        data.clear();
        data.addAll(memorizeDB.loadData(nowItem));
        if(data.isEmpty()) {
            notice.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        }
        else {
            notice.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
        }

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


    public void onBackPressed() {
        if(nowItem!=null){
            nowItem=prevItem;
            prevItem=memorizeDB.findBackItem(nowItem);
            query();
        }
    }

    private void showInput(final int type){
        String s = "";
        switch(type){
            case BaseItem.PROBLEM_TYPE:
                if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED||
                        getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    SnackbarUtils.Custom(title,"没有存取权限,请到设置中手动开启",700)
                            .danger().show();
                    return;
                }


                //先获取一个布局实例,设置一些内部方法
                @SuppressLint("InflateParams") View v= LayoutInflater.from(getContext()).inflate(R.layout.add_problem_dialog,null,false) ;
                ImageButton camera = v.findViewById(R.id.camera);
                picture=v.findViewById(R.id.picture);



                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

//                        File outputImg=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+"/Memorize"),"tmp.jpg");
//                        try {
//                            if(outputImg.exists())
//                                outputImg.delete();
//                            outputImg.createNewFile();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        imgUri=Uri.fromFile(outputImg);
//                        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);
//                        getActivity().startActivityForResult(intent,TAKE_PHOTO);

                    }
                });

                //再把该布局加载到对话框
                new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                        .title(s)
                        .customView(v,true)
                        .positiveText("确认")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @SuppressLint("ResourceType")
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                }
                            }).show();
                return;
            case BaseItem.SUBJECT_TYPE:
                s="新建科目";
                break;
            case BaseItem.PROBLEM_SET_TYPE:
                s="新建习题集";
                break;
            case RENAME:
                s="改名";
                break;
        }


        new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                .title(s)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("请输入名称", "", false,(new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull final MaterialDialog dialog, CharSequence input) {
                        String name=input.toString();
                        switch (type){
                            case BaseItem.SUBJECT_TYPE:
                            case BaseItem.PROBLEM_SET_TYPE:
                                memorizeDB.addItem(nowItem, name, type, new MemorizeDB.callBackListener() {
                                    @Override
                                    public void onFinished() {
                                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                SnackbarUtils.Custom(title,"添加成功",700)
                                                        .confirm().show();
                                                query();
                                                //用eventbus通知知识点树进行相应更改
                                                EventBus.getDefault().post(new MessageEvent(MessageEvent.ITEM_CHANGED));
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(final Exception e) {
                                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                SnackbarUtils.Custom(title,"添加失败,请检查是否有同名项",700)
                                                        .danger().show();
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
                                                SnackbarUtils.Custom(title,"改名成功",700)
                                                        .confirm().show();
                                                query();
                                                EventBus.getDefault().post(new MessageEvent(MessageEvent.ITEM_CHANGED));
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                SnackbarUtils.Custom(title,"改名失败,请检查是否有同名项",700)
                                                        .danger().show();
                                            }
                                        });
                                    }
                                });
                                break;
                        }
                    }
                }))
                .positiveText("确认")
                .negativeText("取消")
                .cancelable(true)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initListPopup() {
        String[] tmp = new String[0];
        if(isFromItem) {
            tmp=new String[]{"重命名","删除"};
        }else{
            if(nowItem==null){
                tmp=new String[]{"新建科目"};
            }
            else
                switch (nowItem.getType()) {
                    case BaseItem.SUBJECT_TYPE:
                        tmp=new String[]{"新建科目","新建习题集"};
                        break;
                    case BaseItem.PROBLEM_SET_TYPE:
                        tmp=new String[]{"新建习题集","新建习题"};
                        break;
                }
        }

        popup = new XUISimplePopup(Objects.requireNonNull(getContext()), tmp)
                .create(DensityUtils.dp2px(getContext(), 170), new XUISimplePopup.OnPopupItemClickListener() {
                    @Override
                    public void onItemClick(XUISimpleAdapter adapter, AdapterItem item, int position) {

                        switch(item.getTitle().toString()){

                            case "新建习题":
                                showInput(BaseItem.PROBLEM_TYPE);
                                break;
                            case "新建科目":
                                showInput(BaseItem.SUBJECT_TYPE);
                                break;
                            case "新建习题集":
                                showInput(BaseItem.PROBLEM_SET_TYPE);
                                break;
                            case "删除":
                                //删除需要确认操作
                                new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                                        .content("确认删除吗?")
                                        .positiveText("确认")
                                        .positiveColor(Color.parseColor("#cc5555"))
                                        .negativeText("取消")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                memorizeDB.deleteItem(data.get(modifiedPosiotion), new MemorizeDB.callBackListener() {
                                                    @Override
                                                    public void onFinished() {
                                                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                SnackbarUtils.Custom(title,"删除成功",700)
                                                                        .confirm().show();
                                                                query();
                                                                EventBus.getDefault().post(new MessageEvent(MessageEvent.ITEM_CHANGED));
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                SnackbarUtils.Custom(title,"删除失败",700)
                                                                        .danger().show();
                                                            }
                                                        });
                                                        Log.d("sql", Objects.requireNonNull(e.getMessage()));
                                                    }
                                                });
                                            }
                                        })
                                        .show();
                                break;
                            case "重命名":
                                showInput(RENAME);
                                break;
                            default:
                        }
                    }
                })
                .setHasDivider(true);
    }
    public BaseItem getNowItem(){
        return nowItem;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case TAKE_PHOTO:
                    if (resultCode == Activity.RESULT_OK) {
                        Intent intent = new Intent("com.android.camera.action.CROP");
                        intent.setDataAndType(imgUri, "image/*");
                        intent.putExtra("scale", true);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                        getActivity().startActivityForResult(intent,CROP_PHOTO);
                    }
                    break;
                case CROP_PHOTO:
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imgUri));
                            picture.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;
            }
    }
}
