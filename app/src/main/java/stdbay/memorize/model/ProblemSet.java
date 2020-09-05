package stdbay.memorize.model;

public class ProblemSet extends BaseItem {
    private int subId;
    private String createTime;
    private int viewTimes;
    private  float grade;
    private  float totalGrade;

    public int getSubId() {
        return subId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public int getViewTimes() {
        return viewTimes;
    }

    public float getGrade() {
        return grade;
    }

    public float getTotalGrade() {
        return totalGrade;
    }

    public void setSubId(int subId) {
        this.subId = subId;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setViewTimes(int viewTimes) {
        this.viewTimes = viewTimes;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }

    public void setTotalGrade(float totalGrade) {
        this.totalGrade = totalGrade;
    }
}
