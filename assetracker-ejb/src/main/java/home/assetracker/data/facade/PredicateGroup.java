/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.facade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author kenmin
 */
public final class PredicateGroup {

    private List<Object> operands = new ArrayList<Object>();
    private boolean conjunction;

    public PredicateGroup() {
    }

    public PredicateGroup(Predicate... operands) {
        this.operands.addAll(Arrays.asList(operands));
    }

    public void addOperand(Object operand) {
        this.operands.add(operand);
    }

    public void removeOperand(Object operand) {
        this.operands.remove(operand);
    }

    public Predicate resolveToPredicate(AbstractFacade facade, Root root, CriteriaBuilder builder) {
        Predicate result = null;
        for (Object operand : operands) {
            if (operand instanceof PredicateGroup) {
                PredicateGroup predicateGroup = (PredicateGroup) operand;
                if (predicateGroup.isConjunction()) {
                    result = (result != null) ? builder.and(result, predicateGroup.resolveToPredicate(facade, root, builder)) : predicateGroup.resolveToPredicate(facade, root, builder);
                } else {
                    result = (result != null) ? builder.or(result, predicateGroup.resolveToPredicate(facade, root, builder)) : predicateGroup.resolveToPredicate(facade, root, builder);
                }
            } else if (operand instanceof PropertyComparator) {
                PropertyComparator propertyComparator = (PropertyComparator) operand;
                if (propertyComparator.isConjunction()) {
                    result = (result != null) ? builder.and(result, propertyComparator.resolveToPredicate(facade, root, builder)) : propertyComparator.resolveToPredicate(facade, root, builder);
                } else {
                    result = (result != null) ? builder.or(result, propertyComparator.resolveToPredicate(facade, root, builder)) : propertyComparator.resolveToPredicate(facade, root, builder);
                }
            }
        }
        return result;
    }

    public boolean isConjunction() {
        return conjunction;
    }
}
