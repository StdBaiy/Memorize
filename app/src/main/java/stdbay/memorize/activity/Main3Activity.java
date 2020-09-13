package stdbay.memorize.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import stdbay.memorize.R;
import stdbay.memorize.model.DrawGeometryView;
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

        RelativeLayout zoom= findViewById(R.id.zoom);
        zoom.addView(bt,p);
        bt=new Button(this);
        p.topMargin=400;
        p.leftMargin=300;
        zoom.addView(bt,p);

        DrawGeometryView view = new DrawGeometryView(this, 0, 0, 100 , 200);
        p = new RelativeLayout.LayoutParams(400, 400);
        view.invalidate();
        p.topMargin =400;
        p.leftMargin=400;
        zoom.addView(view,p);
    }
}
