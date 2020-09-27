package stdbay.memorize.util;

import android.graphics.Color;

import com.luck.picture.lib.style.PictureCropParameterStyle;
import com.luck.picture.lib.style.PictureParameterStyle;

import stdbay.memorize.R;

public class PictureStyle {
    public static PictureCropParameterStyle getmCropParameterStyle(){
        // 裁剪主题
        return new PictureCropParameterStyle(
                Color.parseColor("#557755"),
                Color.parseColor("#557755"),
                Color.parseColor("#557755"),
                Color.parseColor("#ffffff"),
                false);

    }

    public static PictureParameterStyle getmPictureParameterStyle() {
        PictureParameterStyle mPictureParameterStyle = new PictureParameterStyle();
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
        mPictureParameterStyle.pictureContainerBackgroundColor =  Color.parseColor("#eeeeee");
        // 相册列表标题栏右侧上拉箭头
        mPictureParameterStyle.pictureTitleUpResId = R.drawable.picture_icon_arrow_up;
        // 相册列表标题栏右侧下拉箭头
        mPictureParameterStyle.pictureTitleDownResId = R.drawable.picture_icon_arrow_down;
        // 相册文件夹列表选中圆点
        mPictureParameterStyle.pictureFolderCheckedDotStyle = R.drawable.ic_green_dot;
        // 相册返回箭头
//        mPictureParameterStyle.pictureLeftBackIcon = R.drawable.ic_back;
        // 标题栏字体颜色
        mPictureParameterStyle.pictureTitleTextColor =  Color.parseColor("#eeeeee");
        // 相册右侧取消按钮字体颜色  废弃 改用.pictureRightDefaultTextColor和.pictureRightDefaultTextColor
        mPictureParameterStyle.pictureRightDefaultTextColor =  Color.parseColor("#eeeeee");
        // 选择相册目录背景样式
//        mPictureParameterStyle.pictureAlbumStyle = R.drawable;
        // 相册列表勾选图片样式
        mPictureParameterStyle.pictureCheckedStyle = R.drawable.check_box_selector;
        // 相册列表底部背景色
        mPictureParameterStyle.pictureBottomBgColor =  Color.parseColor("#393a3e");
        // 已选数量圆点背景样式
        mPictureParameterStyle.pictureCheckNumBgStyle = R.drawable.ic_green_dot;
        // 相册列表底下预览文字色值(预览按钮可点击时的色值
        mPictureParameterStyle.picturePreviewTextColor =  Color.parseColor("#eeeeee");
        // 相册列表底下不可预览文字色值(预览按钮不可点击时的色值
        mPictureParameterStyle.pictureUnPreviewTextColor =  Color.parseColor("#555555");
        // 相册列表已完成色值(已完成 可点击色值
        mPictureParameterStyle.pictureCompleteTextColor =  Color.parseColor("#eeeeee");
        // 相册列表未完成色值(请选择 不可点击色值
        mPictureParameterStyle.pictureUnCompleteTextColor =  Color.parseColor("#555555");
        // 预览界面底部背景色
        mPictureParameterStyle.picturePreviewBottomBgColor =  Color.parseColor("#555555");
        // 外部预览界面删除按钮样式
        mPictureParameterStyle.pictureExternalPreviewDeleteStyle = R.drawable.picture_icon_delete;
        // 原图按钮勾选样式  需设置.isOriginalImageControl(true; 才有效
//        mPictureParameterStyle.pictureOriginalControlStyle = R.drawable.picture_original_wechat_checkbox;
        // 原图文字颜色 需设置.isOriginalImageControl(true; 才有效
        mPictureParameterStyle.pictureOriginalFontColor =  Color.parseColor("#eeeeee");
        // 外部预览界面是否显示删除按钮
        mPictureParameterStyle.pictureExternalPreviewGonePreviewDelete = false;
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
        return mPictureParameterStyle;
    }
}
