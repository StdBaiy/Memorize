package stdbay.memorize.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.ScreenUtils;
import com.xuexiang.xui.utils.DensityUtils;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.popupwindow.popup.XUISimplePopup;
import com.xuexiang.xui.widget.searchview.MaterialSearchView;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import stdbay.memorize.R;
import stdbay.memorize.adapter.BaseItemAdapter;
import stdbay.memorize.adapter.FlexboxLayoutAdapter;
import stdbay.memorize.adapter.FullyGridLayoutManager;
import stdbay.memorize.adapter.GridImageAdapter;
import stdbay.memorize.adapter.ProblemAdapter;
import stdbay.memorize.model.BaseItem;
import stdbay.memorize.model.MemorizeDB;
import stdbay.memorize.model.ProblemItem;
import stdbay.memorize.util.DeleteUtil;
import stdbay.memorize.util.MessageEvent;
import stdbay.memorize.util.Util;

public class BookFragment extends Fragment{
    private GridImageAdapter gAdapter;
    private MaterialDialog problemDialog;
    private FlexboxLayoutAdapter fAdapter;
    private static List<LocalMedia> beforeMediaList;
    private static List<LocalMedia> mResult=new ArrayList<>();
    private int problemPosition;
    private View problemInflate;
    private EditText num;
    private EditText smy;
    private EditText grd;
    private EditText tolGrd;
    private ImageView  lock;
    private MaterialSearchView mSearchView;


    /**
     * 返回结果回调
     */
    private static class MyResultCallback implements OnResultCallbackListener<LocalMedia> {
        private WeakReference<GridImageAdapter> mAdapterWeakReference;

        MyResultCallback(GridImageAdapter adapter) {
            super();
            this.mAdapterWeakReference = new WeakReference<>(adapter);
        }

        @Override
        public void onResult(List<LocalMedia> result) {
//          TODO 可以通过PictureSelectorExternalUtils.getExifInterface();方法获取一些额外的资源信息，如旋转角度、经纬度等信息
            mResult=result;
            if (mAdapterWeakReference.get() != null) {
                mAdapterWeakReference.get().setList(result);
                mAdapterWeakReference.get().notifyDataSetChanged();
            }

//            for(LocalMedia media:beforeMediaList){
//                boolean in=false;
//                for(int i=0;i<result.size();++i){
//                    if(media.getRealPath().equals(beforeMediaList.get(i).getRealPath())){
//                        in=true;
//                        break;
//                    }
//                }
//                if(!in)
//                    DeleteUtil.delete(media);
//            }
        }

        @Override
        public void onCancel() {
        }
    }

    private XUISimplePopup popup;

    private RecyclerView rv;

    private TextView prevName;
    private TextView title;
    private TextView notice;

    private  boolean isFromItem;
    private boolean isFromProblem;

    private BaseItemAdapter mAdapter;

    private MemorizeDB memorizeDB;

    //当前选定的科目,习题集,知识点或习题,由于主要属性是相似的,用它们的基类来表示
    private BaseItem nowItem=null;
    //前一个选定的,用于简化返回操作
    private BaseItem prevItem=null;

    private int nowPosition=0;
    private int itemPosition=0;
    private static final int RENAME=-1;

