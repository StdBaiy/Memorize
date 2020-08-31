package stdbay.memorize.model;

public class Problem {
    private int id;
    private int subId;
    private int probSetId;
    private String name;
    private String createTime;
    private int viewTimes;
    private  float grade;
    private  float totalGrade;
    private int number;
    private String summary;

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getSubId() {
        return subId;
    }

    public int getProbSetId() {
        return probSetId;
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

    public int getNumber() {
        return number;
    }

    public String getSummary() {
        return summary;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSubId(int subId) {
        this.subId = subId;
    }

    public void setProbSetId(int id) {
        this.probSetId = id;
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

    public void setNumber(int number) {
        this.number = number;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
