package stdbay.memorize.model;

public class Grade {
    private float grade;
    private float totalGrade;

    public Grade(float grade, float totalGrade){
        this.grade=grade;
        this.totalGrade=totalGrade;
    }

    public float getTotalGrade() {
        return totalGrade;
    }

    public void setTotalGrade(float totalGrade) {
        this.totalGrade = totalGrade;
    }

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }
}