    private List<BaseItem> bookData= new ArrayList<>();
    private List<ProblemItem> problemItems=new ArrayList<>();

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
            initViews(view);
            queryBooks();
        }
        return view;
    }

    private RecyclerView rvp;
    private ProblemAdapter pAdapter;

    @SuppressLint({"ClickableViewAccessibility", "InflateParams"})
    private void initViews(View view){
        memorizeDB=MemorizeDB.getInstance(getActivity());
        initListPopup();
//        gAdapter = initGAdapter();
        notice=view.findViewById(R.id.notice);
        prevName = view.findViewById(R.id.prev_name);
        LinearLayout back = view.findViewById(R.id.back);
        title = view.findViewById(R.id.title);
        final LinearLayout menu = view.findViewById(R.id.menu);


        gAdapter= Util.initGAdapter(getActivity());
        gAdapter.setmOnAddPicClickListener(Util.initOnAddPicListener(gAdapter,getActivity(),new MyResultCallback(gAdapter)));

        problemInflate= LayoutInflater.from(getContext()).inflate(R.layout.problem_item,null,false) ;

        RecyclerView recyclerView = problemInflate.findViewById(R.id.recycler_show);
        recyclerView.setAdapter(gAdapter);
        FullyGridLayoutManager fManager = new FullyGridLayoutManager(getActivity(),
                3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(fManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3,
                ScreenUtils.dip2px(Objects.requireNonNull(getActivity()), 8), false));
        num= problemInflate.findViewById(R.id.problem_number_show);
        grd= problemInflate.findViewById(R.id.grade_show);
        tolGrd= problemInflate.findViewById(R.id.total_grade_show);
        smy= problemInflate.findViewById(R.id.summary_show);
        lock= problemInflate.findViewById(R.id.lock);
        mSearchView=problemInflate.findViewById(R.id.search_view);

        LinearLayout l1=problemInflate.findViewById(R.id.linear1);
        LinearLayout l2=problemInflate.findViewById(R.id.linear2);

        l1.setOnClickListener(view1 -> {
            if(lock.isSelected()) {
                MessageEvent.selectedknowledges = fAdapter.getMultiContent();
                EventBus.getDefault().post(new MessageEvent(MessageEvent.ITEM_CHANGED));
                EventBus.getDefault().post(new MessageEvent(MessageEvent.SELECT_KNOWLEDGE));
                problemDialog.dismiss();
            }else{
                SnackbarUtils.Custom(title,"解锁后才能修改",700)
                        .confirm().show();
            }
        });

        l2.setOnClickListener(view1 -> {
            if (lock.isSelected()) {
                beforeMediaList=gAdapter.getData();
                gAdapter.callOnAddPicClick();
            }
            else {
                SnackbarUtils.Custom(title,"解锁后才能修改",700)
                        .confirm().show();
            }
        });

        RecyclerView knowledgeRV = problemInflate.findViewById(R.id.knowledge_items);
        knowledgeRV.setLayoutManager(Util.getFlexboxLayoutManager(getContext()));
        knowledgeRV.setItemAnimator(null);
        knowledgeRV.setAdapter(fAdapter);

        fAdapter = new FlexboxLayoutAdapter(new ArrayList<>());
        fAdapter.setIsMultiSelectMode(true);
        fAdapter.setCancelable(false);
        fAdapter.setOnItemClickListener((itemView, item, position) -> {
            if(lock.isSelected()) {
                fAdapter.select(position);
//            XToastUtils.toast("选中的内容：" + StringUtils.listToString(fAdapter.getMultiContent(), ","));
            }else{
                MessageEvent.findKnowledge=fAdapter.getData().get(position);
                EventBus.getDefault().post(new MessageEvent(MessageEvent.FIND_IN_TREE));
                problemDialog.dismiss();
//                Toast.makeText(getContext(), fAdapter.getData().get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
        knowledgeRV.setAdapter(fAdapter);

        mSearchView.setVoiceSearch(false);
        mSearchView.setEllipsize(true);
        mSearchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SnackbarUtils.Long(mSearchView, "Query: " + query).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });
        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        mSearchView.setSubmitOnClick(true);
//        mSearchView.showSearch(false);


        menu.setOnClickListener(view16 -> {
            isFromItem=false;
            isFromProblem=false;
            initListPopup();
            popup.showDown(view16);
        });

        rv = view.findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter=new BaseItemAdapter(R.layout.list_item,bookData);
        mAdapter.isFirstOnly(false);
        mAdapter.setDuration(500);
        mAdapter.openLoadAnimation(view14 -> new Animator[]{
                ObjectAnimator.ofFloat(view14,"scaleX",1,1.08f,1)
        });

        mAdapter.setOnItemClickListener((adapter, view15, position) -> {
            prevItem=nowItem;
            nowItem=bookData.get(position);
            queryBooks();
            nowPosition=position;
        });

        mAdapter.setOnItemLongClickListener((adapter, view13, position) -> {
            itemPosition=position;
            isFromItem=true;
            isFromProblem=false;

            initListPopup();
            popup.showDown(view13);
            return true;
        });
        rv.setAdapter(mAdapter);

        rvp=view.findViewById(R.id.problem_rv);

        rvp.setLayoutManager(new LinearLayoutManager(getActivity()));

        pAdapter=new ProblemAdapter(R.layout.list_item,problemItems);
        pAdapter.isFirstOnly(false);
        pAdapter.setDuration(500);
        pAdapter.openLoadAnimation(view14 -> new Animator[]{
                ObjectAnimator.ofFloat(view14,"scaleX",1,1.08f,1)
        });

        pAdapter.setPreLoadNumber(10);
        pAdapter.setOnItemClickListener((adapter, view17, position) -> {
            if(problemDialog!=null && problemDialog.isShowing())
                return;
            problemPosition=position;
            ProblemItem item= problemItems.get(position);

            num.setEnabled(false);
            grd.setEnabled(false);
            tolGrd.setEnabled(false);
            smy.setEnabled(false);

            gAdapter.setViewType(GridImageAdapter.VIEW_PIC);
            gAdapter.setList(item.getPictures());
            gAdapter.notifyDataSetChanged();

            num.setText(item.getNumber());
            grd.setText(item.getGrade());
            tolGrd.setText(item.getTotalGrade());
            smy.setText(item.getSummary());
            fAdapter.resetDataSource(item.getKnowledges());
            //默认选中所有
            for (int i=0;i<item.getKnowledges().size();++i){
                if(!fAdapter.isSelected(i))
                    fAdapter.select(i);
            }
            fAdapter.notifyDataSetChanged();
            //锁用于禁止更改内容
            lock.setVisibility(View.VISIBLE);
            lock.setSelected(false);
            lock.setOnClickListener(view22 -> {
                if (view22.isSelected()) {
                    view22.setSelected(false);
                    num.setEnabled(false);
                    grd.setEnabled(false);
                    tolGrd.setEnabled(false);
                    smy.setEnabled(false);
                    memorizeDB.changeProblem(item.getId(), num.getText().toString(), smy.getText().toString(),
                            grd.getText().toString(), tolGrd.getText().toString(), gAdapter.getData(), fAdapter.getMultiContent(), new MemorizeDB.callBackListener() {
                                @Override
                                public void onFinished() {
                                    SnackbarUtils.Custom(title,"修改成功",700)
                                            .confirm().show();
                                }

                                @Override
                                public void onError(Exception e) {
                                    SnackbarUtils.Custom(title,"修改失败,请检查是否有同名项",700)
                                            .danger().show();
                                }
                            });
                    gAdapter.setViewType(GridImageAdapter.VIEW_PIC);

                    //动态修改内容
                    item.setNumber(num.getText().toString());
                    item.setSummary(smy.getText().toString());
                    item.setGrade(grd.getText().toString());
                    item.setTotalGrade(tolGrd.getText().toString());
                    item.setKnowledges(fAdapter.getMultiContent());
                    int selectedNum=fAdapter.getMultiContent().size();
                    fAdapter.resetDataSource(fAdapter.getMultiContent());
                    for(int i=0;i<selectedNum;++i){
                        if(!fAdapter.isSelected(i))
                            fAdapter.select(i);
                    }
                    fAdapter.notifyDataSetChanged();
//                    for(LocalMedia media: beforeList[0]){
//                        boolean in=true;
//                        for(int i=0;i<gAdapter.getData().size();++i){
//                            if(media.getRealPath().equals(gAdapter.getData().get(i).getRealPath())) {
//                                in = false;
//                                break;
//                            }
//                        }
//                        if(in)
//                            DeleteUtil.delete(media);
//                    }

                    item.setPictures(gAdapter.getData());
                } else {
                    view22.setSelected(true);
                    num.setEnabled(true);
                    grd.setEnabled(true);
                    tolGrd.setEnabled(true);
                    smy.setEnabled(true);
                    gAdapter.setViewType(GridImageAdapter.SELECT_PIC);
                }
                gAdapter.notifyDataSetChanged();
            });

            problemDialog = new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                    .backgroundColorRes(R.color.problem_blue)
                    .positiveColorRes(R.color._ccc)
                    .customView(problemInflate, true)
                    .build();
            problemDialog.show();
        });


        pAdapter.setOnItemLongClickListener((adapter, view13, position) -> {

            problemPosition=position;
            isFromItem=false;
            isFromProblem=true;

            initListPopup();
            popup.showDown(view13);
            return true;
        });

        rvp.setAdapter(pAdapter);


        back.setOnClickListener(view12 -> onBackPressed());
        title.setOnClickListener(view1 -> {
            rv.smoothScrollToPosition(0);
            rvp.smoothScrollToPosition(0);
        });
    }

    private void queryBooks(){
        //对于习题集和科目要分开查询
        if(nowItem==null || nowItem.getType()==BaseItem.SUBJECT_TYPE){
            bookData.clear();
            bookData.addAll(memorizeDB.loadData(nowItem));
            rvp.setVisibility(View.GONE);
            if (bookData.isEmpty()) {
                notice.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);

            } else {
                notice.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
            }

            mAdapter.notifyDataSetChanged();
            rv.scrollToPosition(nowPosition);
        }
        else if(nowItem.getType()==BaseItem.PROBLEM_SET_TYPE) {
            problemItems.clear();
            problemItems.addAll(memorizeDB.getProblemItems(nowItem.getId()));
            //设置可见性
            rv.setVisibility(View.GONE);
            if(problemItems.isEmpty()){
                notice.setVisibility(View.VISIBLE);
                rvp.setVisibility(View.GONE);
            }else{
                notice.setVisibility(View.GONE);
                rvp.setVisibility(View.VISIBLE);
            }
            pAdapter.notifyDataSetChanged();
            rvp.scrollToPosition(0);
        }

        if (nowItem == null) {
            title.setText(R.string.home);
            prevName.setText("");
        } else {
            title.setText(nowItem.getName());
            if (prevItem != null)
                prevName.setText(prevItem.getName());
            else
                prevName.setText(R.string.home);
        }
//        MoveToPosition(mLayoutManager,nowPosition);
    }


    public void onBackPressed() {
        if(nowItem!=null){
            nowItem=prevItem;
            prevItem=memorizeDB.findBackItem(nowItem);
            queryBooks();
        }
    }

    //显示添加界面
    private void showProblemItem(){
        //先获取一个布局实例,设置一些内部方法
        smy.setText("");
        grd.setText("");
        tolGrd.setText("");
        num.setText("");

        smy.setEnabled(true);
        grd.setEnabled(true);
        tolGrd.setEnabled(true);
        num.setEnabled(true);

        gAdapter.setViewType(GridImageAdapter.SELECT_PIC);
        gAdapter.setList(new ArrayList<>());
        gAdapter.notifyDataSetChanged();

        MessageEvent.selectedknowledges=new ArrayList<>();
        fAdapter.resetDataSource(new ArrayList<>());
        fAdapter.notifyDataSetChanged();

        //隐藏锁图标
        lock.setVisibility(View.GONE);
        lock.setSelected(true);

        //用于判断本次添加是否作废
        final boolean[] isEffective = {true};
        //再把该布局加载到对话框
        //不设置positiveText的话就没有确定按钮
        //动态更改EditText的可编辑性,可以达到自由修改的效果
        problemDialog = new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                .backgroundColorRes(R.color.problem_blue)
                .positiveColorRes(R.color._ccc)
                .customView(problemInflate, true)
                .positiveText("添加")
                .onPositive((dialog, which) -> {
                    isEffective[0] =false;
                    String number = num.getText().toString();
                    String grade = grd.getText().toString();
                    String totalGrade = tolGrd.getText().toString();
                    String summary = smy.getText().toString();
                    List<BaseItem>list=fAdapter.getMultiContent();
                    memorizeDB.addProblem(nowItem.getId(), number, summary, grade, totalGrade,gAdapter.getData(), list,new MemorizeDB.callBackListener() {
                        @Override
                        public void onError(Exception e) {
                            SnackbarUtils.Custom(title,"题目添加失败",700)
                                    .danger().show();
                        }

                        @Override
                        public void onFinished() {
                            SnackbarUtils.Custom(title, "题目添加成功", 700)
                                    .confirm().show();
                            queryBooks();
                        }
                    });
                }).build();

        problemDialog.setOnDismissListener(dialogInterface -> {
            //作废的话就删除缓存文件以减少存储
            if(isEffective[0]){
                for(LocalMedia media:mResult){
                    DeleteUtil.delete(media);
                }
            }
        });
        problemDialog.show();
    }

    private void showInput(final int type){
        String s = "";
        switch(type){
            case BaseItem.PROBLEM_TYPE:
                showProblemItem();
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
                .input("请输入名称", "", false,((dialog, input) -> {
                    String name=input.toString();
                    switch (type){
                        case BaseItem.SUBJECT_TYPE:
                        case BaseItem.PROBLEM_SET_TYPE:
                            memorizeDB.addItem(nowItem, name, type, new MemorizeDB.callBackListener() {
                                @Override
                                public void onFinished() {
                                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                                        SnackbarUtils.Custom(title,"添加成功",700)
                                                .confirm().show();
                                        queryBooks();
                                        //用eventbus通知知识点树进行相应更改
                                        EventBus.getDefault().post(new MessageEvent(MessageEvent.ITEM_CHANGED));
                                    });
                                }

                                @Override
                                public void onError(final Exception e) {
                                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> SnackbarUtils.Custom(title,"添加失败,请检查是否有同名项",700)
                                            .danger().show());
                                }
                            });
                            break;
                        case RENAME:
                            memorizeDB.reName(bookData.get(itemPosition), name, new MemorizeDB.callBackListener() {
                                @Override
                                public void onFinished() {
                                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                                        SnackbarUtils.Custom(title,"改名成功",700)
                                                .confirm().show();
                                        queryBooks();
                                        EventBus.getDefault().post(new MessageEvent(MessageEvent.ITEM_CHANGED));
                                    });
                                }

                                @Override
                                public void onError(Exception e) {
                                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> SnackbarUtils.Custom(title,"改名失败,请检查是否有同名项",700)
                                            .danger().show());
                                }
                            });
                            break;
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
        if(isFromProblem){
            tmp=new String[]{"删除"};
        }
        else if(isFromItem) {
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
                        tmp=new String[]{"新建习题"};
                        break;
                }
        }

        popup = new XUISimplePopup(Objects.requireNonNull(getContext()), tmp)
                .create(DensityUtils.dp2px(getContext(), 170), (adapter, item, position) -> {

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
                                    .onPositive((dialog, which) -> {
                                        int id=0,type=0;
                                        if(isFromProblem){
                                            id=problemItems.get(problemPosition).getId();
                                            type=BaseItem.PROBLEM_TYPE;
                                            //删除本地文件
                                            for(LocalMedia media:problemItems.get(problemPosition).getPictures()){
                                                DeleteUtil.delete(media);
                                            }
                                        }
                                        else if(isFromItem) {
                                            id=bookData.get(itemPosition).getId();
                                            type=bookData.get(itemPosition).getType();
                                        }

                                        List<ProblemItem> problemItemList = new ArrayList<>();
                                        if(type==BaseItem.SUBJECT_TYPE){
                                            List<BaseItem>probSets = memorizeDB.loadData(bookData.get(itemPosition));
                                            for(int i=0;i<probSets.size();++i){
                                                problemItemList.addAll(memorizeDB.getProblemItems(probSets.get(i).getId()));
                                            }
                                        }
                                        else if(type == BaseItem.PROBLEM_SET_TYPE)
                                            problemItemList= memorizeDB.getProblemItems(id);
                                        int finalType = type;
                                        List<ProblemItem> finalProblemItemList = problemItemList;
                                        memorizeDB.deleteItem(id,type, new MemorizeDB.callBackListener() {
                                            @Override
                                            public void onFinished() {
                                                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                                                    SnackbarUtils.Custom(title, "删除成功", 700)
                                                            .confirm().show();
                                                    if(finalType == BaseItem.PROBLEM_SET_TYPE
                                                    ||finalType==BaseItem.SUBJECT_TYPE){
                                                        for(int i = 0; i< finalProblemItemList.size(); ++i){
                                                            for(LocalMedia media: finalProblemItemList.get(i).getPictures()){
                                                                DeleteUtil.delete(media);
                                                            }
                                                        }
                                                    }

                                                    queryBooks();
                                                    EventBus.getDefault().post(new MessageEvent(MessageEvent.ITEM_CHANGED));
                                                });
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                Objects.requireNonNull(getActivity()).runOnUiThread(() -> SnackbarUtils.Custom(title, "删除失败", 700)
                                                        .danger().show());
                                                Log.d("sql", Objects.requireNonNull(e.getMessage()));
                                            }
                                        }
                                        
                                        );
                                    })
                                    
                                    .show();
                            break;
                        case "重命名":
                            showInput(RENAME);
                            break;
                        default:
                    }
                })
                .setHasDivider(true);
    }
    public BaseItem getNowItem(){
        return nowItem;
    }

    private void clearCache() {
        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        if (PermissionChecker.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //PictureFileUtils.deleteCacheDirFile(this, PictureMimeType.ofImage());
            PictureFileUtils.deleteAllCacheDirFile(Objects.requireNonNull(getActivity()));
        } else {
            PermissionChecker.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
        }
    }

    @Override
    public void onDestroy() {
        if (mSearchView.isSearchOpen()) {
            mSearchView.closeSearch();
        }
        clearCache();
        super.onDestroy();
    }
    
    public void updateKnowledgeItems(){
        problemDialog.show();
        List<BaseItem>list = MessageEvent.selectedknowledges;
        fAdapter.resetDataSource(list);
        //选中
        for (int i=0;i<list.size();++i){
            if(!fAdapter.isSelected(i))
                fAdapter.select(i);
        }
        fAdapter.notifyDataSetChanged();
        //刷新知识点树,让选中的清空
        MessageEvent.selectedknowledges.clear();
        EventBus.getDefault().post(new MessageEvent(MessageEvent.ITEM_CHANGED));
    }

}
