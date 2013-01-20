/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.facade;

import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

/**
 *
 * @author kenmin
 */
public class NotInPredicateBuilder<T> implements CustomPredicateBuilder {
    
    private List<T> excludeBy;

    public NotInPredicateBuilder(List<T> excludeBy) {
        this.excludeBy = excludeBy;
    }

    @Override
    public Predicate buildMatcher(CriteriaBuilder builder, Path property) {
        return property.in(excludeBy).not();
    }
    
}
