package stdbay.memorize.fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.permissions.PermissionChecker;
import com.luck.picture.lib.style.PictureCropParameterStyle;
import com.luck.picture.lib.style.PictureParameterStyle;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.ScreenUtils;
import com.xuexiang.xui.utils.DensityUtils;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.popupwindow.popup.XUISimplePopup;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import stdbay.memorize.R;
import stdbay.memorize.adapter.BaseItemAdapter;
import stdbay.memorize.adapter.FullyGridLayoutManager;
import stdbay.memorize.adapter.GridImageAdapter;
import stdbay.memorize.adapter.ProblemAdapter;
import stdbay.memorize.model.BaseItem;
import stdbay.memorize.model.MemorizeDB;
import stdbay.memorize.model.ProblemItem;
import stdbay.memorize.util.DeleteUtil;
import stdbay.memorize.util.GlideEngine;
import stdbay.memorize.util.MessageEvent;

//import com.xuexiang.xui.widget.progress.loading.ARCLoadingView;

public class BookFragment extends Fragment {
    private GridImageAdapter gAdapter;
    private int maxSelectNum = 8;


    private GridImageAdapter.onAddPicClickListener onAddPicClickListener=new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {


            PictureSelector.create(getActivity())
                    .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                    .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
//                    .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
//                    .isWeChatStyle(true)// 是否开启微信图片选择风格
//                    .isUseCustomCamera(cb_custom_camera.isChecked())// 是否使用自定义相机
//                    .setLanguage(language)// 设置语言，默认中文
//                    .isPageStrategy(cbPage.isChecked())// 是否开启分页策略 & 每页多少条；默认开启
                    .setPictureStyle(mPictureParameterStyle)// 动态自定义相册主题
                    .setPictureCropStyle(mCropParameterStyle)// 动态自定义裁剪主题
//                    .setPictureWindowAnimationStyle(mWindowAnimationStyle)// 自定义相册启动退出动画
//                    .setRecyclerAnimationMode(animationMode)// 列表动画效果
//                    .isWithVideoImage(true)// 图片和视频是否可以同选,只在ofAll模式下有效
//                    .isMaxSelectEnabledMask(cbEnabledMask.isChecked())// 选择数到了最大阀值列表是否启用蒙层效果
                    //.isAutomaticTitleRecyclerTop(false)// 连续点击标题栏RecyclerView是否自动回到顶部,默认true
                    //.loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
                    //.setOutputCameraPath()// 自定义相机输出目录，只针对Android Q以下，例如 Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +  File.separator + "Camera" + File.separator;
                    //.setButtonFeatures(CustomCameraView.BUTTON_STATE_BOTH)// 设置自定义相机按钮状态
                    .maxSelectNum(maxSelectNum)// 最大图片选择数量
                    .minSelectNum(1)// 最小选择数量
                    .maxVideoSelectNum(1) // 视频最大选择数量
                    //.minVideoSelectNum(1)// 视频最小选择数量
                    //.closeAndroidQChangeVideoWH(!SdkVersionUtils.checkedAndroid_Q())// 关闭在AndroidQ下获取图片或视频宽高相反自动转换
                    .imageSpanCount(3)// 每行显示个数
                    .isReturnEmpty(false)// 未选择数据时点击按钮是否可以返回
                    .closeAndroidQChangeWH(true)//如果图片有旋转角度则对换宽高,默认为true
//                    .closeAndroidQChangeVideoWH(!SdkVersionUtils.checkedAndroid_Q())// 如果视频有旋转角度则对换宽高,默认为false
                    //.isAndroidQTransform(true)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);有效,默认处理
//                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)// 设置相册Activity方向，不设置默认使用系统
                    //.bindCustomPlayVideoCallback(new MyVideoSelectedPlayCallback(getContext()))// 自定义视频播放回调控制，用户可以使用自己的视频播放界面
                    //.bindCustomPreviewCallback(new MyCustomPreviewInterfaceListener())// 自定义图片预览回调接口
                    //.bindCustomCameraInterfaceListener(new MyCustomCameraInterfaceListener())// 提供给用户的一些额外的自定义操作回调
                    .cameraFileName("123"+".jpg")    // 重命名拍照文件名、如果是相册拍照则内部会自动拼上当前时间戳防止重复，注意这个只在使用相机时可以使用，如果使用相机又开启了压缩或裁剪 需要配合压缩和裁剪文件名api
                    .renameCompressFile("123" +".jpg")// 重命名压缩文件名、 如果是多张压缩则内部会自动拼上当前时间戳防止重复
                    .renameCropFileName("123" + ".jpg")// 重命名裁剪文件名、 如果是多张裁剪则内部会自动拼上当前时间戳防止重复
//            System.currentTimeMillis()
                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
//                    .isSingleDirectReturn(true)// 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
                    .isPreviewImage(true)// 是否可预览图片
//                    .isPreviewVideo(cb_preview_video.isChecked())// 是否可预览视频
                    //.queryBooksSpecifiedFormatSuffix(PictureMimeType.ofJPEG())// 查询指定后缀格式资源
//                    .isEnablePreviewAudio(cb_preview_audio.isChecked()) // 是否可播放音频
                    .isCamera(true)// 是否显示拍照按钮
//                    .isMultipleSkipCrop(false)// 多图裁剪时是否支持跳过，默认支持
                    .isMultipleRecyclerAnimation(true)// 多图裁剪底部列表显示动画效果
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                    .imageFormat(PictureMimeType.JPEG_Q)// 拍照保存图片格式后缀,默认jpeg,Android Q使用PictureMimeType.PNG_Q
                    .isEnableCrop(true)// 是否裁剪
                    //.basicUCropConfig()//对外提供所有UCropOptions参数配制，但如果PictureSelector原本支持设置的还是会使用原有的设置
//                    .isCompress(true)// 是否压缩
//                    .compressQuality(80)// 图片压缩后输出质量 0~ 100
//                    .synOrAsy(false)//同步true或异步false 压缩 默认同步
                    //.queryBooksMaxFileSize(10)// 只查多少M以内的图片、视频、音频  单位M
                    //.compressSavePath(getPath())//压缩图片保存地址
                    //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效 注：已废弃
                    //.glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度 注：已废弃
//                    .withAspectRatio(aspect_ratio_x, aspect_ratio_y)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
//                    .hideBottomControls(!cb_hide.isChecked())// 是否显示uCrop工具栏，默认不显示
//                    .isGif(cb_isGif.isChecked())// 是否显示gif图片
                    //.isWebp(false)// 是否显示webp图片,默认显示
                    //.isBmp(false)//是否显示bmp图片,默认显示
                    .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
//                    .circleDimmedLayer(cb_crop_circular.isChecked())// 是否圆形裁剪
                    .setCropDimmedColor(ContextCompat.getColor(getContext(), R.color._333))// 设置裁剪背景色值
                    //.setCircleDimmedBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.app_color_white))// 设置圆形裁剪边框色值
                    //.setCircleStrokeWidth(3)// 设置圆形裁剪边框粗细
                    .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                    .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
//                    .isOpenClickSound(cb_voice.isChecked())// 是否开启点击声音
                    .selectionData(gAdapter.getData())// 是否传入已选图片
                    //.isDragFrame(false)// 是否可拖动裁剪框(固定)
                    //.videoMinSecond(10)// 查询多少秒以内的视频
                    //.videoMaxSecond(15)// 查询多少秒以内的视频
                    //.recordVideoSecond(10)//录制视频秒数 默认60s
                    .isPreviewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                    //.cropCompressQuality(90)// 注：已废弃 改用cutOutQuality()
                    .cutOutQuality(90)// 裁剪输出质量 默认100
                    .minimumCompressSize(100)// 小于多少kb的图片不压缩
                    //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                    //.cropImageWideHigh()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                    .rotateEnabled(false) // 裁剪是否可旋转图片
                    //.scaleEnabled(false)// 裁剪是否可放大缩小图片
                    //.videoQuality()// 视频录制质量 0 or 1
                    //.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code

                    .forResult(new MyResultCallback(gAdapter));
        }
    };



    private static List<LocalMedia> mResult=new ArrayList<>();
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
//            for (LocalMedia media : result) {
//                Log.i(TAG, "是否压缩:" + media.isCompressed());
//                Log.i(TAG, "压缩:" + media.getCompressPath());
//                Log.i(TAG, "原图:" + media.getPath());
//                Log.i(TAG, "是否裁剪:" + media.isCut());
//                Log.i(TAG, "裁剪:" + media.getCutPath());
//                Log.i(TAG, "是否开启原图:" + media.isOriginal());
//                Log.i(TAG, "原图路径:" + media.getOriginalPath());
//                Log.i(TAG, "Android Q 特有Path:" + media.getAndroidQToPath());
//                Log.i(TAG, "宽高: " + media.getWidth() + "x" + media.getHeight());
//                Log.i(TAG, "Size: " + media.getSize());
//                 TODO 可以通过PictureSelectorExternalUtils.getExifInterface();方法获取一些额外的资源信息，如旋转角度、经纬度等信息
//            }
            mResult=result;
            if (mAdapterWeakReference.get() != null) {
                mAdapterWeakReference.get().setList(result);
                mAdapterWeakReference.get().notifyDataSetChanged();
            }
        }

        @Override
        public void onCancel() {
//            Log.i(TAG, "PictureSelector Cancel");
        }
    }



