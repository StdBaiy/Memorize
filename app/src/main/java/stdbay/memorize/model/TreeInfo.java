package stdbay.memorize.model;

import java.util.List;

public class TreeInfo{
    private TreeNode root;
    private List<Integer>treeLevel;

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public List<Integer> getTreeLevel() {
        return treeLevel;
    }

    public void setTreeLevel(List<Integer> treeLevel) {
        this.treeLevel = treeLevel;
    }
}