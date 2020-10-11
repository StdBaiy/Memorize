package stdbay.memorize.model;

/**
 * 本类是用于统计查询时,记载知识点一些特征的
 */

public class KnowledgeIssue {
    public String name;
    public float grade=0;
    public float totalGrade=0;
    public int viewTimes=0;
    public int occurrence=0;
    public String createTime;
    public static int totalOccurences;

    public KnowledgeIssue(String name,float grade, float totalGrade){
        this.name=name;
        this.grade=grade;
        this.totalGrade=totalGrade;
    }
}
