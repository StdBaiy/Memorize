package stdbay.memorize.model;

import java.util.List;

public class TreeNode {
    public static int treeNodeIntervalY = 150;
    public static int treeNodeIntervalX=300;
    public static int treeNodeW=150;
    public static int treeNodeH = 100;
    public int x=0,y=0;

    private int id;
    private TreeNode father=null;
    private List<TreeNode>children;
    private String name;

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TreeNode getFather() {
        return father;
    }

    public void setFather(TreeNode father) {
        this.father = father;
    }
}
