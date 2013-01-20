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
public class InPredicateBuilder<T> implements CustomPredicateBuilder {

    private List<T> filterBy;

    public InPredicateBuilder(List<T> filterBy) {
        if (filterBy == null || filterBy.isEmpty()) {
            throw new IllegalArgumentException("Items to compare cannot be null or empty!");
        } else {
            this.filterBy = filterBy;
        }
    }

    @Override
    public Predicate buildMatcher(CriteriaBuilder builder, Path property) {
        return property.in(filterBy);
    }
}
