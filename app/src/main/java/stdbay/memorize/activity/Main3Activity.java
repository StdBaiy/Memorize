package stdbay.memorize.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import stdbay.memorize.R;
import stdbay.memorize.model.TreeNode;

public class Main3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Button bt =new Button(this);
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(TreeNode.treeNodeW, TreeNode.treeNodeH);
        p.topMargin = 200;
        p.leftMargin = 100;
        RelativeLayout hv=findViewById(R.id.hv);
//        RelativeLayout zoom= findViewById(R.id.zoom);
        hv.addView(bt,p);
        Button b=new Button(this);
        RelativeLayout.LayoutParams p2 = new RelativeLayout.LayoutParams(TreeNode.treeNodeW, TreeNode.treeNodeH);
        p.topMargin=400;
        p.leftMargin=200;
        hv.addView(b,p2);

    }
}
