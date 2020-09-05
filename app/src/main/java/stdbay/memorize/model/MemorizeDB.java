package stdbay.memorize.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import stdbay.memorize.db.MemorizeOpenHelper;

public class MemorizeDB {

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

    //获取MemorizeDB实例
    public synchronized static MemorizeDB getInstance(Context context){
        if(memorizeDB==null){
            memorizeDB=new MemorizeDB(context);
        }
        return memorizeDB;
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
            case BaseItem.PROBLEM_SET_TYPE:
                break;
        }
        return list;
    }

    public void addItem(final BaseItem father, final String name, String type, final callBackListener listener){
        switch(type){
            case "subject":
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
            case "probSet":
                new Thread(new Runnable() {
                    @SuppressLint("Recycle")
                    @Override
                    public void run() {
                        try{
                            if(father.getType()==BaseItem.SUBJECT_TYPE){
                                db.execSQL("insert into problem_set (name,subId,createTime,viewTimes,grade,totalGrade)" +
                                        "values (?,?,(select date('now')),0,0,0)",new String[]{name, String.valueOf(father.getId())});
                            }else if(father.getType()==BaseItem.PROBLEM_SET_TYPE){
                                Cursor cursor;
                                int subId=0;
                                cursor =db.rawQuery("select*from problem_set where id=?",new String[]{String.valueOf(father.getId())}); //子节点的subId和父节点相同
                                if (cursor.moveToFirst()) {
                                    subId=cursor.getInt(cursor.getColumnIndex("fatherId"));
                                }
                                db.execSQL("insert into problem_set (name,subId,fatherId,createTime,viewTimes,grade,totalGrade)" +
                                        "values(?,?,?,(select date('now')),0,0,0)",
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
        }
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
        return rtn;

    }


    private void queryFromCursorToList(int type){
        if(cursor.moveToFirst()) do {
            BaseItem baseItem = new BaseItem();
            baseItem.setType(type);
            baseItem.setId(cursor.getInt(cursor.getColumnIndex("id")));
            baseItem.setName(cursor.getString(cursor.getColumnIndex("name")));
            list.add(baseItem);
        } while (cursor.moveToNext());
    };

    public String getFatherName(){
return null;
    }

    public interface callBackListener{
        void onFinished();
        void onError(Exception e);
    }
}
