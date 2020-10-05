package stdbay.memorize.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;

import java.util.List;
import java.util.Objects;

import stdbay.memorize.R;
import stdbay.memorize.adapter.GridImageAdapter;

//为图片适配器提供支持
public class Util {

    public static GridImageAdapter initGAdapter(Activity activity){
        GridImageAdapter gAdapter = new GridImageAdapter(activity);
        gAdapter.setViewType(GridImageAdapter.VIEW_PIC);
        gAdapter.setSelectMax();
        gAdapter.setOnItemClickListener((v1, position1) -> {
            List<LocalMedia> selectList = gAdapter.getData();
            if (selectList.size() > 0) {
                if(position1>=selectList.size())return;
                LocalMedia media = selectList.get(position1);
                String mimeType = media.getMimeType();
                int mediaType = PictureMimeType.getMimeType(mimeType);
                if (mediaType == PictureConfig.TYPE_VIDEO) {// 预览视频
                    PictureSelector.create(activity)
//                            .themeStyle(R.style.picture_default_style)
                            .setPictureStyle(PictureStyle.getmPictureParameterStyle())// 动态自定义相册主题
                            .externalPictureVideo(TextUtils.isEmpty(media.getAndroidQToPath()) ? media.getPath() : media.getAndroidQToPath());
                } else {// 预览图片 可自定长按保存路径
//                        PictureWindowAnimationStyle animationStyle = new PictureWindowAnimationStyle();
//                        animationStyle.activityPreviewEnterAnimation = R.anim.picture_anim_up_in;
//                        animationStyle.activityPreviewExitAnimation = R.anim.picture_anim_down_out;
                    PictureSelector.create(activity)
//                            .themeStyle(R.style.picture_default_style) // xml设置主题
                            .setPictureStyle(PictureStyle.getmPictureParameterStyle())// 动态自定义相册主题
                            //.setPictureWindowAnimationStyle(animationStyle)// 自定义页面启动动画
                            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)// 设置相册Activity方向，不设置默认使用系统
                            .isNotPreviewDownload(true)// 预览图片长按是否可以下载
                            //.bindCustomPlayVideoCallback(new MyVideoSelectedPlayCallback(getContext()))// 自定义播放回调控制，用户可以使用自己的视频播放界面
                            .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                            .openExternalPreview(position1, selectList);
                }
            }
        });
        return gAdapter;
    }

    public static GridImageAdapter.onAddPicClickListener initOnAddPicListener(GridImageAdapter gAdapter, Activity activity, OnResultCallbackListener listener){
        return () -> PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
//                    .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
//                    .isWeChatStyle(true)// 是否开启微信图片选择风格
//                    .isUseCustomCamera(cb_custom_camera.isChecked())// 是否使用自定义相机
//                    .setLanguage(language)// 设置语言，默认中文
                .isPageStrategy(true)// 是否开启分页策略 & 每页多少条；默认开启
                .setPictureStyle(PictureStyle.getmPictureParameterStyle())// 动态自定义相册主题
                .setPictureCropStyle(PictureStyle.getmCropParameterStyle())// 动态自定义裁剪主题
//                    .setPictureWindowAnimationStyle(mWindowAnimationStyle)// 自定义相册启动退出动画
//                    .setRecyclerAnimationMode(animationMode)// 列表动画效果
                .isWithVideoImage(true)// 图片和视频是否可以同选,只在ofAll模式下有效
//                    .isMaxSelectEnabledMask(cbEnabledMask.isChecked())// 选择数到了最大阀值列表是否启用蒙层效果
                //.isAutomaticTitleRecyclerTop(false)// 连续点击标题栏RecyclerView是否自动回到顶部,默认true
//                .loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
                //.setOutputCameraPath()// 自定义相机输出目录，只针对Android Q以下，例如 Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +  File.separator + "Camera" + File.separator;
                //.setButtonFeatures(CustomCameraView.BUTTON_STATE_BOTH)// 设置自定义相机按钮状态
                .maxSelectNum(9)// 最大图片选择数量
                .minSelectNum(0)// 最小选择数量
                .maxVideoSelectNum(9) // 视频最大选择数量
                .minVideoSelectNum(0)// 视频最小选择数量
                .closeAndroidQChangeVideoWH(true)// 关闭在AndroidQ下获取图片或视频宽高相反自动转换
                .imageSpanCount(3)// 每行显示个数
                .isReturnEmpty(false)// 未选择数据时点击按钮是否可以返回
                .closeAndroidQChangeWH(true)//如果图片有旋转角度则对换宽高,默认为true
//                    .closeAndroidQChangeVideoWH(false)// 如果视频有旋转角度则对换宽高,默认为false
                .isAndroidQTransform(true)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);有效,默认处理
