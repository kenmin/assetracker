/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.facade;

import home.assetracker.data.common.AppConfig;
import home.assetracker.data.entity.UploadedFile;
import home.assetracker.data.util.FileUtils;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author kenmin
 */
@Stateless
public class UploadedFileFacade extends AbstractFacade<UploadedFile> implements UploadedFileFacadeLocal {

    @PersistenceContext
    private EntityManager entityManager;

    public UploadedFileFacade() {
        super(UploadedFile.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public void remove(UploadedFile entity) {
        FileUtils.deleteFile(entity.getFileName(), AppConfig.getInstance().getPropertyAsString("uploadedFile.uploadPath"));
        super.remove(entity);
    }
}
