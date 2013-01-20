/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.util;

import home.assetracker.data.entity.RelatableEntity;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kenmin
 */
public class TreeNode {

    private RelatableEntity entity;
    private Map<Long, TreeNode> children;

    public TreeNode() {
        children = new HashMap<Long, TreeNode>();
    }

    public TreeNode(RelatableEntity entity) {
        this();
        this.entity = entity;
    }

    public TreeNode addChild(TreeNode child) {
        return children.put(child.getEntity().getId(), child);
    }

    public TreeNode addChild(TreeNode child, TreeNode parent) {
        TreeNode node = find(parent);
        if (node != null) {
            return node.addChild(child);
        } else {
            return null;
        }
    }

    public TreeNode find(TreeNode node) {
        if (!children.isEmpty()) {
            if (children.containsKey(node.getEntity().getId())) {
                return children.get(node.getEntity().getId());
            } else {
                TreeNode searchResult = null;
                for (Map.Entry<Long, TreeNode> child : children.entrySet()) {
                    searchResult = child.getValue().find(node);
                    if (searchResult != null) {
                        break;
                    }
                }
                return searchResult;
            }
        } else {
            return null;
        }
    }

    public RelatableEntity getEntity() {
        return entity;
    }

    public void setEntity(RelatableEntity entity) {
        this.entity = entity;
    }

    public Map<Long, TreeNode> getChildren() {
        return children;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TreeNode) {
            TreeNode other = (TreeNode) obj;
            return this.getEntity().getId().equals(other.getEntity().getId());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 17;
        return hash;
    }
}
