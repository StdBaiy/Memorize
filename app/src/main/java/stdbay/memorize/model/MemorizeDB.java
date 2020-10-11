package stdbay.memorize.model;

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
import stdbay.memorize.fragment.KnowledgeTreeFragment;

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


    public KnowledgeItem getKnowledge(int knowId){
        KnowledgeItem rtn=new KnowledgeItem();
        cursor=db.rawQuery("select*from knowledge where id=?",new String[]{String.valueOf(knowId)});
        if(cursor.moveToFirst()){
            rtn.setId(cursor.getInt(cursor.getColumnIndex("id")));
            rtn.setFatherId(cursor.getInt(cursor.getColumnIndex("fatherId")));
            rtn.setName(cursor.getString(cursor.getColumnIndex("name")));
            rtn.setAnnotation(cursor.getString(cursor.getColumnIndex("annotation")));
            //添加图片
            List<LocalMedia>pics=new ArrayList<>();
            Cursor csr=db.rawQuery("select*from know_pic where knowId=?",new String[]{String.valueOf(rtn.getId())});
            if(csr.moveToFirst()){
                do{
                    LocalMedia media = new LocalMedia();
                    media.setPath(csr.getString(csr.getColumnIndex("path")));
                    media.setCompressPath(csr.getString(csr.getColumnIndex("compressPath")));
                    media.setAndroidQToPath(csr.getString(csr.getColumnIndex("androidQToPath")));
                    media.setBucketId(csr.getInt(csr.getColumnIndex("id")));
                    media.setChecked(csr.getInt(csr.getColumnIndex("isChecked"))==1);
                    media.setCut(csr.getInt(csr.getColumnIndex("isCut"))==1);
                    media.setCutPath(csr.getString(csr.getColumnIndex("cutPath")));
                    media.setRealPath(csr.getString(csr.getColumnIndex("realPath")));
                    media.setPosition(csr.getInt(csr.getColumnIndex("position")));
                    media.setNum(csr.getInt(csr.getColumnIndex("num")));
                    media.setMimeType(csr.getString(csr.getColumnIndex("mimeType")));
                    media.setChooseModel(csr.getInt(csr.getColumnIndex("chooseModel")));
                    media.setCompressed(csr.getInt(csr.getColumnIndex("compressed"))==1);
                    media.setWidth(csr.getInt(csr.getColumnIndex("width")));
                    media.setHeight(csr.getInt(csr.getColumnIndex("height")));
                    media.setSize(csr.getInt(csr.getColumnIndex("size")));
                    media.setFileName(csr.getString(csr.getColumnIndex("fileName")));
                    media.setParentFolderName(csr.getString(csr.getColumnIndex("parentFolderName")));
                    media.setOrientation(csr.getInt(csr.getColumnIndex("orientation")));
                    media.loadLongImageStatus=csr.getInt(csr.getColumnIndex("loadLongImageStatus"));
                    media.isLongImage=csr.getInt(csr.getColumnIndex("isLongImage"))==1;
                    media.setMaxSelectEnabledMask(csr.getInt(csr.getColumnIndex("isMaxSelectEnabledMask"))==1);
                    pics.add(media);
                }while(csr.moveToNext());
                csr.close();
            }

            //添加相关题目
            List<BaseItem>problems=new ArrayList<>();
            csr=db.rawQuery("select distinct probId from prob_know where knowId=?",new String[]{String.valueOf(rtn.getId())});
            if(csr.moveToFirst()){
                do {
                    BaseItem item=new BaseItem();
                    item.setType(BaseItem.PROBLEM_TYPE);
                    item.setId(csr.getInt(csr.getColumnIndex("probId")));
                    String name="";
                    Cursor c1=db.rawQuery("select * from problem where id=?",new String[]{String.valueOf(item.getId())});
                    if(c1.moveToFirst()){
                        Cursor c2=db.rawQuery("select * from problem_set where id=?",new String[]{String.valueOf(c1.getInt(c1.getColumnIndex("probSetId")))});
                        if(c2.moveToFirst()){
                            name+=c2.getString(c2.getColumnIndex("name"));
                            c2.close();
                        }
                        name+="/第";
                        name+=c1.getString(c1.getColumnIndex("number"));
                        name+="题";
                        c1.close();
                    }
                    item.setName(name);
                    problems.add(item);
                }while(csr.moveToNext());
                csr.close();
            }
            rtn.setPictures(pics);
            rtn.setProblems(problems);
        }
        cursor.close();
        return rtn;
    }


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
                problemItem.setNumber(String.valueOf(cursor.getInt(cursor.getColumnIndex("number"))));
                problemItem.setGrade(String.valueOf(cursor.getFloat(cursor.getColumnIndex("grade"))));
                problemItem.setTotalGrade(String.valueOf(cursor.getFloat(cursor.getColumnIndex("totalGrade"))));
                problemItem.setSubId(cursor.getInt(cursor.getColumnIndex("subId")));
                problemItem.setSummary(cursor.getString(cursor.getColumnIndex("summary")));


                //添加图片
                Cursor csr=db.rawQuery("select*from prob_pic where probId=?",new String[]{String.valueOf(id)});
                if(csr.moveToFirst()){
                    do{
                        LocalMedia media = new LocalMedia();
                        media.setPath(csr.getString(csr.getColumnIndex("path")));
                        media.setCompressPath(csr.getString(csr.getColumnIndex("compressPath")));
                        media.setAndroidQToPath(csr.getString(csr.getColumnIndex("androidQToPath")));
                        media.setBucketId(csr.getInt(csr.getColumnIndex("id")));
                        media.setChecked(csr.getInt(csr.getColumnIndex("isChecked"))==1);
                        media.setCut(csr.getInt(csr.getColumnIndex("isCut"))==1);
                        media.setCutPath(csr.getString(csr.getColumnIndex("cutPath")));
                        media.setRealPath(csr.getString(csr.getColumnIndex("realPath")));
                        media.setPosition(csr.getInt(csr.getColumnIndex("position")));
                        media.setNum(csr.getInt(csr.getColumnIndex("num")));
                        media.setMimeType(csr.getString(csr.getColumnIndex("mimeType")));
                        media.setChooseModel(csr.getInt(csr.getColumnIndex("chooseModel")));
                        media.setCompressed(csr.getInt(csr.getColumnIndex("compressed"))==1);
                        media.setWidth(csr.getInt(csr.getColumnIndex("width")));
                        media.setHeight(csr.getInt(csr.getColumnIndex("height")));
                        media.setSize(csr.getInt(csr.getColumnIndex("size")));
                        media.setFileName(csr.getString(csr.getColumnIndex("fileName")));
                        media.setParentFolderName(csr.getString(csr.getColumnIndex("parentFolderName")));
                        media.setOrientation(csr.getInt(csr.getColumnIndex("orientation")));
                        media.loadLongImageStatus=csr.getInt(csr.getColumnIndex("loadLongImageStatus"));
                        media.isLongImage=csr.getInt(csr.getColumnIndex("isLongImage"))==1;
                        media.setMaxSelectEnabledMask(csr.getInt(csr.getColumnIndex("isMaxSelectEnabledMask"))==1);
                        pics.add(media);
                    }while(csr.moveToNext());
                    csr.close();
                }

                //添加知识点
                List<BaseItem> knowledges=new ArrayList<>();
                csr=db.rawQuery("select*from prob_know where probId=?",new String[]{String.valueOf(id)});
                if(csr.moveToFirst()){
                    do{
                        int knowId=csr.getInt(csr.getColumnIndex("knowId"));
                        BaseItem item=new BaseItem();
                        item.setId(knowId);

                        Cursor c1=db.rawQuery("select*from knowledge where id=?",new String[]{String.valueOf(knowId)});
                        if(c1.moveToFirst())
                            item.setName(c1.getString(c1.getColumnIndex("name")));
                        c1.close();
                        item.setType(BaseItem.KNOWLEDGE_TYPE);
                        knowledges.add(item);
                    }while(csr.moveToNext());
                    csr.close();
                }
                problemItem.setPictures(pics);
                problemItem.setKnowledges(knowledges);
                rtn.add(problemItem);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return  rtn;
    }

    public List<BaseItem>loadData(BaseItem nowItem){
        list.clear();
        if(nowItem==null){
            cursor=db.rawQuery("select*from subject where fatherId is null",null);
            queryFromCursorToList(BaseItem.SUBJECT_TYPE);
            cursor.close();
        }else if (nowItem.getType() == BaseItem.SUBJECT_TYPE) {
            cursor = db.rawQuery("select*from problem_set where fatherId is null and subId=?"
                    , new String[]{String.valueOf(nowItem.getId())});
            queryFromCursorToList(BaseItem.PROBLEM_SET_TYPE);
            cursor.close();

            cursor = db.rawQuery("select*from subject where fatherId=?", new String[]{String.valueOf(nowItem.getId())});
            queryFromCursorToList(BaseItem.SUBJECT_TYPE);
            cursor.close();
        }
        return list;
    }



    public void addItem(final BaseItem father, final String name, int type, final callBackListener listener){
        switch(type){
            case BaseItem.SUBJECT_TYPE:
                new Thread(() -> {
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
                }).start();
                break;
            case BaseItem.PROBLEM_SET_TYPE:
                new Thread(() -> {
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
                }).start();
                break;
        }
    }

    public void addKnowledge(int type, final int fatherId,  final int subId,final String name,final String annotation, List<LocalMedia>mediaList,final callBackListener listener){
        new Thread(() -> {
            try {
                //通过这种方法,在插入主键之前就确定了主键值
                cursor = db.rawQuery("select*from knowledge order by id desc", null);
                int id = 1;
                if (cursor.moveToFirst())
                    id = cursor.getInt(cursor.getColumnIndex("id")) + 1;

                cursor.close();


                if (fatherId == NO_FATHER) {
                    db.execSQL("insert into knowledge (id,name,subId,annotation) values (?,?,?,?)",
                            new String[]{String.valueOf(id),name, String.valueOf(subId),annotation});
                }
                else {
                    db.execSQL("insert into knowledge (id,name,fatherId,subId,annotation) values(?,?,?,?,?)",
                            new String[]{String.valueOf(id), name, String.valueOf(fatherId), String.valueOf(subId), annotation});
                }

                if(type== KnowledgeTreeFragment.INSERT){
                    if(fatherId!=NO_FATHER)
                        db.execSQL("update knowledge set fatherId=? where fatherId=? and id!=? and subId=?",new String[]{
                                String.valueOf(id), String.valueOf(fatherId), String.valueOf(id), String.valueOf(subId)});
                    else
                        db.execSQL("update knowledge set fatherId=? where fatherId is null and id!=? and subId=?",new String[]{
                                String.valueOf(id),String.valueOf(id), String.valueOf(subId)});
                }

                //为知识点添加图片
                addPictures(id,BaseItem.KNOWLEDGE_TYPE,mediaList);

                if (listener != null)
                    listener.onFinished();
            } catch (SQLException e) {
                if (listener != null)
                    listener.onError(e);
                e.printStackTrace();
            }
        }).start();
    }

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


    private void queryFromCursorToList(int type){
        if(cursor.moveToFirst()) do {
            BaseItem item = new BaseItem();
            item.setType(type);
            item.setId(cursor.getInt(cursor.getColumnIndex("id")));
            item.setName(cursor.getString(cursor.getColumnIndex("name")));

            //查询子项
            Cursor csr;
            Map<String, Integer>map=new HashMap<>();
            switch(type){
                case BaseItem.SUBJECT_TYPE:
                    csr=db.rawQuery("select id from subject where fatherId=?",new String[]{String.valueOf(item.getId())});
                    csr.moveToFirst();
                    csr.close();
                    map.put("子科目",csr.getCount());
                    csr=db.rawQuery("select id from problem_set where subId=?",new String[]{String.valueOf(item.getId())});
                    csr.moveToFirst();
                    csr.close();
                    map.put("子习题集",csr.getCount());
                    break;
                case BaseItem.PROBLEM_SET_TYPE:
                    csr=db.rawQuery("select id from problem where probSetId=?",new String[]{String.valueOf(item.getId())});
                    csr.moveToFirst();
                    csr.close();
                    map.put("习题",csr.getCount());
                    break;
                case BaseItem.PROBLEM_TYPE:
            }
            item.setChildrenData(map);
            list.add(item);
        } while (cursor.moveToNext());
        cursor.close();
    }

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
                try{
                    int fatherId=0;
                    cursor=db.rawQuery("select*from knowledge where id=?",new String[]{String.valueOf(id)});
                    if(cursor.moveToFirst()){
                        fatherId=cursor.getInt(cursor.getColumnIndex("fatherId"));
                        cursor.close();
                    }
                    if(fatherId!=NO_FATHER)
                        db.execSQL("update knowledge set fatherId=? where fatherId=?",new String[]{String.valueOf(fatherId), String.valueOf(id)});
                    else
                        db.execSQL("update knowledge set fatherId=null where fatherId=?",new String[]{String.valueOf(id)});
                    sql+="knowledge";
                } catch (Exception e) {
                    e.printStackTrace();
                }
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


    public interface callBackListener{
        void onFinished();
        void onError(Exception e);
    }


    public void GoThroughKnowledge(final int subId,  final callBackListener listener){
        new Thread(() -> {
            try {
                TreeNode root = new TreeNode();
                cursor=db.rawQuery("select*from subject where id=?",new String[]{String.valueOf(subId)});
                if(cursor.moveToFirst()){
                    root.setName(cursor.getString(cursor.getColumnIndex("name")));
                }
                Queue<TreeNode> queue = new LinkedList<>();
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
//        cursor=db.rawQuery("select * from subject s1 where not exists " +
//                "(select * from subject s2 where s2.fatherId in (select id from subject where id=s1.id))",null);
        cursor=db.rawQuery("select*from subject",null);
        if(cursor.moveToFirst()){
            do{
                rtn.put(cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getInt(cursor.getColumnIndex("id")));
            }while (cursor.moveToNext());
        }
        return rtn;
    }

    private void addPictures(int id, int type, List<LocalMedia> mediaList){
        String sql="";
        if(type==BaseItem.KNOWLEDGE_TYPE)
            sql="insert into know_pic (knowId,path,compressPath,cutPath,realPath,id,isChecked,androidQToPath," +
                    "isCut,position,num,mimeType,chooseModel,compressed,width,height,size,fileName,parentFolderName,orientation," +
                    "loadLongImageStatus,isLongImage,bucketId,isMaxSelectEnabledMask)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        else if(type==BaseItem.PROBLEM_TYPE)
            sql="insert into prob_pic (probId,path,compressPath,cutPath,realPath,id,isChecked,androidQToPath," +
                    "isCut,position,num,mimeType,chooseModel,compressed,width,height,size,fileName,parentFolderName,orientation," +
                    "loadLongImageStatus,isLongImage,bucketId,isMaxSelectEnabledMask)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        //把每一个照片地址存下来
        for(LocalMedia media:mediaList){
            db.execSQL(sql,
                    new String[]{String.valueOf(id), media.getPath(),media.getCompressPath(),media.getCutPath(),media.getRealPath(), String.valueOf(media.getId()),
                            media.isChecked()?"1":"0",media.getAndroidQToPath(),media.isCut()?"1":"0", String.valueOf(media.getPosition()), String.valueOf(media.getNum()),
                            media.getMimeType(), String.valueOf(media.getChooseModel()),media.isCompressed()?"1":"0", String.valueOf(media.getWidth()), String.valueOf(media.getHeight()),
                            String.valueOf(media.getSize()),media.getFileName(),media.getParentFolderName(), String.valueOf(media.getOrientation()), String.valueOf(media.loadLongImageStatus),
                            media.isLongImage?"1":"0", String.valueOf(media.getBucketId()),media.isMaxSelectEnabledMask()?"1":"0"});
        }
    }

    public void addProblem(int probSetId,String number,String summary,String grade,String totalGrade,List<LocalMedia> mediaList,List<BaseItem> knowledgeList,callBackListener listener){
        try {
            //通过这种方法,在插入主键之前就确定了主键值
            cursor = db.rawQuery("select*from problem order by id desc", null);
            int id = 1;
            if (cursor.moveToFirst())
                id = cursor.getInt(cursor.getColumnIndex("id")) + 1;

            if (grade.equals(""))
                grade="0";
            if(totalGrade.equals(""))
                totalGrade="0";
            //如果没有选择题目编号,就自动添加
            if(number.equals("")) {
                cursor = db.rawQuery("select*from problem where probSetId=? order by number desc", new String[]{String.valueOf(probSetId)});
                if(cursor.moveToFirst())
                    number= String.valueOf(cursor.getInt(cursor.getColumnIndex("number"))+1);
                else
                    //如果表中还没有数据
                    number="1";
            }

            int subId = 0;
            cursor = db.rawQuery("select*from problem_set where id=?", new String[]{String.valueOf(probSetId)});
            if (cursor.moveToFirst())
                subId = cursor.getInt(cursor.getColumnIndex("subId"));
            db.execSQL("insert into problem (id,probSetId,subId,number,createTime,summary,grade,totalGrade)" +
                            "values(?,?,?,?,(select date('now')),?,?,?)",
                    new String[]{String.valueOf(id), String.valueOf(probSetId), String.valueOf(subId),number,
                            summary, grade, totalGrade});

            //存图片
            addPictures(id,BaseItem.PROBLEM_TYPE,mediaList);

            //存知识点
            for(BaseItem item:knowledgeList){
                db.execSQL("insert into prob_know (probId,knowId,name) values (?,?,?)",
                        new String[]{String.valueOf(id), String.valueOf(item.getId()),item.getName()});
            }
            if (listener!=null)
                listener.onFinished();
        } catch (SQLException e) {
            if (listener!=null)
                listener.onError(e);
            e.printStackTrace();
        }
    }

    public void changeProblem(int id,String number,String summary,String grade,String totalGrade,List<LocalMedia> mediaList,List<BaseItem> knowledgeList,callBackListener listener){
        try {

            int probSetId=0;
            cursor=db.rawQuery("select*from problem where id=?",new String[]{String.valueOf(id)});
            if(cursor.moveToFirst())
                probSetId=cursor.getInt(cursor.getColumnIndex("probSetId"));
            if (grade.equals(""))
                grade="0";
            if(totalGrade.equals(""))
                totalGrade="0";
            //如果没有选择题目编号,就自动添加
            if(number.equals("")) {
                cursor = db.rawQuery("select*from problem where probSetId=? order by number desc", new String[]{String.valueOf(probSetId)});
                if(cursor.moveToFirst())
                    number= String.valueOf(cursor.getInt(cursor.getColumnIndex("number"))+1);
                else
                    //如果表中还没有数据
                    number="1";
            }

            db.execSQL("update problem set number=?,summary=?,grade=?,totalGrade=? where id=?",
                    new String[]{number,summary, grade, totalGrade, String.valueOf(id)});

            //先删掉已经有的图片
            db.execSQL("delete from prob_pic where probId=?",new String[]{String.valueOf(id)});
            //再添加更改后的
            addPictures(id,BaseItem.PROBLEM_TYPE,mediaList);

            //先删掉已有的知识点
            db.execSQL("delete from prob_know where probId=?",new String[]{String.valueOf(id)});
            //添加更改后的
            for(BaseItem item : knowledgeList){
                db.execSQL("insert into prob_know (probId,knowId,name)values(?,?,?)",
                        new String[]{String.valueOf(id), String.valueOf(item.getId()),item.getName()});
            }

            if (listener!=null)
                listener.onFinished();
        } catch (SQLException e) {
            if (listener!=null)
                listener.onError(e);
            e.printStackTrace();
        }
    }


    //通过知识点获取科目号
    public int getSubId(int knowId){
        cursor=db.rawQuery("select*from knowledge where id=?",new String[]{String.valueOf(knowId)});
        int rtn=0;
        if(cursor.moveToFirst()){
            rtn=cursor.getInt(cursor.getColumnIndex("subId"));
        }
        return rtn;
    }

    public void changeKnowledge(int knowId,String name,String annotation,List<LocalMedia>mediaList,callBackListener listener){
        try {
            db.execSQL("update knowledge set name=?,annotation=? where id=?",
                    new String[]{name,annotation, String.valueOf(knowId)});

            //先删掉已经有的图片
            db.execSQL("delete from know_pic where knowId=?",new String[]{String.valueOf(knowId)});
            //再添加更改后的
            addPictures(knowId,BaseItem.KNOWLEDGE_TYPE,mediaList);

            if (listener!=null)
                listener.onFinished();
        } catch (SQLException e) {
            if (listener!=null)
                listener.onError(e);
            e.printStackTrace();
        }
    }

    public BaseItem getProblemSetByProblem(int probId){
            cursor=db.rawQuery("select * from problem where id=?",new String[]{String.valueOf(probId)});
            BaseItem probSet=new BaseItem();
            if(cursor.moveToFirst()){
                int probSetId=cursor.getInt(cursor.getColumnIndex("probSetId"));
                Cursor csr=db.rawQuery("select * from problem_set where id=?",new String[]{String.valueOf(probSetId)});
                if(csr.moveToFirst()){
                    probSet.setId(probSetId);
                    probSet.setFatherId(csr.getInt(csr.getColumnIndex("fatherId")));
                    probSet.setType(BaseItem.PROBLEM_SET_TYPE);
                    probSet.setName(csr.getString(csr.getColumnIndex("name")));

                    csr.close();
                }
            }
            return probSet;
    }

    public boolean isExist(int subId){
        cursor=db.rawQuery("select*from subject where id=?",new String[]{String.valueOf(subId)});
        return cursor.moveToFirst();
    }

    public List<BaseItem>getSubjectList(){
        List<BaseItem>rtn=new ArrayList<>();
        try{
            cursor=db.rawQuery("select*from subject",null);
            if(cursor.moveToFirst()){
                do{
                    BaseItem item=new BaseItem();
                    item.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    item.setName(cursor.getString(cursor.getColumnIndex("name")));
                    item.setType(BaseItem.SUBJECT_TYPE);
                    item.setFatherId(cursor.getInt(cursor.getColumnIndex("fatherId")));
                    rtn.add(item);
                }while(cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rtn;
    }

    //查询科目的得分情况
    public Map<Integer, KnowledgeIssue> queryBySelection(List<Integer>selecedSubIds, int selecType, int orderType, String startDate, String endDate){
        //map的第一个参数是knowId,第二个是得分情况
        Map<Integer,KnowledgeIssue>map=new HashMap<>();
        int totalOccurences=0;
            try {
                //对所有指定科目循环
                for(int subId:selecedSubIds){
                    cursor=db.rawQuery("select*from problem where subId=?",new String[]{String.valueOf(subId)});
                    if(cursor.moveToFirst()){
                        do{
                            float grd=cursor.getFloat(cursor.getColumnIndex("grade"));
                            float tolGrd=cursor.getFloat(cursor.getColumnIndex("totalGrade"));
                            //查询每个习题对应的知识点情况
                            Cursor csr=db.rawQuery("select*from prob_know where probId=?",
                                    new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex("id")))});
                            if(csr.moveToFirst()){//如果该题包含有知识点
                                do{
                                    int knowId=csr.getInt(csr.getColumnIndex("knowId"));
                                    if(map.containsKey(knowId)){//如果该知识点已经在map里,就更新得分情况,否则新建一个得分并添加到map
                                        KnowledgeIssue issue= map.get(knowId);
                                        if(issue!=null) {
                                            issue.grade+=grd;
                                            issue.totalGrade+=tolGrd;
                                        }
                                    }else {
                                        Cursor c1=db.rawQuery("select*from knowledge where id=?",new String[]{String.valueOf(knowId)});
                                        if(c1.moveToFirst()){
                                            KnowledgeIssue issue = new KnowledgeIssue(c1.getString(c1.getColumnIndex("name")),grd,tolGrd);
                                            map.put(knowId,issue);
                                            c1.close();
                                        }
                                    }
                                    //每出现一次,查看次数+1
                                    map.get(knowId).occurrence++;
                                    totalOccurences++;
                                }while(csr.moveToNext());
                                csr.close();
                            }
                        }while (cursor.moveToNext());
                        cursor.close();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        KnowledgeIssue.totalOccurences=totalOccurences;
        return map;
    }
}
