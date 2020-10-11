package stdbay.memorize.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import stdbay.memorize.util.Util;

public class MemorizeOpenHelper extends SQLiteOpenHelper {
    private static  final String CREATE_SUBJECT_TABLE="create table subject(" +//科目表
            "name text not null UNIQUE ,"+
            "id integer primary key autoincrement," +
            "fatherId integer," +
            "foreign key(fatherId)references subject(id) on delete cascade)";

    private static final String CREATE_PROBLEM_SET_TABLE="create table problem_set(" +//习题集表
            "name text not null," +
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
            "name text not null," +
            "subId integer," +
            "annotation text," +//注解
            "foreign key(subId) references subject(id) on delete cascade," +
            "foreign key(fatherId)references knowledge(id) on delete cascade)";

    private static final String CREATE_KNOW_PIC_TABLE="create table know_pic(" +//知识_图片表
            "knowId integer," +
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
            "foreign key(knowId)references knowledge(id)on delete cascade)";

    private  static final String CREATE_PROB_KNOW_TABLE="create table prob_know(" +//习题_知识表
            "probId integer," +
            "knowId integer," +
            "name text," +
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
        try {

            db.execSQL(CREATE_SUBJECT_TABLE);
            db.execSQL(CREATE_PROBLEM_SET_TABLE);
            db.execSQL(CREATE_PROBLEM_TABLE);
            db.execSQL(CREATE_PROB_PIC_TABLE);
            db.execSQL(CREATE_KNOWLEDGE_TABLE);
            db.execSQL(CREATE_KNOW_PIC_TABLE);
            db.execSQL(CREATE_PROB_KNOW_TABLE);
            db.execSQL("insert into subject (name,id) values('数学 Ⅰ',1)");
            db.execSQL("insert into subject (name,id,fatherId) values('高等数学',2,1)");
            db.execSQL("insert into subject (name,id,fatherId) values('线性代数',3,1)");
            db.execSQL("insert into subject (name,id,fatherId) values('概率论与数理统计',4,1)");
            db.execSQL("insert into subject (name,id,fatherId) values('函数',5,2)");
            db.execSQL("insert into subject (name,id,fatherId) values('数列极限',6,2)");

            db.execSQL("insert into problem_set (name,id,subId,createTime) values('8套卷',1,2,(select date('now')))");
            db.execSQL("insert into problem_set (name,id,subId,createTime) values('600题',2,2,(select date('now')))");

            db.execSQL("insert into knowledge (id,name,subId,annotation)values(1,'函数的概念和特征',5,'')");
            db.execSQL("insert into knowledge (id,name,subId,annotation)values(2,'函数的图像',5,'')");
            db.execSQL("insert into knowledge (id,name,fatherId,subId,annotation)values(3,'函数',1,5,'')");
            db.execSQL("insert into knowledge (id,name,fatherId,subId,annotation)values(4,'反函数',1,5,'')");
            db.execSQL("insert into knowledge (id,name,fatherId,subId,annotation)values(5,'复合函数',1,5,'')");
            db.execSQL("insert into knowledge (id,name,fatherId,subId,annotation)values(6,'四种特性',1,5,'')");
            db.execSQL("insert into knowledge (id,name,fatherId,subId,annotation)values(7,'有界性',6,5,'')");
            db.execSQL("insert into knowledge (id,name,fatherId,subId,annotation)values(8,'单调性',6,5,'')");
            db.execSQL("insert into knowledge (id,name,fatherId,subId,annotation)values(9,'奇偶性',6,5,'')");
            db.execSQL("insert into knowledge (id,name,fatherId,subId,annotation)values(10,'周期性',6,5,'')");
            db.execSQL("insert into knowledge (id,name,fatherId,subId,annotation)values(11,'直角坐标系',5,5,'')");
            db.execSQL("insert into knowledge (id,name,fatherId,subId,annotation)values(12,'极坐标系',5,5,'')");
            db.execSQL("insert into knowledge (id,name,fatherId,subId,annotation)values(13,'参数方程',5,5,'')");

            db.execSQL("insert into knowledge (id,name,subId,annotation)values(14,'定义',6,'')");
            db.execSQL("insert into knowledge (id,name,subId,annotation)values(15,'性质',6,'')");
            db.execSQL("insert into knowledge (id,name,subId,annotation)values(16,'运算规则',6,'')");
            db.execSQL("insert into knowledge (id,name,subId,annotation)values(17,'夹逼准则',6,'')");
            db.execSQL("insert into knowledge (id,name,subId,annotation)values(18,'单调有界准则',6,'')");
            db.execSQL("insert into knowledge (id,name,fatherId,subId,annotation)values(19,'唯一性',15,6,'')");
            db.execSQL("insert into knowledge (id,name,fatherId,subId,annotation)values(20,'有界性',15,6,'')");
            db.execSQL("insert into knowledge (id,name,fatherId,subId,annotation)values(21,'保号性',15,6,'')");

            for (int i=0;i<20;++i){
                db.execSQL("insert into problem (probSetId,subId,number,createTime,grade,totalGrade) values(1,2,?,(select date('now')),?,?)",
                        new String[]{String.valueOf(i+1), String.valueOf(Util.getRandom(0,5)),"5"});
                db.execSQL("insert into prob_know (probId,knowId) values(?,?)",
                        new String[]{String.valueOf(i+1), String.valueOf(((int)(Math.random()*20))%20+1)});
                db.execSQL("insert into prob_know (probId,knowId) values(?,?)",
                        new String[]{String.valueOf(i+1), String.valueOf(((int)(Math.random()*20))%20+1)});
                db.execSQL("insert into prob_know (probId,knowId) values(?,?)",
                        new String[]{String.valueOf(i+1), String.valueOf(((int)(Math.random()*20))%20+1)});
                db.execSQL("insert into prob_know (probId,knowId) values(?,?)",
                        new String[]{String.valueOf(i+1), String.valueOf(((int)(Math.random()*20))%20+1)});
            }

            for (int i=0;i<10;++i){
                db.execSQL("insert into problem (probSetId,subId,number,createTime,grade,totalGrade) values(2,2,?,(select date('now')),?,?)",
                        new String[]{String.valueOf(i+1), String.valueOf(Util.getRandom(0,5)),"5"});
                db.execSQL("insert into prob_know (probId,knowId) values(?,?)",
                        new String[]{String.valueOf(i+21), String.valueOf(((int)(Math.random()*20))%20+1)});
                db.execSQL("insert into prob_know (probId,knowId) values(?,?)",
                        new String[]{String.valueOf(i+21), String.valueOf(((int)(Math.random()*20))%20+1)});
                db.execSQL("insert into prob_know (probId,knowId) values(?,?)",
                        new String[]{String.valueOf(i+21), String.valueOf(((int)(Math.random()*20))%20+1)});
                db.execSQL("insert into prob_know (probId,knowId) values(?,?)",
                        new String[]{String.valueOf(i+21), String.valueOf(((int)(Math.random()*20))%20+1)});
            }
//            for(int i=0;i<18;++i){
//                db.execSQL("insert into problem (probSetId,subId,number,createTime,grade,totalGrade) values(2,2,?,(select date('now')),?,?)",
//                        new String[]{String.valueOf(i+1), String.valueOf(Util.getRandom(0,5)),"5"});
//                db.execSQL("insert into prob_know (probId,knowId) values(?,?)",
//                        new String[]{String.valueOf(i+11), String.valueOf(((int)(Math.random()*20))%20+1)});
//                db.execSQL("insert into prob_know (probId,knowId) values(?,?)",
//                        new String[]{String.valueOf(i+111), String.valueOf(((int)(Math.random()*20))%20+1)});
//                db.execSQL("insert into prob_know (probId,knowId) values(?,?)",
//                        new String[]{String.valueOf(i+11), String.valueOf(((int)(Math.random()*20))%20+1)});
//                db.execSQL("insert into prob_know (probId,knowId) values(?,?)",
//                        new String[]{String.valueOf(i+11), String.valueOf(((int)(Math.random()*20))%20+1)});
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }



}
