/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.facade;

import home.assetracker.data.entity.ConsolidatedAsset;
import home.assetracker.data.entity.RelatableEntity;
import home.assetracker.data.util.TreeNode;
import javax.ejb.Local;

/**
 *
 * @author kenmin
 */
@Local
public interface ConsolidatedAssetFacadeLocal extends FacadeLocal<ConsolidatedAsset> {

    TreeNode getAssetBreakDownTree(RelatableEntity root);
}
