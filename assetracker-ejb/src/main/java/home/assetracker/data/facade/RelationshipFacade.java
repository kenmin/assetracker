/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.facade;

import home.assetracker.data.entity.Relationship;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author kenmin
 */
@Stateless
public class RelationshipFacade extends AbstractFacade<Relationship> implements RelationshipFacadeLocal {

    @PersistenceContext
    private EntityManager entityManager;

    public RelationshipFacade() {
        super(Relationship.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
