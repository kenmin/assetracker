/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.facade;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Local Interface for Abstract Facade
 *
 * @author Adams Tan
 * @author kenmin
 */
public interface FacadeLocal<T> extends IndexRangeRetrievable<T> {

    /**
     * This offset is used to widen date inclusive range search for MS SQL
     * datetime columns that store time to the nearest 3.33 milliseconds
     */
    public static final long DATE_INCLUSIVE_OFFSET_MS = 4L;

    /**
     * Count the total number of objects that can be retrieved.
     *
     * @return The count value
     */
    int count();

    /**
     * Count the number of objects that can be retrieved after applying filters.
     *
     * @param filterCriteria key-value map, where the key contains the property
     * name to be filtered, and the value contains the filter value ('contains'
     * mode).
     * @return The count value
     */
    int count(Map<String, Object> filterCriteria);

    /**
     * Count the number of records that fall within a comparable range
     * (projection)
     *
     * @param propName The property to be compared
     * @param min The minimum acceptable value of the property. If null, only
     * {@code max} will be used.
     * @param max The maximum acceptable value of the property. If null, only
     * {@code min} will be used.
     * @param inclusive specifies whether the range includes the boundaries (min
     * and max)
     * @return The number of records that match this criteria.
     */
    int count(String propName, Comparable<?> min, Comparable<?> max, boolean inclusive);

    /**
     * Persist an entity to the database.
     *
     * @param entity The entity to be persisted.
     */
    void create(T entity);

    /**
     * Update an entity to the database.
     *
     * @param entity The entity to be updated.
     */
    void edit(T entity);

    /**
     * Remove an entity permanently from database.
     *
     * @param entity The entity to be removed.
     */
    void remove(T entity);
    
    /**
     * Remove multiple entities permanently from database.
     *
     * @param entities A list of entities to be removed.
     */
    void removeMultiple(List<T> entities);

    /**
     * Find an entity by ID.
     *
     * @param id The entity's ID.
     * @return The found entity (if any).
     */
    T find(long id);

    /**
     * Find an entity by ID and load some of its properties eagerly.
     *
     * @param id The entity's ID.
     * @param eagerProps The properties to be loaded eagerly. This requires that
     * the properties have associated accessor methods (getXx).
     * @return The found entity (if any).
     */
    T findEager(long id, List<String> eagerProps);

    /**
     * Find all entities of this type.
     *
     * @return A list of all entities (if any).
     */
    List<T> findAll();

    /**
     * Find entities by one property (analogous to findProperties)
     *
     * @param propName The property name.
     * @param value The desired property value (a {@code List} of values is
     * acceptable)
     * @param sortCriteria An ordered hash map of entries where key contains the
     * property to be sorted, and the value contains the sort order (ascending
     * when true, descending when false).
     * @return A list of found entities (if any).
     */
    List<T> findByPropertyValue(String propName, Object value,
            LinkedHashMap<String, Boolean> sortCriteria);

    /**
     * Find entities by one or more properties (in conjunction)
     *
     * @param filterCriteria Key-value map, where key contains the property
     * name, and value contains the property's desired value (a {@code List} of
     * values is acceptable)
     * @param sortCriteria An ordered hash map of entries where key contains the
     * property to be sorted, and the value contains the sort order (ascending
     * when true, descending when false).
     * @return A list of found entities (if any).
     */
    List<T> findByProperties(Map<String, Object> filterCriteria,
            LinkedHashMap<String, Boolean> sortCriteria);

    /**
     * Find entities by one or more properties (in conjunction)
     *
     * @param filterCriteria Key-value map, where key contains the property
     * name, and value contains the property's desired value (a {@code List} of
     * values is acceptable)
     * @param sortCriteria An ordered hash map of entries where key contains the
     * property to be sorted, and the value contains the sort order (ascending
     * when true, descending when false).
     * @param valuesExclusive If true, the values in {@code filterCriteria} will
     * be negated instead of included in the criteria (equivalent to SQL NOT
     * IN).
     * @return A list of found entities (if any).
     */
    List<T> findByProperties(Map<String, Object> filterCriteria,
            LinkedHashMap<String, Boolean> sortCriteria, boolean valuesExclusive);

    /**
     * Retrieve records that fall within a comparable range.
     *
     * @param propName The property to be compared.
     * @param min The minimum acceptable value of the property. If null, only
     * {@code max} will be used.
     * @param max The maximum acceptable value of the property. If null, only
     * {@code min} will be used.
     * @param inclusive specifies whether the range includes the boundaries (min
     * and max)
     * @param sortCriteria a ordered hash map of entries where key represent the
     * property to be stored, and the value represent sort order (ascending when
     * true, descending when false)
     * @return A list of found entities (if any).
     */
    List<T> findComparableRange(String propName, Comparable<?> min, Comparable<?> max, boolean inclusive,
            LinkedHashMap<String, Boolean> sortCriteria);

