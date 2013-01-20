/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.facade;

import home.assetracker.data.entity.Asset;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author kenmin
 */
@Stateless
public class AssetFacade extends AbstractFacade<Asset> implements AssetFacadeLocal {

    @PersistenceContext
    private EntityManager entityManager;
    @EJB
    private UploadedFileFacadeLocal uploadedFileFacade;

    public AssetFacade() {
        super(Asset.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public void remove(Asset entity) {
        uploadedFileFacade.removeMultiple(uploadedFileFacade.findByPropertyValue("asset.id", entity.getId(), null));
        super.remove(entity);
    }
}
