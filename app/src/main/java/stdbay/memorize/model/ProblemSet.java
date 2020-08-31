package stdbay.memorize.model;

public class ProblemSet {
    private int id;
    private int subId;
    private int fatherId;
    private String name;
    private String createTime;
    private int viewTimes;
    private  float grade;
    private  float totalGrade;

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getSubId() {
        return subId;
    }

    public int getFatherId() {
        return fatherId;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setSubId(int subId) {
        this.subId = subId;
    }

    public void setFatherId(int fatherId) {
        this.fatherId = fatherId;
    }

    public void setName(String name) {
        this.name = name;
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
