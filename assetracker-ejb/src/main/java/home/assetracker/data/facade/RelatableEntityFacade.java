/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.facade;

import home.assetracker.data.entity.RelatableEntity;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author kenmin
 */
@Stateless
public class RelatableEntityFacade extends AbstractFacade<RelatableEntity> implements RelatableEntityFacadeLocal {

    @PersistenceContext
    private EntityManager entityManager;

    public RelatableEntityFacade() {
        super(RelatableEntity.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
