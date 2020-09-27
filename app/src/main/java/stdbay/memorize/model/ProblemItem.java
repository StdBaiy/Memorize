package stdbay.memorize.model;

import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

public class ProblemItem {
    private String number;
    private int id;
    private int subId;
    private int probSetId;
    private String grade;
    private String totalGrade;
    private String summary;
    private String createTime;
    private List<LocalMedia>pictures;


    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubId() {
        return subId;
    }

    public void setSubId(int subId) {
        this.subId = subId;
    }

    public int getProbSetId() {
        return probSetId;
    }

    public void setProbSetId(int probSetId) {
        this.probSetId = probSetId;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTotalGrade() {
        return totalGrade;
    }

    public void setTotalGrade(String totalGrade) {
        this.totalGrade = totalGrade;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<LocalMedia> getPictures() {
        return pictures;
    }

    public void setPictures(List<LocalMedia> pictures) {
        this.pictures = pictures;
    }
}