//    private static final int TAKE_PHOTO=1;
//    private static final int CROP_PHOTO=2;



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

    private List<BaseItem> bookData= new ArrayList<>();
    private List<ProblemItem> problemItems=new ArrayList<>();

//    public BookFragment() {
//    }


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
        getDefaultStyle();
        if(bundle != null){
            bindViews(view);
            queryBooks();
        }
        return view;
    }

    private RecyclerView rvp;
    private ProblemAdapter pAdapter;

    private void bindViews(View view){
        initListPopup();
        notice=view.findViewById(R.id.notice);
        prevName = view.findViewById(R.id.prev_name);
        LinearLayout back = view.findViewById(R.id.back);
        title = view.findViewById(R.id.title);
        final ImageView menu = view.findViewById(R.id.menu);

        menu.setOnClickListener(view16 -> {
            isFromItem=false;
            initListPopup();
            popup.showDown(view16);
        });

//        registerForContextMenu(rv);
        memorizeDB=MemorizeDB.getInstance(getActivity());

        rv = view.findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter=new BaseItemAdapter(R.layout.list_item,bookData);
        mAdapter.isFirstOnly(false);
        mAdapter.setDuration(500);
        mAdapter.openLoadAnimation(view14 -> new Animator[]{
                ObjectAnimator.ofFloat(view14,"scaleX",1,1.07f,1)
        });

        mAdapter.setOnItemClickListener((adapter, view15, position) -> {
            prevItem=nowItem;
            nowItem=bookData.get(position);
            queryBooks();
            nowPosition=position;
        });

        mAdapter.setOnItemLongClickListener((adapter, view13, position) -> {
            modifiedPosiotion=position;
            isFromItem=true;
//                    menu.showContextMenu();
            initListPopup();
            popup.showDown(view13);
            return true;
        });
        rv.setAdapter(mAdapter);



//        @SuppressLint("InflateParams") View v= LayoutInflater.from(getContext()).inflate(R.layout.problem_item,null,false) ;
        rvp=view.findViewById(R.id.problem_rv);
        rvp.setLayoutManager(new LinearLayoutManager(getActivity()));
        pAdapter=new ProblemAdapter(R.layout.problem_item,problemItems);
        pAdapter.isFirstOnly(false);
        pAdapter.setDuration(500);
        pAdapter.openLoadAnimation(view14 -> new Animator[]{
                ObjectAnimator.ofFloat(view14,"scaleX",1,1.07f,1)
        });

        pAdapter.setOnItemChildClickListener((adapter,view2,position)->{

        });
        rvp.setAdapter(pAdapter);

        back.setOnClickListener(view12 -> onBackPressed());

        title.setOnClickListener(view1 -> rv.smoothScrollToPosition(0));
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

    private void queryProblems(){
        problemItems.clear();
        problemItems.addAll(memorizeDB.getProblemItems(nowItem.getId()));
//        int a=1;

    } 


    public void onBackPressed() {
        if(nowItem!=null){
            nowItem=prevItem;
            prevItem=memorizeDB.findBackItem(nowItem);
            queryBooks();
        }
    }

    private void showProblemItem(){
        //先获取一个布局实例,设置一些内部方法
        @SuppressLint("InflateParams") View v= LayoutInflater.from(getContext()).inflate(R.layout.add_problem_dialog,null,false) ;
//                ImageButton camera = v.findViewById(R.id.camera);
//                picture=v.findViewById(R.id.picture);
        RecyclerView recyclerView = v.findViewById(R.id.recycler);
        EditText num= v.findViewById(R.id.problem_number);
        String number = num.getText().toString();

        EditText grd= v.findViewById(R.id.grade);
        String grade = grd.getText().toString();

        EditText tolGrd= v.findViewById(R.id.total_grade);
        String totalGrade = tolGrd.getText().toString();

        EditText smy= v.findViewById(R.id.summary);
        String summary = smy.getText().toString();

        FullyGridLayoutManager manager = new FullyGridLayoutManager(getActivity(),
                4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        recyclerView.addItemDecoration(new GridSpacingItemDecoration(4,
                ScreenUtils.dip2px(getActivity(), 8), false));
        gAdapter = new GridImageAdapter(getContext(), onAddPicClickListener);
        gAdapter.setViewType(GridImageAdapter.SELECT_PIC);

        gAdapter.setSelectMax(maxSelectNum);
        recyclerView.setAdapter(gAdapter);

        gAdapter.setOnItemClickListener((v1, position) -> {
            List<LocalMedia> selectList = gAdapter.getData();
            if (selectList.size() > 0) {
                LocalMedia media = selectList.get(position);
                String mimeType = media.getMimeType();
                int mediaType = PictureMimeType.getMimeType(mimeType);
                if (mediaType == PictureConfig.TYPE_VIDEO) {// 预览视频
                    PictureSelector.create(BookFragment.this)
                            .themeStyle(R.style.picture_default_style)
//                                            .setPictureStyle(mPictureParameterStyle)// 动态自定义相册主题
                            .externalPictureVideo(TextUtils.isEmpty(media.getAndroidQToPath()) ? media.getPath() : media.getAndroidQToPath());
                    //                    case PictureConfig.TYPE_AUDIO:
//                        // 预览音频
//                        PictureSelector.create(getActivity())
//                                .externalPictureAudio(PictureMimeType.isContent(media.getPath()) ? media.getAndroidQToPath() : media.getPath());
//                        break;
                } else {// 预览图片 可自定长按保存路径
//                        PictureWindowAnimationStyle animationStyle = new PictureWindowAnimationStyle();
//                        animationStyle.activityPreviewEnterAnimation = R.anim.picture_anim_up_in;
//                        animationStyle.activityPreviewExitAnimation = R.anim.picture_anim_down_out;
                    PictureSelector.create(BookFragment.this)
                            .themeStyle(R.style.picture_default_style) // xml设置主题
//                                            .setPictureStyle(mPictureParameterStyle)// 动态自定义相册主题
                            //.setPictureWindowAnimationStyle(animationStyle)// 自定义页面启动动画
                            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)// 设置相册Activity方向，不设置默认使用系统
                            .isNotPreviewDownload(true)// 预览图片长按是否可以下载
                            //.bindCustomPlayVideoCallback(new MyVideoSelectedPlayCallback(getContext()))// 自定义播放回调控制，用户可以使用自己的视频播放界面
                            .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                            .openExternalPreview(position, selectList);
                }
            }
        });

        //用于判断本次添加是否作废
        final boolean[] isEffective = {true};
        //再把该布局加载到对话框
        //不设置positiveText的话就没有确定按钮
        //动态更改EditText的可编辑性,可以达到自由修改的效果
        MaterialDialog problemDialog = new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                .title(" ")
                .customView(v, true)
                .positiveText("确认")
                .onPositive((dialog, which) -> {
                    isEffective[0] =false;
                    memorizeDB.addProblem(nowItem.getId(), number, summary, grade, totalGrade, mResult,new MemorizeDB.callBackListener() {
                        @Override
                        public void onFinished() {
                            SnackbarUtils.Custom(title,"题目添加成功",700)
                                    .confirm().show();
                            queryBooks();
//                            queryProblems();
                        }

                        @Override
                        public void onError(Exception e) {
                            SnackbarUtils.Custom(title,"题目添加失败",700)
                                    .danger().show();
                        }
                    });
                }).build();
        problemDialog.setOnDismissListener(dialogInterface -> {
            //作废的话就删除缓存文件以减少存储
            if(isEffective[0]){
                for(LocalMedia media:mResult){
                    if(media.isCompressed()) {
                        DeleteUtil.delete(media.getAndroidQToPath());
                    }
                    if(media.isCut()) {
                        DeleteUtil.delete(media.getAndroidQToPath());
                    }
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
                            memorizeDB.reName(bookData.get(modifiedPosiotion), name, new MemorizeDB.callBackListener() {
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
                                    .onPositive((dialog, which) -> memorizeDB.deleteItem(bookData.get(modifiedPosiotion), new MemorizeDB.callBackListener() {
                                        @Override
                                        public void onFinished() {
                                            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                                                SnackbarUtils.Custom(title,"删除成功",700)
                                                        .confirm().show();
                                                queryBooks();
                                                EventBus.getDefault().post(new MessageEvent(MessageEvent.ITEM_CHANGED));
                                            });
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Objects.requireNonNull(getActivity()).runOnUiThread(() -> SnackbarUtils.Custom(title,"删除失败",700)
                                                    .danger().show());
                                            Log.d("sql", Objects.requireNonNull(e.getMessage()));
                                        }
                                    }))
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

//    @SuppressLint("RestrictedApi")
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent bookData) {
//            super.onActivityResult(requestCode, resultCode, bookData);
//            switch (requestCode) {
////                case TAKE_PHOTO:
////                    if (resultCode == Activity.RESULT_OK) {
////                        Intent intent = new Intent("com.android.camera.action.CROP");
////                        intent.setbookDataAndType(imgUri, "image/*");
////                        intent.putExtra("scale", true);
////                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
////                        getActivity().startActivityForResult(intent,CROP_PHOTO);
////                    }
////                    break;
////                case CROP_PHOTO:
////                    if (resultCode == Activity.RESULT_OK) {
////                        try {
////                            Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imgUri));
////                            picture.setImageBitmap(bitmap);
////                        } catch (FileNotFoundException e) {
////                            e.printStackTrace();
////                        }
////                    }
////                    break;
//                default:
//                    break;
//            }
//    }


    private PictureParameterStyle mPictureParameterStyle;
    private PictureCropParameterStyle mCropParameterStyle;

    private void getDefaultStyle() {
        // 相册主题
        mPictureParameterStyle = new PictureParameterStyle();
        // 是否改变状态栏字体颜色(黑白切换)
        mPictureParameterStyle.isChangeStatusBarFontColor = false;
        // 是否开启右下角已完成(0/9)风格
        mPictureParameterStyle.isOpenCompletedNumStyle = true;
        // 是否开启类似QQ相册带数字选择风格
        mPictureParameterStyle.isOpenCheckNumStyle = false;
        // 相册状态栏背景色
        mPictureParameterStyle.pictureStatusBarColor = Color.parseColor("#557755");
        // 相册列表标题栏背景色
        mPictureParameterStyle.pictureTitleBarBackgroundColor = Color.parseColor("#557755");
        // 相册父容器背景色
        mPictureParameterStyle.pictureContainerBackgroundColor = ContextCompat.getColor(getContext(), R.color._ccc);
        // 相册列表标题栏右侧上拉箭头
        mPictureParameterStyle.pictureTitleUpResId = R.drawable.picture_icon_arrow_up;
        // 相册列表标题栏右侧下拉箭头
        mPictureParameterStyle.pictureTitleDownResId = R.drawable.picture_icon_arrow_down;
        // 相册文件夹列表选中圆点
        mPictureParameterStyle.pictureFolderCheckedDotStyle = R.drawable.ic_green_dot;
        // 相册返回箭头
//        mPictureParameterStyle.pictureLeftBackIcon = R.drawable.ic_back;
        // 标题栏字体颜色
        mPictureParameterStyle.pictureTitleTextColor = ContextCompat.getColor(getContext(), R.color._ccc);
        // 相册右侧取消按钮字体颜色  废弃 改用.pictureRightDefaultTextColor和.pictureRightDefaultTextColor
        mPictureParameterStyle.pictureRightDefaultTextColor = ContextCompat.getColor(getContext(), R.color._ccc);
        // 选择相册目录背景样式
//        mPictureParameterStyle.pictureAlbumStyle = R.drawable;
        // 相册列表勾选图片样式
        mPictureParameterStyle.pictureCheckedStyle = R.drawable.check_box_selector;
        // 相册列表底部背景色
        mPictureParameterStyle.pictureBottomBgColor = ContextCompat.getColor(getContext(), R.color.picture_color_grey);
        // 已选数量圆点背景样式
        mPictureParameterStyle.pictureCheckNumBgStyle = R.drawable.ic_green_dot;
        // 相册列表底下预览文字色值(预览按钮可点击时的色值)
        mPictureParameterStyle.picturePreviewTextColor = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        // 相册列表底下不可预览文字色值(预览按钮不可点击时的色值)
        mPictureParameterStyle.pictureUnPreviewTextColor = ContextCompat.getColor(getContext(), R.color._ccc);
        // 相册列表已完成色值(已完成 可点击色值)
        mPictureParameterStyle.pictureCompleteTextColor = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        // 相册列表未完成色值(请选择 不可点击色值)
        mPictureParameterStyle.pictureUnCompleteTextColor = ContextCompat.getColor(getContext(), R.color._ccc);
        // 预览界面底部背景色
        mPictureParameterStyle.picturePreviewBottomBgColor = ContextCompat.getColor(getContext(), R.color.picture_color_grey);
        // 外部预览界面删除按钮样式
        mPictureParameterStyle.pictureExternalPreviewDeleteStyle = R.drawable.picture_icon_delete;
        // 原图按钮勾选样式  需设置.isOriginalImageControl(true); 才有效
        mPictureParameterStyle.pictureOriginalControlStyle = R.drawable.picture_original_wechat_checkbox;
        // 原图文字颜色 需设置.isOriginalImageControl(true); 才有效
        mPictureParameterStyle.pictureOriginalFontColor = ContextCompat.getColor(getContext(), R.color._ccc);
        // 外部预览界面是否显示删除按钮
        mPictureParameterStyle.pictureExternalPreviewGonePreviewDelete = true;
        // 设置NavBar Color SDK Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP有效
        mPictureParameterStyle.pictureNavBarColor = Color.parseColor("#557755");
//        // 自定义相册右侧文本内容设置
//        mPictureParameterStyle.pictureRightDefaultText = "";
//        // 自定义相册未完成文本内容
//        mPictureParameterStyle.pictureUnCompleteText = "";
//        // 自定义相册完成文本内容
//        mPictureParameterStyle.pictureCompleteText = "";
//        // 自定义相册列表不可预览文字
//        mPictureParameterStyle.pictureUnPreviewText = "";
//        // 自定义相册列表预览文字
//        mPictureParameterStyle.picturePreviewText = "";
//
//        // 自定义相册标题字体大小
//        mPictureParameterStyle.pictureTitleTextSize = 18;
//        // 自定义相册右侧文字大小
//        mPictureParameterStyle.pictureRightTextSize = 14;
//        // 自定义相册预览文字大小
//        mPictureParameterStyle.picturePreviewTextSize = 14;
//        // 自定义相册完成文字大小
//        mPictureParameterStyle.pictureCompleteTextSize = 14;
//        // 自定义原图文字大小
//        mPictureParameterStyle.pictureOriginalTextSize = 14;

        // 裁剪主题
        mCropParameterStyle = new PictureCropParameterStyle(
                Color.parseColor("#557755"),
                Color.parseColor("#557755"),
                Color.parseColor("#557755"),
                Color.parseColor("#ffffff"),
false);
    }

    private void clearCache() {
        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        if (PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //PictureFileUtils.deleteCacheDirFile(this, PictureMimeType.ofImage());
            PictureFileUtils.deleteAllCacheDirFile(getContext());
        } else {
            PermissionChecker.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE);
        }
    }

    @Override
    public void onDestroy() {
        clearCache();
        super.onDestroy();
    }
}
