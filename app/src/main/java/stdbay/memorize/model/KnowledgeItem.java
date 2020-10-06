package stdbay.memorize.model;

import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

public class KnowledgeItem {
    private List<LocalMedia> pictures;
    //涉及到的知识点
    private List<BaseItem> problems;
    private int id;
    private int fatherId;
    private String name;
    private String annotation;

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFatherId() {
        return fatherId;
    }

    void setFatherId(int fatherId) {
        this.fatherId = fatherId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<BaseItem> getProblems() {
        return problems;
    }

    void setProblems(List<BaseItem> problems) {
        this.problems = problems;
    }

    public List<LocalMedia> getPictures() {
        return pictures;
    }

    void setPictures(List<LocalMedia> pictures) {
        this.pictures = pictures;
    }
}
