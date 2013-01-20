/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.facade;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author kenmin
 */
public final class PropertyComparator {

    public enum ComparisonOperator {

        LESS_THAN, LESS_THAN_EQUAL_TO, EQUAL, MORE_THAN, MORE_THAN_EQUAL_TO
    }
    private String propName;
    private ComparisonOperator comparisonOperator;
    private Comparable comparedValue;
    private boolean conjunction;

    private PropertyComparator(String propName, ComparisonOperator comparisonOperator, Comparable comparedValue, boolean conjunction) {
        this.propName = propName;
        this.comparisonOperator = comparisonOperator;
        this.comparedValue = comparedValue;
        this.conjunction = conjunction;
    }

    public static PropertyComparator lessThan(String propName, Comparable comparedValue, boolean conjunction) {
        return new PropertyComparator(propName, ComparisonOperator.LESS_THAN, comparedValue, conjunction);
    }

    public static PropertyComparator lessThanEqualTo(String propName, Comparable comparedValue, boolean conjunction) {
        return new PropertyComparator(propName, ComparisonOperator.LESS_THAN_EQUAL_TO, comparedValue, conjunction);
    }

    public static PropertyComparator equal(String propName, Comparable comparedValue, boolean conjunction) {
        return new PropertyComparator(propName, ComparisonOperator.EQUAL, comparedValue, conjunction);
    }

    public static PropertyComparator moreThan(String propName, Comparable comparedValue, boolean conjunction) {
        return new PropertyComparator(propName, ComparisonOperator.MORE_THAN, comparedValue, conjunction);
    }

    public static PropertyComparator moreThanEqualTo(String propName, Comparable comparedValue, boolean conjunction) {
        return new PropertyComparator(propName, ComparisonOperator.MORE_THAN_EQUAL_TO, comparedValue, conjunction);
    }

    public Comparable getComparedValue() {
        return comparedValue;
    }

    public ComparisonOperator getComparisonOperator() {
        return comparisonOperator;
    }

    public String getPropName() {
        return propName;
    }

    public boolean isConjunction() {
        return conjunction;
    }

    public Predicate resolveToPredicate(AbstractFacade facade, Root root, CriteriaBuilder builder) {
        Path path = facade.resolvePathString(root, propName);
        Predicate result = builder.conjunction();
        if (this.propName != null && !this.propName.trim().equals("")
                && this.comparisonOperator != null && this.comparedValue != null) {
            switch (this.comparisonOperator) {
                case LESS_THAN:
                    result = builder.lessThan(path, comparedValue);
                    break;
                case LESS_THAN_EQUAL_TO:
                    result = builder.lessThanOrEqualTo(path, comparedValue);
                    break;
                case EQUAL:
                    result = builder.equal(path, comparedValue);
                    break;
                case MORE_THAN:
                    result = builder.greaterThan(path, comparedValue);
                    break;
                case MORE_THAN_EQUAL_TO:
                    result = builder.greaterThanOrEqualTo(path, comparedValue);
            }
        }
        return result;
    }
}
