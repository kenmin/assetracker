/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.facade;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This interface contains methods that allow records to be filtered according
 * to their indexes. Useful for extending PrimeFaces' LazyDataModel.
 *
 * @author Adams Tan
 */
public interface IndexRangeRetrievable<T> {

    Class<T> getEntityClass();

    /**
     * Retrieve a specific range of records.
     *
     * @param start index of the first record to fetch
     * @param max the maximum records to fetch
     * @return the matching records
     */
    List<T> findIndexRange(int start, int max);

    /**
     * Retrieve a specific range of records, that is ordered.
     *
     * @param start index of the first record to fetch
     * @param max the maximum records to fetch
     * @param sortCriteria an ordered hash map of whose key contains the
     * property to be sorted, and value contains the desired sort order
     * (ascending when true, descending when false)
     * @return the matching records
     */
    List<T> findIndexRange(int start, int max, LinkedHashMap<String, Boolean> sortCriteria);

    /**
     * Retrieve a specific range of records, that is ordered and filtered.
     * Number of records that fulfill the filter criteria but not retrieved (due
     * to range constraints) can be obtained.
     *
     * @param start index of the first record to fetch
     * @param max the maximum records to fetch
     * @param sortCriteria an ordered hash map of whose key contains the
     * property to be sorted, and value contains the desired sort order
     * (ascending when true, descending when false)
     * @param filterCriteria key-value map, containing the property name and
     * value to be matched; if value is of String type, then the matching would
     * be done in 'contains' mode.
     * @param matchingCount a placeholder to store the count of records that
     * would have satisfied the {@code matchEntries} if start/max constraints
     * were not in place.
     * @return the matching records
     */
    List<T> findIndexRange(int start, int max, LinkedHashMap<String, Boolean> sortCriteria,
            Map<String, Object> filterCriteria, AtomicInteger matchingCount);
}
