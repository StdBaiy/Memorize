package stdbay.memorize.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import stdbay.memorize.db.MemorizeOpenHelper;

public class MemorizeDB {
    private static TreeNode root;
    private static int treeDepth;


    private static final String DB_NAME="memorize";
    private static final int VERSION=1;
    private static final int NO_FATHER =0;

    private static MemorizeDB memorizeDB;
    private SQLiteDatabase db;

    private MemorizeDB(Context context){
        MemorizeOpenHelper helper=new MemorizeOpenHelper(context,DB_NAME,null,VERSION);
        db=helper.getWritableDatabase();
    }

    private List<BaseItem>list= new ArrayList<>();

    private Cursor cursor;

        public static TreeNode getTreeRoot(){
            return root;
        }

    //获取MemorizeDB实例
    public synchronized static MemorizeDB getInstance(Context context){
        if(memorizeDB==null){
            memorizeDB=new MemorizeDB(context);
        }
        return memorizeDB;
    }

    public static int getTreeDepth() {
        return treeDepth;
    }

    @SuppressLint("Recycle")
    public List<ProblemItem>getProblemItems(int probSetId){
        List<ProblemItem>rtn=new ArrayList<>();
        cursor=db.rawQuery("select*from problem where probSetId=?",new String[]{String.valueOf(probSetId)});
        if(cursor.moveToFirst()){
            do{
                ProblemItem problemItem=new ProblemItem();
                List<LocalMedia>pics=new ArrayList<>();
                int id=cursor.getInt(cursor.getColumnIndex("id"));
                problemItem.setProbSetId(probSetId);
                problemItem.setId(id);
                problemItem.setCreateTime(cursor.getString(cursor.getColumnIndex("createTime")));
                problemItem.setNumber(String.valueOf(cursor.getInt(cursor.getColumnIndex("number"))));;
                problemItem.setGrade(String.valueOf(cursor.getFloat(cursor.getColumnIndex("grade"))));
                problemItem.setTotalGrade(String.valueOf(cursor.getFloat(cursor.getColumnIndex("totalGrade"))));
                problemItem.setSubId(cursor.getInt(cursor.getColumnIndex("subId")));
                problemItem.setSummary(cursor.getString(cursor.getColumnIndex("summary")));

                 Cursor csr=db.rawQuery("select*from prob_pic where probId=?",new String[]{String.valueOf(id)});
                if(csr.moveToFirst()){
                    do{
                        LocalMedia media = new LocalMedia();
                        media.setPath(csr.getString(csr.getColumnIndex("picPath")));
                        pics.add(media);
                    }while(csr.moveToNext());
                }
                problemItem.setPictures(pics);
                rtn.add(problemItem);
            }while(cursor.moveToNext());
        }
        return  rtn;
    }

    @SuppressLint("Recycle")
    public List<BaseItem>loadData(BaseItem nowItem){
        list.clear();
        if(nowItem==null){
            cursor=db.rawQuery("select*from subject where fatherId is null",null);
            queryFromCursorToList(BaseItem.SUBJECT_TYPE);
        }else
        switch(nowItem.getType()){
            case BaseItem.SUBJECT_TYPE:
                cursor=db.rawQuery("select*from subject where fatherId=?",new String[]{String.valueOf(nowItem.getId())});
                queryFromCursorToList(BaseItem.SUBJECT_TYPE);
                cursor=db.rawQuery("select*from problem_set where fatherId is null and subId=?"
                        ,new String[]{String.valueOf(nowItem.getId())});
                queryFromCursorToList(BaseItem.PROBLEM_SET_TYPE);
                break;
//            case BaseItem.PROBLEM_SET_TYPE:
//                cursor=db.rawQuery("select*from problem_set where fatherId=?",new String[]{String.valueOf(nowItem.getId())});
//                queryFromCursorToList(BaseItem.PROBLEM_SET_TYPE);
//                break;
        }
        cursor.close();
        return list;
    }



