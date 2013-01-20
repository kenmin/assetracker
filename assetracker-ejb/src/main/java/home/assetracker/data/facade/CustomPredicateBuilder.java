/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.facade;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

/**
 *
 * @author Adams Tan
 * @author kenmin
 */
public interface CustomPredicateBuilder {

    /**
     * Build a predicate that is matched against the given property path.
     *
     * @param builder The criteria builder that is used to build the predicate.
     * @param property The given property path.
     * @return the custom predicate
     */
    Predicate buildMatcher(CriteriaBuilder builder, Path property);
}