//                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)// 设置相册Activity方向，不设置默认使用系统
                //.bindCustomPlayVideoCallback(new MyVideoSelectedPlayCallback(getContext()))// 自定义视频播放回调控制，用户可以使用自己的视频播放界面
                //.bindCustomPreviewCallback(new MyCustomPreviewInterfaceListener())// 自定义图片预览回调接口
                //.bindCustomCameraInterfaceListener(new MyCustomCameraInterfaceListener())// 提供给用户的一些额外的自定义操作回调
                .cameraFileName("origin"+".jpg")    // 重命名拍照文件名、如果是相册拍照则内部会自动拼上当前时间戳防止重复，注意这个只在使用相机时可以使用，如果使用相机又开启了压缩或裁剪 需要配合压缩和裁剪文件名api
                .renameCompressFile("compress" +".jpg")// 重命名压缩文件名、 如果是多张压缩则内部会自动拼上当前时间戳防止重复
                .renameCropFileName("cut" + ".jpg")// 重命名裁剪文件名、 如果是多张裁剪则内部会自动拼上当前时间戳防止重复
//            System.currentTimeMillis()
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
//                    .isSingleDirectReturn(true)// 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
                .isPreviewImage(true)// 是否可预览图片
                .isPreviewVideo(true)// 是否可预览视频
                //.queryBooksSpecifiedFormatSuffix(PictureMimeType.ofJPEG())// 查询指定后缀格式资源
//                    .isEnablePreviewAudio(true) // 是否可播放音频
                .isCamera(true)// 是否显示拍照按钮
                .isMultipleSkipCrop(true)// 多图裁剪时是否支持跳过，默认支持
                .isMultipleRecyclerAnimation(true)// 多图裁剪底部列表显示动画效果
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .imageFormat(PictureMimeType.JPEG_Q)// 拍照保存图片格式后缀,默认jpeg,Android Q使用PictureMimeType.PNG_Q
                .isEnableCrop(true)// 是否裁剪
//                .basicUCropConfig()//对外提供所有UCropOptions参数配制，但如果PictureSelector原本支持设置的还是会使用原有的设置
//                .isCompress(true)// 是否压缩
//                .compressQuality(80)// 图片压缩后输出质量 0~ 100
//                    .synOrAsy(false)//同步true或异步false 压缩 默认同步
                //.queryBooksMaxFileSize(10)// 只查多少M以内的图片、视频、音频  单位M
                //.compressSavePath(getPath())//压缩图片保存地址
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效 注：已废弃
                //.glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度 注：已废弃
//                    .withAspectRatio(aspect_ratio_x, aspect_ratio_y)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
                .isGif(true)// 是否显示gif图片
                //.isWebp(false)// 是否显示webp图片,默认显示
                //.isBmp(false)//是否显示bmp图片,默认显示
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
//                    .circleDimmedLayer(cb_crop_circular.isChecked())// 是否圆形裁剪
                .setCropDimmedColor(ContextCompat.getColor(Objects.requireNonNull(activity), R.color._333))// 设置裁剪背景色值
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
                .cutOutQuality(80)// 裁剪输出质量 默认100
//                .minimumCompressSize(0)// 小于多少kb的图片不压缩
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.cropImageWideHigh()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                .rotateEnabled(false) // 裁剪是否可旋转图片
                //.scaleEnabled(false)// 裁剪是否可放大缩小图片
                .videoQuality(1)// 视频录制质量 0 or 1
                //.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                .forResult(listener);
    }
    public static FlexboxLayoutManager getFlexboxLayoutManager(Context context) {
        //设置布局管理器
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(context);
        //flexDirection 属性决定主轴的方向（即项目的排列方向）。类似 LinearLayout 的 vertical 和 horizontal:
        // 主轴为水平方向，起点在左端。
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        //flexWrap 默认情况下 Flex 跟 LinearLayout 一样，都是不带换行排列的，但是flexWrap属性可以支持换行排列:
        // 按正常方向换行
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        //justifyContent 属性定义了项目在主轴上的对齐方式:
        // 交叉轴的起点对齐
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);

        return flexboxLayoutManager;
    }
}