    public void addItem(final BaseItem father, final String name, int type, final callBackListener listener){
        switch(type){
            case BaseItem.SUBJECT_TYPE:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(father==null)
                                db.execSQL("insert into subject (name)values(?)",
                                new String[]{name});
                            else
                                db.execSQL("insert into subject (name,fatherId)values(?,?)",
                                        new String[]{name, String.valueOf(father.getId())});
                            if(listener!=null)
                                listener.onFinished();
                        }catch (SQLException e) {
                            Log.d("error", Objects.requireNonNull(e.getMessage()));
                            listener.onError(e);
                        }
                    }
                }).start();
                break;
            case BaseItem.PROBLEM_SET_TYPE:
                new Thread(new Runnable() {
                    @SuppressLint("Recycle")
                    @Override
                    public void run() {
                        try{
                            if(father.getType()==BaseItem.SUBJECT_TYPE){
                                db.execSQL("insert into problem_set (name,subId,createTime,viewTimes)" +
                                        "values (?,?,(select date('now')),0)",new String[]{name, String.valueOf(father.getId())});
                            }else if(father.getType()==BaseItem.PROBLEM_SET_TYPE){
                                int subId=0;
                                cursor =db.rawQuery("select*from problem_set where id=?",new String[]{String.valueOf(father.getId())}); //子节点的subId和父节点相同
                                if (cursor.moveToFirst()) {
                                    subId=cursor.getInt(cursor.getColumnIndex("subId"));
                                }
                                cursor.close();
                                db.execSQL("insert into problem_set (name,fatherId,subId,createTime,viewTimes)" +
                                        "values(?,?,?,(select date('now')),0)",
                                        new String[]{name, String.valueOf(father.getId()), String.valueOf(subId)});
                            }
                            if(listener!=null)
                                listener.onFinished();
                        } catch (Exception e) {
                            if(listener!=null)
                                listener.onError(e);
                        }
                    }
                }).start();
                break;
        }
    }

    public void addKnowledge(final int fatherId,  final int subId,final String name,final String annotation, final callBackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (fatherId == NO_FATHER)
                        db.execSQL("insert into knowledge (name,subId,annotation) values (?,?,?)", new String[]{name, String.valueOf(subId),annotation});
                    else
                        db.execSQL("insert into knowledge (name,fatherId,subId,annotation) values(?,?,?,?)",
                                new String[]{name, String.valueOf(fatherId), String.valueOf(subId),annotation});
                    if (listener != null)
                        listener.onFinished();
                } catch (SQLException e) {
                    if (listener != null)
                        listener.onError(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @SuppressLint("Recycle")
    public BaseItem findBackItem(BaseItem nowItem){
        if(nowItem==null)return null;
        switch (nowItem.getType()){
            case BaseItem.SUBJECT_TYPE:
                if(nowItem.getFatherId()==NO_FATHER)return null;
                cursor=db.rawQuery("select*from subject where id=?",new String[]{String.valueOf(nowItem.getFatherId())});
                break;
            case BaseItem.PROBLEM_SET_TYPE:
                if(nowItem.getFatherId()==NO_FATHER){//习题集没有父亲的话,它的上一级就是科目
                    cursor=db.rawQuery("select*from problem_set where id=?",new String[]{String.valueOf(nowItem.getId())});
                    //因为nowitem不含subId字段,因此要先从数据库中查出来
                    if(cursor.moveToFirst()){
                        int subId=cursor.getInt(cursor.getColumnIndex("subId"));
                        cursor= db.rawQuery("select*from subject where id=?",new String[]{String.valueOf(subId)});
                    }

                }else{
                    cursor=db.rawQuery("select*from problem_set where id=?",new String[]{String.valueOf(nowItem.getFatherId())});
                }
                break;
        }
        BaseItem rtn=new BaseItem();
        if(cursor.moveToFirst()){
            rtn.setType(BaseItem.SUBJECT_TYPE);
            rtn.setName(cursor.getString(cursor.getColumnIndex("name")));
            rtn.setFatherId(cursor.getInt(cursor.getColumnIndex("fatherId")));
            rtn.setId(cursor.getInt(cursor.getColumnIndex("id")));
        }
        cursor.close();
        return rtn;

    }


    @SuppressLint("Recycle")
    private void queryFromCursorToList(int type){
        if(cursor.moveToFirst()) do {
            BaseItem item = new BaseItem();
            item.setType(type);
            item.setId(cursor.getInt(cursor.getColumnIndex("id")));
            item.setName(cursor.getString(cursor.getColumnIndex("name")));

            //查询子项
            Cursor csr=null;
            Map<String, Integer>map=new HashMap<>();
            switch(type){
                case BaseItem.SUBJECT_TYPE:
                    csr=db.rawQuery("select id from subject where fatherId=?",new String[]{String.valueOf(item.getId())});
                    csr.moveToFirst();
                    map.put("子科目",csr.getCount());
                    csr=db.rawQuery("select id from problem_set where subId=?",new String[]{String.valueOf(item.getId())});
                    csr.moveToFirst();
                    map.put("子习题集",csr.getCount());
                    break;
                case BaseItem.PROBLEM_SET_TYPE:
                    csr=db.rawQuery("select id from problem where probSetId=?",new String[]{String.valueOf(item.getId())});
                    csr.moveToFirst();
                    map.put("习题",csr.getCount());
                    break;
                case BaseItem.PROBLEM_TYPE:
            }
            item.setChildrenData(map);
            list.add(item);
        } while (cursor.moveToNext());
        cursor.close();
    };

    public void deleteItem(int id,int type,callBackListener listener){
        String sql="delete from ";
        switch(type){
            case BaseItem.SUBJECT_TYPE:
                sql+="subject";
                break;
            case BaseItem.PROBLEM_SET_TYPE:
                sql+="problem_set";
                break;
            case BaseItem.KNOWLEDGE_TYPE:
                sql+="knowledge";
                break;
            case BaseItem.PROBLEM_TYPE:
                sql+="problem";
                break;
        }
        sql+=" where id=?";
        try {
            db.execSQL(sql, new String[]{String.valueOf(id)});
            if(listener!=null)
                listener.onFinished();
        } catch (SQLException e) {
            if(listener!=null)
                listener.onError(e);
        }
    }

    public void reName(BaseItem item,String newName,callBackListener listener){
        try{
            String sql="update ";
            switch(item.getType()){
                case BaseItem.SUBJECT_TYPE:
                    sql+="subject";
                    break;
                case BaseItem.PROBLEM_SET_TYPE:
                    sql+="problem_set";
                    break;
                case BaseItem.KNOWLEDGE_TYPE:
                    sql+="knowledge";
                    break;
            }
            sql+=" set name=? where id=?";
            db.execSQL(sql,new String[]{newName, String.valueOf(item.getId())});
            if(listener!=null)
                listener.onFinished();
        } catch (SQLException e) {
            if(listener!=null)
                listener.onError(e);
        }

    }

    public void changeKnowledgeAnnotation(int id,String annotation,callBackListener listener){
        try {
            db.execSQL("update knowledge set annotation=? where id=?",
                    new String[]{annotation,String.valueOf(id)});
            if(listener!=null)
                listener.onFinished();
        } catch (SQLException e) {
            if(listener!=null)
                listener.onError(e);
            e.printStackTrace();
        }
    }


    public interface callBackListener{
        void onFinished();
        void onError(Exception e);
    }


    public void GoThroughKnowledge(final int subId,  final callBackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TreeNode root = new TreeNode();
                    cursor=db.rawQuery("select*from subject where id=?",new String[]{String.valueOf(subId)});
                    if(cursor.moveToFirst()){
                        root.setName(cursor.getString(cursor.getColumnIndex("name")));
                    }
                    Queue<TreeNode> queue = new LinkedList<TreeNode>();
                    queue.offer(root);
                    int length;
                    int tmpLength = 1;



                    MemorizeDB.treeDepth=0;
                    while (!queue.isEmpty()) {
                        length = tmpLength;
                        tmpLength = 0;
                        for (int i = 0; i < length; ++i) {
                            if (queue.peek() == root)//根节点没有父亲
                                cursor = db.rawQuery("select*from knowledge where fatherId is null and subId =?", new String[]{String.valueOf(subId)});
                            else
                                cursor = db.rawQuery("select * from knowledge where fatherId =? and subId =?",
                                        new String[]{String.valueOf(Objects.requireNonNull(queue.peek()).getId()), String.valueOf(subId)});
                            List<TreeNode> children = new ArrayList<>();
                            if (cursor.moveToFirst()) {
                                do {
                                    TreeNode treeNode = new TreeNode();
                                    treeNode.setId(cursor.getInt(cursor.getColumnIndex("id")));
                                    treeNode.setName(cursor.getString(cursor.getColumnIndex("name")));
                                    treeNode.setFather(queue.peek());
                                    treeNode.setAnnotation(cursor.getString(cursor.getColumnIndex("annotation")));
                                    children.add(treeNode);
                                    queue.offer(treeNode);
                                } while (cursor.moveToNext());
                            }
                            tmpLength += cursor.getCount();
                            Objects.requireNonNull(queue.poll()).setChildren(children);
                        }
                        MemorizeDB.treeDepth++;
                    }
                    MemorizeDB.root=root;
                    if (listener!=null)
                        listener.onFinished();
                } catch (Exception e) {
                    if (listener!=null)
                        listener.onError(e);
                }
            }
        }).start();
    }

    //计算给定节点的叶子节点数,用于规划布局位置
    public static int getLeavesNum(TreeNode node){
        if(node.getChildren().isEmpty())return 1;
        else{
            int num=0;
            for(int i=0;i<node.getChildren().size();++i){
                if(node.getChildren().get(i).getChildren().isEmpty())num++;
                else num+=getLeavesNum(node.getChildren().get(i));
            }
            return num;
        }
    }

    public Map<String,Integer> getSubjects(){
        Map<String,Integer>rtn=new HashMap<>();

        //这里仅查询科目表中的叶子结点
        cursor=db.rawQuery("select * from subject s1 where not exists " +
                "(select * from subject s2 where s2.fatherId in (select id from subject where id=s1.id))",null);
        if(cursor.moveToFirst()){
            do{
                rtn.put(cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getInt(cursor.getColumnIndex("id")));
            }while (cursor.moveToNext());
        }
        return rtn;
    }

    public void addProblem(int probSetId,String number,String summary,String grade,String totalGrade,List<LocalMedia> mediaList,callBackListener listener){
        try {
            //通过这种方法,在插入主键之前就确定了主键值
            cursor = db.rawQuery("select*from problem order by id desc", null);
            int id = 1;
            if (cursor.moveToFirst())
                id = cursor.getInt(cursor.getColumnIndex("id")) + 1;
            int subId = 0;
            cursor = db.rawQuery("select*from problem_set where id=?", new String[]{String.valueOf(probSetId)});
            if (cursor.moveToFirst())
                subId = cursor.getInt(cursor.getColumnIndex("subId"));
            db.execSQL("insert into problem (id,probSetId,subId,number,createTime,summary,grade,totalGrade)" +
                            "values(?,?,?,?,(select date('now')),?,?,?)",
                    new String[]{String.valueOf(id), String.valueOf(probSetId), String.valueOf(subId),number,
                            summary, grade, totalGrade});

            //把每一个照片地址存下来
            for(LocalMedia media:mediaList){
                String path;
                if(media.getCompressPath()!=null)
                    path=media.getCompressPath();
                else if(media.getCutPath()!=null)
                    path=media.getCutPath();
                else if(media.getAndroidQToPath()!=null)
                    path=media.getAndroidQToPath();
                else
                    path=media.getPath();
                db.execSQL("insert into prob_pic (probId,picPath)values(?,?)",
                        new String[]{String.valueOf(id),path});
            }
            if (listener!=null)
                listener.onFinished();
        } catch (SQLException e) {
            if (listener!=null)
                listener.onError(e);
            e.printStackTrace();
        }
    }

    public void changeProblem(int id,String number,String summary,String grade,String totalGrade,List<LocalMedia> mediaList,callBackListener listener){
        try {
//            //通过这种方法,在插入主键之前就确定了主键值
//            cursor = db.rawQuery("select*from problem order by id desc", null);
//            int id = 1;
//            if (cursor.moveToFirst())
//                id = cursor.getInt(cursor.getColumnIndex("id")) + 1;
//            int subId = 0;
//            cursor = db.rawQuery("select*from problem_set where id=?", new String[]{String.valueOf(probSetId)});
//            if (cursor.moveToFirst())
//                subId = cursor.getInt(cursor.getColumnIndex("subId"));
            db.execSQL("update problem set number=?,summary=?,grade=?,totalGrade=? where id=?",
                    new String[]{number,summary, grade, totalGrade, String.valueOf(id)});


            //先删掉已经有的图片
            db.execSQL("delete from prob_pic where probId=?",new String[]{String.valueOf(id)});
            //再添加更改后的
            if(!mediaList.isEmpty())
            for(LocalMedia media:mediaList){
                String path;
                if(media.getCompressPath()!=null)
                    path=media.getCompressPath();
                else if(media.getCutPath()!=null)
                    path=media.getCutPath();
                else if(media.getAndroidQToPath()!=null)
                    path=media.getAndroidQToPath();
                else
                    path=media.getPath();

                db.execSQL("insert into prob_pic (probId,picPath)values(?,?)",
                        new String[]{String.valueOf(id),path});
            }

            if (listener!=null)
                listener.onFinished();
        } catch (SQLException e) {
            if (listener!=null)
                listener.onError(e);
            e.printStackTrace();
        }
    }
}
