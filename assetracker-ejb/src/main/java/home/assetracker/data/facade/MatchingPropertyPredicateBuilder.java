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
 */
public class MatchingPropertyPredicateBuilder<T> implements CustomPredicateBuilder {

    private T mustMatch;

    public MatchingPropertyPredicateBuilder(T mustMatch) {
        if (mustMatch == null) {
            throw new IllegalArgumentException("Given item to match cannot be null!");
        } else {
            this.mustMatch = mustMatch;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Predicate buildMatcher(CriteriaBuilder builder, Path property) {
        return builder.equal(property, mustMatch);
    }
}
