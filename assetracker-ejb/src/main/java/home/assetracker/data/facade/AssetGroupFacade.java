/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.facade;

import home.assetracker.data.entity.AssetGroup;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author kenmin
 */
@Stateless
public class AssetGroupFacade extends AbstractFacade<AssetGroup> implements AssetGroupFacadeLocal {

    @PersistenceContext
    private EntityManager entityManager;

    public AssetGroupFacade() {
        super(AssetGroup.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