    /**
     * Retrieve records that fall within a comparable range.
     *
     * @param propName The property to be compared.
     * @param min The minimum acceptable value of the property. If null, only
     * {@code max} will be used.
     * @param max The maximum acceptable value of the property. If null, only
     * {@code min} will be used.
     * @param inclusive specifies whether the range includes the boundaries (min
     * and max)
     * @param sortCriteria an ordered hash map of entries whose {@code key}
     * contains the property to be sorted and {@code value} contains the desired
     * sort order (ascending when true, descending when false)
     * @param filterCriteria a hash map of entries whose {@code key} contains
     * the property to be filtered and {@code value} contains the desired filter
     * value.
     * @return A list of found entities (if any).
     */
    List<T> findComparableRange(String propName, Comparable<?> min, Comparable<?> max,
            boolean inclusive, LinkedHashMap<String, Boolean> sortCriteria, Map<String, Object> filterCriteria);

    /**
     * Retrieve records that fall within a specific date range.
     *
     * @param datePropName The property name containing the date
     * @param start The start date. If null, only the end date will be used.
     * @param end The end date. If null, only the start date will be used.
     * @param inclusive Specifies whether the range includes the boundaries
     * (start and end date).
     * @param applyDateTimeOffset If true, a small time offset (refer to
     * {@link #DATE_INCLUSIVE_OFFSET_MS}) will be applied for inclusive search.
     * This parameter has no effect if {@code inclusive} is set to false.
     * @return A list of found entities (if any).
     */
    List<T> findDateRange(String datePropName, Date start, Date end, boolean inclusive, boolean applyDateTimeOffset);

    /**
     * Retrieve records that fall within a specific date range.
     *
     * @param datePropName the property name representing the date
     * @param start The start date. If null, only the end date will be used.
     * @param end The end date. If null, only the start date will be used.
     * @param inclusive Specifies whether the range includes the boundaries
     * (start and end date).
     * @param applyDateTimeOffset If true, a small time offset (refer to
     * {@link #DATE_INCLUSIVE_OFFSET_MS}) will be applied for inclusive search.
     * This parameter has no effect if {@code inclusive} is set to false.
     * @param sortCriteria an ordered hash map of entries where key represent
     * the property to be stored, and the value represent sort order (ascending
     * when true, descending when false).
     * @return A list of found entities (if any).
     */
    List<T> findDateRange(String datePropName, Date start, Date end, boolean inclusive,
            boolean applyDateTimeOffset, LinkedHashMap<String, Boolean> sortCriteria);

    /**
     * Retrieve records that fall within a specific date range.
     *
     * @param datePropName the property name representing the date
     * @param start The start date. If null, only the end date will be used.
     * @param end The end date. If null, only the start date will be used.
     * @param inclusive Specifies whether the range includes the boundaries
     * (start and end date).
     * @param applyDateTimeOffset If true, a small time offset (refer to
     * {@link #DATE_INCLUSIVE_OFFSET_MS}) will be applied for inclusive search.
     * This parameter has no effect if {@code inclusive} is set to false.
     * @param sortCriteria an ordered hash map of entries whose {@code key}
     * contains the property to be sorted and {@code value} contains the desired
     * sort order (ascending when true, descending when false)
     * @param filterCriteria a hash map of entries whose {@code key} contains
     * the property to be filtered and {@code value} contains the desired filter
     * value.
     * @return A list of found entities (if any).
     */
    List<T> findDateRange(String datePropName, Date start, Date end, boolean inclusive,
            boolean applyDateTimeOffset, LinkedHashMap<String, Boolean> sortCriteria, Map<String, Object> filterCriteria);

    /**
     * Retrieve records that intersect a specific (comparable) range of values.
     *
     * @param propNameMin The 'min' property to be compared
     * @param propNameMax The 'max' property to be compared
     * @param min The minimum acceptable value of the property.
     * @param max The maximum acceptable value of the property.
     * @param inclusive Specifies whether the range includes the boundaries (min
     * and max).
     * @param applyDateTimeOffset If true, a small time offset (refer to
     * {@link #DATE_INCLUSIVE_OFFSET_MS}) will be applied for inclusive search.
     * This parameter has no effect if {@code inclusive} is set to false.
     * @param sortCriteria an ordered hash map of entries whose {@code key}
     * contains the property to be sorted and {@code value} contains the desired
     * sort order (ascending when true, descending when false)
     * @param filterCriteria a hash map of entries whose {@code key} contains
     * the property to be filtered and {@code value} contains the desired filter
     * value.
     * @return A list of found entities (if any).
     */
    public List<T> findIntersectingRange(String propNameMin, String propNameMax,
            Comparable min, Comparable max, boolean inclusive, boolean applyDateTimeOffset,
            LinkedHashMap<String, Boolean> sortCriteria, Map<String, Object> filterCriteria);
}
