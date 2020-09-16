package stdbay.memorize.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import stdbay.memorize.R;

public class MyFragment extends Fragment {
    private TextView mTextView;

    // 官方不建议 使用  带有参数的构造方法  如果强行使用  官方建议Fragment.setArguments(Bundle bundle)传递参数
    //    @SuppressLint("ValidFragment") 放在 类的开头
//    public MyFragment(String content){
//        this.content = content;
//    }


    // 获取MyFragment实例 并设置 参数
    public static MyFragment getInstance(String content){
        Bundle bundle = new Bundle();
        bundle.putString("content", content);

        MyFragment myFragment = new MyFragment();
        myFragment.setArguments(bundle);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);
        Bundle bundle = getArguments();
        if(bundle != null){
            String content = bundle.getString("content");
            mTextView = view.findViewById(R.id.txt_content);
            mTextView.setText(content);
        }

        return view;
    }
}
