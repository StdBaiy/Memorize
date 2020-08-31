package stdbay.memorize.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import stdbay.memorize.db.MemorizeOpenHelper;

public class MemorizeDB {
    public static final String DB_NAME="memorize";
    public static final int VERSION=1;
    private static final int NO_FATHER =-1;

    private static MemorizeDB memorizeDB;
    private SQLiteDatabase db;

    private MemorizeDB(Context context){
        MemorizeOpenHelper helper=new MemorizeOpenHelper(context,DB_NAME,null,VERSION);
        db=helper.getWritableDatabase();
    }

    //获取MemorizeDB实例
    public synchronized static MemorizeDB getInstance(Context context){
        if(memorizeDB==null){
            memorizeDB=new MemorizeDB(context);
        }
        return memorizeDB;
    }


    public void saveSubject(String name,String fatherId){
        if(TextUtils.isEmpty(fatherId))
            fatherId="null";
        try{
            db.execSQL("insert into subject (name,fatherId) values(?,?)",new String[]{name,fatherId});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //获取subject列表
    @SuppressLint("Recycle")
    public List<Subject>loadSubject(int fatherId){
        List<Subject>list=new ArrayList<Subject>();
        //fatherId=-1,说明是首页,那么需要选出没有父节点的科目,反之需要选择父节点
        Cursor cursor;
        if(fatherId==NO_FATHER){
            cursor=db.query("subject",null,"fatherId=null",
                null,null,null,null);
        }else{
            cursor=db.query("subject",null,"fatherId=?",
                    new String[]{String.valueOf(fatherId)},null,null,null);
        }

        if(cursor.moveToFirst()){
            do{
                Subject subject=new Subject();
                subject.setId(cursor.getInt(cursor.getColumnIndex("id")));
                subject.setName(cursor.getString(cursor.getColumnIndex("name")));
                subject.setFatherId(cursor.getInt(cursor.getColumnIndex("fatherId")));
                list.add(subject);
            }while(cursor.moveToNext());
        }
        return list;
    }

    //获取problemSet列表
    @SuppressLint("Recycle")
    public List<ProblemSet>loadProblemSet(int fatherId){
        List<ProblemSet>list=new ArrayList<ProblemSet>();
        //fatherId=-1,说明是首页,那么需要选出没有父节点的科目,反之需要选择父节点
        Cursor cursor;
        if(fatherId==NO_FATHER){
            cursor=db.query("problem_set",null,"fatherId=null",
                    null,null,null,null);
        }else{
            cursor=db.query("subject",null,"fatherId=?",
                    new String[]{String.valueOf(fatherId)},null,null,null);
        }

        if(cursor.moveToFirst()){
            do{
                ProblemSet problemSet=new ProblemSet();
                problemSet.setId(cursor.getInt(cursor.getColumnIndex("id")));
                problemSet.setSubId(cursor.getInt(cursor.getColumnIndex("subId")));
                problemSet.setName(cursor.getString(cursor.getColumnIndex("name")));
                problemSet.setFatherId(cursor.getInt(cursor.getColumnIndex("fatherId")));
                problemSet.setCreateTime(cursor.getString(cursor.getColumnIndex("createTime")));
                problemSet.setViewTimes(cursor.getInt(cursor.getColumnIndex("viewTimes")));
                problemSet.setGrade(cursor.getFloat(cursor.getColumnIndex("grade")));
                problemSet.setTotalGrade(cursor.getFloat(cursor.getColumnIndex("totalGrade")));
                list.add(problemSet);
            }while(cursor.moveToNext());
        }
        return list;
    }

    //获取problem列表
    @SuppressLint("Recycle")
    public List<Problem>loadProblem(int probSetId){
        List<Problem>list=new ArrayList<Problem>();
        //fatherId=-1,说明是首页,那么需要选出没有父节点的科目,反之需要选择父节点
        Cursor cursor;
        cursor=db.query("subject",null,"fatherId=?",
            new String[]{String.valueOf(probSetId)},null,null,null);

        if(cursor.moveToFirst()){
            do{
                Problem problem=new Problem();
                problem.setId(cursor.getInt(cursor.getColumnIndex("id")));
                problem.setSubId(cursor.getInt(cursor.getColumnIndex("subId")));
                problem.setName(cursor.getString(cursor.getColumnIndex("name")));
                problem.setProbSetId(cursor.getInt(cursor.getColumnIndex("probSetId")));
                problem.setCreateTime(cursor.getString(cursor.getColumnIndex("createTime")));
                problem.setSummary(cursor.getString(cursor.getColumnIndex("summary")));
                problem.setNumber(cursor.getInt(cursor.getColumnIndex("number")));
                problem.setViewTimes(cursor.getInt(cursor.getColumnIndex("viewTimes")));
                problem.setGrade(cursor.getFloat(cursor.getColumnIndex("grade")));
                problem.setTotalGrade(cursor.getFloat(cursor.getColumnIndex("totalGrade")));
                list.add(problem);
            }while(cursor.moveToNext());
        }
        return list;
    }

    //获取knowledge列表
    @SuppressLint("Recycle")
    public List<Knowledge>loadKnowledge(int fatherId){
        List<Knowledge>list=new ArrayList<Knowledge>();
        //fatherId=-1,说明是首页,那么需要选出没有父节点的科目,反之需要选择父节点
        Cursor cursor;
        if(fatherId==NO_FATHER){
            cursor=db.query("knowledge",null,"fatherId=null",
                    null,null,null,null);
        }else{
            cursor=db.query("knowledge",null,"fatherId=?",
                    new String[]{String.valueOf(fatherId)},null,null,null);
        }

        if(cursor.moveToFirst()){
            do{
                Knowledge knowledge=new Knowledge();
                knowledge.setId(cursor.getInt(cursor.getColumnIndex("id")));
                knowledge.setSubId(cursor.getInt(cursor.getColumnIndex("subId")));
                knowledge.setName(cursor.getString(cursor.getColumnIndex("name")));
                knowledge.setFatherId(cursor.getInt(cursor.getColumnIndex("fatherId")));
                knowledge.setAnnotation(cursor.getString(cursor.getColumnIndex("annotation")));
                list.add(knowledge);
            }while(cursor.moveToNext());
        }
        return list;
    }
}
