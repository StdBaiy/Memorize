package stdbay.memorize.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MemorizeOpenHelper extends SQLiteOpenHelper {
    private static  final String CREATE_SUBJECT_TABLE="create table subject(" +//科目表
            "name text not null UNIQUE ,"+
            "id integer primary key autoincrement," +
            "fatherId integer," +
            "foreign key(fatherId)references subject(id))";
    private static final String CREATE_PROBLEMSET_TABLE="create table problem_set(" +//习题集表
            "name text not null UNIQUE ," +
            "id integer primary key autoincrement," +
            "subId integer not null," +
            "fatherId integer," +
            "createTime text," +//创建时间
            "viewTimes integer," +//查看次数
            "grade real," +//得分
            "totalGrade real," +//总分
            "FOREIGN KEY(subId)REFERENCES subject(id)," +
            "foreign key(fatherId)references problemSet(id))";

    private static final String CREATE_PROBLEM_TABLE="create table problem(" +//习题表
            "name text not null UNIQUE ," +
            "id integer primary key autoincrement," +
            "probSetId integer not null," +
            "subId integer not null," +
            "number integer," +//习题编号
            "createTime text," +
            "summary text," +
            "viewTimes integer," +
            "grade real," +
            "totalGrade real," +
            "foreign key(subId)references subject(id)," +
            "foreign key(probSetId)references problemSet(id))";

    private static final String CREATE_PROB_PIC_TABLE="create table prob_pic(" +//习题_图片表
            "probId integer," +
            "picPosition text," +//图片保存地址
            "foreign key(probId)references problem(id))";

    private static final String CREATE_KNOWLEDGE_TABLE="create table knowledge(" +//知识点表
            "id integer primary key autoincrement," +
            "fatherId integer,"+
            "name text not null UNIQUE," +
            "subId integer," +
            "annotation text," +//注解
            "foreign key(subId) references subject(id)," +
            "foreign key(fatherId)references knowledge(id))";

//    private static final String CREATE_KNOW_LEVEL_TABLE="create table know_level(" +//知识点层次表
//            "fatherId integer," +
//            "sonId integer," +
//            "foreign key (fatherId) references knowledge(id)," +
//            "foreign key (sonId) references knowledge(id))";

    private  static final String CREATE_PROB_KNOW_TABLE="create table prob_know(" +//习题_知识表
            "probId integer," +
            "knowId integer," +
            "foreign key (probId) references problem(id)," +
            "foreign key (knowId) references knowledge(id))";


    public MemorizeOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints 开启外键约束
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        try {

            db.execSQL(CREATE_SUBJECT_TABLE);
            db.execSQL(CREATE_PROBLEMSET_TABLE);
            db.execSQL(CREATE_PROBLEM_TABLE);
            db.execSQL(CREATE_PROB_PIC_TABLE);
            db.execSQL(CREATE_KNOWLEDGE_TABLE);
//        db.execSQL(CREATE_KNOW_LEVEL_TABLE);
            db.execSQL(CREATE_PROB_KNOW_TABLE);
//        } catch (SQLException e) {
//            Log.d("建表错误", Objects.requireNonNull(e.getMessage()));
//        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

}
