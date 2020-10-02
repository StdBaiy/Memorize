package stdbay.memorize.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnItemClickListener;
import com.luck.picture.lib.tools.DateUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import stdbay.memorize.R;
import stdbay.memorize.listener.OnItemLongClickListener;


/**
 * @author：luck
 * @date：2016-7-27 23:02
 * @describe：GridImageAdapter
 */
public class GridImageAdapter extends RecyclerView.Adapter<GridImageAdapter.ViewHolder> {
    public static final String TAG = "PictureSelector";
    private static final int TYPE_CAMERA = 1;
    private static final int TYPE_PICTURE = 2;
    private LayoutInflater mInflater;
    private List<LocalMedia> list = new ArrayList<>();
    private int selectMax = 8;

    public static final int SELECT_PIC = 1;
    public static final int VIEW_PIC = 2;
    private int viewType=VIEW_PIC;

    /**
     * 点击添加图片跳转
     */
    private onAddPicClickListener mOnAddPicClickListener;

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }


    public interface onAddPicClickListener {
        void onAddPicClick();
    }

    /**
     * 删除
     */
    public void delete(int position) {
        try {

            if (position != RecyclerView.NO_POSITION && list.size() > position) {
                list.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, list.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GridImageAdapter(Context context){
        this.mInflater = LayoutInflater.from(context);
    }

    public void setmOnAddPicClickListener(onAddPicClickListener mOnAddPicClickListener){
        this.mOnAddPicClickListener = mOnAddPicClickListener;
    }

    public void setSelectMax(int selectMax) {
        this.selectMax = selectMax;
    }

    public void setList(List<LocalMedia> list) {
        this.list = list;
    }

    public List<LocalMedia> getData() {
        return list == null ? new ArrayList<>() : list;
    }

    public void remove(int position) {
        if (list != null && position < list.size()) {
            list.remove(position);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImg;
        ImageView mIvDel;
        TextView tvDuration;

        ViewHolder(View view) {
            super(view);
            mImg = view.findViewById(R.id.fiv);
            mIvDel = view.findViewById(R.id.iv_del);
            tvDuration = view.findViewById(R.id.tv_duration);
        }
    }

    @Override
    public int getItemCount() {
        if (list.size() < selectMax) {
            return list.size() + 1;
        } else {
            return list.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowAddItem(position)) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PICTURE;
        }
    }

    /**
     * 创建ViewHolder
     */
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.gv_filter_image,
                viewGroup, false);
        return new ViewHolder(view);
    }

    private boolean isShowAddItem(int position) {
        int size = list.size();
        return position == size;
    }


    public void callOnAddPicClick(){
        mOnAddPicClickListener.onAddPicClick();
    }

    /**
     * 设置值
     */
    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NotNull final ViewHolder viewHolder, final int position) {
        if (getItemViewType(position) != TYPE_CAMERA) {
            if(getViewType()==SELECT_PIC) {
                viewHolder.mIvDel.setVisibility(View.VISIBLE);
                viewHolder.mIvDel.setOnClickListener(view -> {
                    int index = viewHolder.getAdapterPosition();
                    // 这里有时会返回-1造成数据下标越界,具体可参考getAdapterPosition()源码，
                    // 通过源码分析应该是bindViewHolder()暂未绘制完成导致，知道原因的也可联系我~感谢
                    if (index != RecyclerView.NO_POSITION && list.size() > index) {
                        list.remove(index);
                        GridImageAdapter.this.notifyItemRemoved(index);
                        GridImageAdapter.this.notifyItemRangeChanged(index, list.size());
                    }
                });
            }else{
                viewHolder.mIvDel.setVisibility(View.INVISIBLE);
            }
            LocalMedia media = list.get(position);
            if (media == null
                    || TextUtils.isEmpty(media.getPath())) {
                return;
            }
            int chooseModel = media.getChooseModel();
            String path;
            if (media.isCut() && !media.isCompressed()) {
                // 裁剪过
                path = media.getCutPath();
            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                path = media.getCompressPath();
            } else {
                // 原图
                path = media.getPath();
            }

//            Log.i(TAG, "原图地址::" + media.getPath());
//
//            if (media.isCut()) {
//                Log.i(TAG, "裁剪地址::" + media.getCutPath());
//            }
//            if (media.isCompressed()) {
//                Log.i(TAG, "压缩地址::" + media.getCompressPath());
//                Log.i(TAG, "压缩后文件大小::" + new File(media.getCompressPath()).length() / 1024 + "k");
//            }
//            if (!TextUtils.isEmpty(media.getAndroidQToPath())) {
//                Log.i(TAG, "Android Q特有地址::" + media.getAndroidQToPath());
//            }
//            if (media.isOriginal()) {
//                Log.i(TAG, "是否开启原图功能::" + true);
//                Log.i(TAG, "开启原图功能后地址::" + media.getOriginalPath());
//            }
            long duration = media.getDuration();
            viewHolder.tvDuration.setVisibility(PictureMimeType.isHasVideo(media.getMimeType())
                    ? View.VISIBLE : View.GONE);
            if (chooseModel == PictureMimeType.ofAudio()) {
                viewHolder.tvDuration.setVisibility(View.VISIBLE);
                viewHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds
                        (R.drawable.picture_icon_audio, 0, 0, 0);

            } else {
                viewHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds
                        (R.drawable.picture_icon_video, 0, 0, 0);
            }
            viewHolder.tvDuration.setText(DateUtils.formatDurationTime(duration));
            if (chooseModel == PictureMimeType.ofAudio()) {
                viewHolder.mImg.setImageResource(R.drawable.picture_audio_placeholder);
            } else {
                new RequestOptions();
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.color.f4);
//                    RequestOptions.circleCropTransform();
//                    requestOptions.centerCrop();
                requestOptions.transforms( new RoundedCorners(20));

//                    if(isScrollEnd) {
                    Glide.with(viewHolder.itemView.getContext())
                            .load(PictureMimeType.isContent(path) && !media.isCut() && !media.isCompressed() ? Uri.parse(path)
                                    : path)
                            .apply(requestOptions)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .into(viewHolder.mImg);
            }

     } else {
         //少于8张，显示继续添加的图标
//                if(getViewType()==SELECT_PIC) {
//                    viewHolder.mImg.setImageResource(R.drawable.ic_camera_color);
//                    viewHolder.mImg.setVisibility(View.VISIBLE);
//                    viewHolder.mImg.setOnClickListener(v -> mOnAddPicClickListener.onAddPicClick());
//                    viewHolder.mIvDel.setVisibility(View.GONE);
//                }else{
//                    viewHolder.mImg.setVisibility(View.GONE);
//                    viewHolder.mImg.setOnClickListener(null);
//                    viewHolder.mIvDel.setVisibility(View.GONE);
//                }
     }
        //itemView 的点击事件
                if (mItemClickListener != null) {
                    viewHolder.itemView.setOnClickListener(v -> {
                        int adapterPosition = viewHolder.getAdapterPosition();
                        mItemClickListener.onItemClick(v, adapterPosition);
                    });
                }

                if (mItemLongClickListener != null) {
                    viewHolder.itemView.setOnLongClickListener(v -> {
                        int adapterPosition = viewHolder.getAdapterPosition();
                        mItemLongClickListener.onItemLongClick(viewHolder, adapterPosition, v);
                        return true;
                    });
                }
           }

    private OnItemClickListener mItemClickListener;

    public void setOnItemClickListener(OnItemClickListener l) {
        this.mItemClickListener = l;
    }

    private OnItemLongClickListener mItemLongClickListener;

    public void setItemLongClickListener(OnItemLongClickListener l) {
        this.mItemLongClickListener = l;
    }

//    private Activity activity;
//    public  void setActivity(Activity activity){
//        this.activity=activity;
//    }

//    protected boolean isScrolling = false;
//
//    public void setScrolling(boolean scrolling) {
//        isScrolling = scrolling;
//    }

    public interface onSrollListener{
        public void onScrollEnd();
    }

    public static boolean isScrollEnd=true;


}
