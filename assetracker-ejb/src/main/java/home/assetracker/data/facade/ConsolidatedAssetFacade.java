/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.facade;

import home.assetracker.data.entity.Asset;
import home.assetracker.data.entity.AssetGroup;
import home.assetracker.data.entity.ConsolidatedAsset;
import home.assetracker.data.entity.RelatableEntity;
import home.assetracker.data.entity.Relationship;
import home.assetracker.data.util.TreeNode;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author kenmin
 */
@Stateless
public class ConsolidatedAssetFacade extends AbstractFacade<ConsolidatedAsset> implements ConsolidatedAssetFacadeLocal {

    @PersistenceContext
    private EntityManager entityManager;
    @Inject
    private RelationshipFacadeLocal relationshipFacade;

    public ConsolidatedAssetFacade() {
        super(ConsolidatedAsset.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public TreeNode getAssetBreakDownTree(RelatableEntity root) {
        List<Relationship> relationships = relationshipFacade.findByPropertyValue("parent.id", root.getId(), null);
        RelatableEntity rootAssetGroup = root;
        if (rootAssetGroup != null) {
            TreeNode rootNode = new TreeNode(rootAssetGroup);
            if (relationships != null) {
                for (Relationship relationship : relationships) {
                    RelatableEntity child = relationship.getChild();
                    TreeNode childNode = null;
                    if (child instanceof AssetGroup) {
                        childNode = getAssetBreakDownTree(child);
                    } else if (child instanceof Asset) {
                        childNode = new TreeNode(child);
                    }

                    if (childNode != null) {
                        rootNode.addChild(childNode);
                    }
                }
            }
            return rootNode;
        }
        return null;
    }
}
