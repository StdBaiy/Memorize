package stdbay.memorize.model;

import java.util.List;

public class TreeInfo{
    private TreeNode root;
    private List<List<TreeNode>>treeLevel;

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public List<List<TreeNode>> getTreeLevel() {
        return treeLevel;
    }

    public void setTreeLevel(List<List<TreeNode>> treeLevel) {
        this.treeLevel = treeLevel;
    }
}