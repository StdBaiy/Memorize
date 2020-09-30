package stdbay.memorize.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MemorizeOpenHelper extends SQLiteOpenHelper {
    private static  final String CREATE_SUBJECT_TABLE="create table subject(" +//科目表
            "name text not null UNIQUE ,"+
            "id integer primary key autoincrement," +
            "fatherId integer," +
            "foreign key(fatherId)references subject(id) on delete cascade)";

    private static final String CREATE_PROBLEM_SET_TABLE="create table problem_set(" +//习题集表
            "name text not null UNIQUE ," +
            "id integer primary key autoincrement," +
            "subId integer not null," +
            "fatherId integer," +
            "createTime date," +//创建时间
            "viewTimes integer," +//查看次数
//            "grade decimal(10,1)," +//得分
//            "totalGrade decimal(10,1)," +//总分
            "foreign key(subId)REFERENCES subject(id) on delete cascade," +
            "foreign key(fatherId)references problem_set(id) on delete cascade)";

    private static final String CREATE_PROBLEM_TABLE="create table problem(" +//习题表
            "id integer primary key," +
            "probSetId integer not null," +
            "subId integer not null," +
            "number integer," +//习题编号
            "createTime date," +
            "summary text," +
            "viewTimes integer," +
            "grade decimal(10,1)," +
            "totalGrade decimal(10,1)," +
            "foreign key(subId)references subject(id) on delete cascade," +
            "foreign key(probSetId)references problem_set(id) on delete cascade) ";

    private static final String CREATE_PROB_PIC_TABLE="create table prob_pic(" +//习题_图片表
            "probId integer," +
            "path text," +//图片保存地址
            "compressPath text," +
            "cutPath text," +
            "realPath text," +
            "id integer," +
            "originalPath text," +
            "duration int," +
            "isChecked integer," +
            "androidQToPath text,"+
            "isCut integer," +
            "position integer," +
            "num integer," +
            "mimeType text," +
            "chooseModel integer," +
            "compressed integer," +
            "width integer," +
            "height integer," +
            "size integer," +
            "isOriginal integer," +
            "fileName text," +
            "parentFolderName text," +
            "orientation integer," +
            "loadLongImageStatus integer," +
            "isLongImage integer," +
            "bucketId integer," +
            "isMaxSelectEnabledMask integer,"+
            "foreign key(probId)references problem(id)on delete cascade)";

    private static final String CREATE_KNOWLEDGE_TABLE="create table knowledge(" +//知识点表
            "id integer primary key autoincrement," +
            "fatherId integer,"+
            "name text not null UNIQUE," +
            "subId integer," +
            "annotation text," +//注解
            "foreign key(subId) references subject(id) on delete cascade," +
            "foreign key(fatherId)references knowledge(id) on delete cascade)";

    private  static final String CREATE_PROB_KNOW_TABLE="create table prob_know(" +//习题_知识表
            "probId integer," +
            "knowId integer," +
            "foreign key (probId) references problem(id) on delete cascade," +
            "foreign key (knowId) references knowledge(id) on delete cascade)";


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
            db.execSQL(CREATE_PROBLEM_SET_TABLE);
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
