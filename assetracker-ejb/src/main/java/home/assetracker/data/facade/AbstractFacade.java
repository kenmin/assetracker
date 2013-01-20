/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.facade;

import home.assetracker.data.entity.ModifiableEntity;
import home.assetracker.data.util.ReflectionUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.jboss.logging.Logger;

/**
 * Contains methods to access and modify entities in the persistence context.
 * Based on code by Adams Tan, extending functionalities to include: - Path
 * joining for filter criteria. - Eager loading for single entity find
 * (findEager). - Find by properties (findByPropertyValue, findByProperties).
 * Version: 05/2012
 *
 * @author Adams Tan
 * @author kenmin
 */
public abstract class AbstractFacade<T> implements FacadeLocal<T> {

    private static final Logger LOGGER = Logger.getLogger(AbstractFacade.class);
    private Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();
    @Resource
    private SessionContext sessionContext;

    /**
     * Get the authenticated user id. <b>Note: This method does not work during
     * postConstruct stage or earlier.</b>
     *
     * @return the authenticated user id, or "anonymous" otherwise, for example,
     * where authentication is not in-place.
     * @throws IllegalStateException When Session context is not ready, for
     * example, during postConstruct stage or earlier.
     */
    protected final String getAuthenticatedUserId() {
        if (sessionContext != null) {
            return sessionContext.getCallerPrincipal().getName();
        } else {
            throw new IllegalStateException("Session context is not ready.");
        }
    }

    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }

    protected void updateModifiableEntity(T entity) {
        if (entity instanceof ModifiableEntity) {
            ModifiableEntity modEntity = (ModifiableEntity) entity;
            modEntity.setLastModifiedUsername(getAuthenticatedUserId());
            modEntity.setLastModifiedDateTime(new Date());
        }
    }

    @Override
    public int count() {
        return count(null);
    }

    @Override
    public int count(Map<String, Object> filterCriteria) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<T> root = countQuery.from(entityClass);
        countQuery.select(builder.count(root)); //this is a projection, so no actual query

        //if filters are provided, need to build predicates before executing query
        if (filterCriteria != null && filterCriteria.size() > 0) {
            List<Predicate> p = new ArrayList<Predicate>();
            for (Map.Entry<String, Object> entry : filterCriteria.entrySet()) {
                p.add(buildPropertyValuePredicate(resolvePathString(root, entry.getKey()), entry, false, builder));
            }
            countQuery.where(p.toArray(new Predicate[p.size()]));
        }

        return getEntityManager().createQuery(countQuery).getSingleResult().intValue();
    }

    @Override
    public int count(String propName, Comparable<?> min, Comparable<?> max,
            boolean inclusive) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<T> root = countQuery.from(entityClass);
        countQuery.select(builder.count(root)); //this is a projection, so no actual query
        countQuery.where(buildComparableRangePredicate(root, propName, min, max, inclusive));
        return getEntityManager().createQuery(countQuery).getSingleResult().intValue();
    }

    @Override
    public void create(T entity) {
        updateModifiableEntity(entity);
        getEntityManager().persist(entity);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Persisted new entity: " + entity);
        }
    }

    @Override
    public void edit(T entity) {
        updateModifiableEntity(entity);
        getEntityManager().merge(entity);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Merged entity: " + entity);
        }
    }

    @Override
    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Removed entity: " + entity);
        }
    }
    
    @Override
    public void removeMultiple(List<T> entities) {
        if (entities != null) {
            for (T entity : entities) {
                remove(entity);
            }
        }
    }

    @Override
    public T find(long id) {
        return getEntityManager().find(entityClass, id);
    }

    @Override
    public T findEager(long id, List<String> eagerProps) {
        T t = find(id);
        if (t != null) {
            for (String propName : eagerProps) {
                Object propValue = ReflectionUtils.getPropertyValue(t, propName);
                if (propValue instanceof Collection) {
                    Collection c = (Collection) propValue;
                    c.size();
                }
            }
        }
        return t;
    }

    @Override
    public List<T> findAll() {
        CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq.select(root);
        List<T> result = getEntityManager().createQuery(cq).getResultList();
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Found (all) " + result.size() + " records of entity type " + entityClass.getSimpleName());
        }
        return result;
    }

    @Override
    public List<T> findByPropertyValue(String propName, Object value, LinkedHashMap<String, Boolean> sortCriteria) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(propName, value);
        return findByProperties(values, sortCriteria, false);
    }

    @Override
    public List<T> findByProperties(Map<String, Object> filterCriteria, LinkedHashMap<String, Boolean> sortCriteria) {
        return findByProperties(filterCriteria, sortCriteria, false);
    }

    @Override
    public List<T> findByProperties(Map<String, Object> filterCriteria, LinkedHashMap<String, Boolean> sortCriteria,
            boolean excludeValues) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        if (filterCriteria != null) {
            CriteriaQuery<T> cq = builder.createQuery(getEntityClass());
            Root<T> root = cq.from(getEntityClass());
            List<Predicate> p = new ArrayList<Predicate>();
            for (Map.Entry<String, Object> entry : filterCriteria.entrySet()) {
                p.add(buildPropertyValuePredicate(resolvePathString(root, entry.getKey()), entry, excludeValues, builder));
            }
            if (p != null) {
                cq.where((Predicate[]) p.toArray(new Predicate[p.size()]));
            }
            cq.orderBy(buildOrderList(root, sortCriteria));
            //obtain results
            TypedQuery<T> q = getEntityManager().createQuery(cq);
            List<T> result = q.getResultList();
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(String.format(
                        "Found (range) %d records [excludeValues=%b]",
                        result.size(), excludeValues));
            }
            return result;
        }
        return null;
    }

    @Override
    public List<T> findComparableRange(String propName, Comparable<?> min, Comparable<?> max,
            boolean inclusive, LinkedHashMap<String, Boolean> sortCriteria) {
        return findComparableRange(propName, min, max, inclusive, sortCriteria, null);
    }

    @Override
    public List<T> findComparableRange(String propName, Comparable<?> min, Comparable<?> max,
            boolean inclusive, LinkedHashMap<String, Boolean> sortCriteria, Map<String, Object> filterCriteria) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = builder.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        List<Predicate> p = new ArrayList<Predicate>();
        for (Map.Entry<String, Object> entry : filterCriteria.entrySet()) {
            p.add(buildPropertyValuePredicate(resolvePathString(root, entry.getKey()), entry, false, builder));
        }
        p.add(buildComparableRangePredicate(root, propName, min, max, inclusive));
        cq.where(p.toArray(new Predicate[p.size()]));
        cq.orderBy(buildOrderList(root, sortCriteria));

        TypedQuery<T> q = getEntityManager().createQuery(cq);
        List<T> result = q.getResultList();

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(String.format(
                    "Found (range) %d records [propName=%s; min=%s; max=%s; inclusive=%b]",
                    result.size(), propName, min, max, inclusive));
        }

        return result;
    }

    @Override
    public List<T> findDateRange(String datePropName, Date start, Date end, boolean inclusive, boolean applyDateTimeOffset) {
        return findDateRange(datePropName, start, end, inclusive, applyDateTimeOffset, null);
    }

    @Override
    public List<T> findDateRange(String datePropName, Date start, Date end, boolean inclusive,
            boolean applyDateTimeOffset, LinkedHashMap<String, Boolean> sortCriteria) {
        return findDateRange(datePropName, start, end, inclusive, applyDateTimeOffset, sortCriteria, null);
    }

    @Override
    public List<T> findDateRange(String datePropName, Date start, Date end, boolean inclusive,
            boolean applyDateTimeOffset, LinkedHashMap<String, Boolean> sortCriteria, Map<String, Object> filterCriteria) {
        //make sure if start is before end
        if (start != null && end != null && start.after(end)) {
            throw new IllegalArgumentException(
                    String.format("Start date [%s] cannot be later than end date [%s]", start, end));
        }

        if (start != null && inclusive && applyDateTimeOffset) {
            start = new Date(start.getTime() - DATE_INCLUSIVE_OFFSET_MS);
        }

        if (end != null && inclusive && applyDateTimeOffset) {
            end = new Date(end.getTime() + DATE_INCLUSIVE_OFFSET_MS);
        }

        return findComparableRange(datePropName, start, end, inclusive, sortCriteria, filterCriteria);
    }

    public List<T> findIntersectingRange(String propNameMin, String propNameMax,
            Comparable min, Comparable max, boolean inclusive, boolean applyDateTimeOffset) {
        return findIntersectingRange(propNameMin, propNameMax, min, max, inclusive, applyDateTimeOffset, null, null);
    }

    @Override
    public List<T> findIntersectingRange(String propNameMin, String propNameMax,
            Comparable min, Comparable max, boolean inclusive, boolean applyDateTimeOffset,
            LinkedHashMap<String, Boolean> sortCriteria, Map<String, Object> filterCriteria) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = (CriteriaQuery<T>) builder.createQuery(getEntityClass());
        Root<T> root = (Root<T>) cq.from(getEntityClass());
        if (min instanceof Date) {
            if (min != null && inclusive && applyDateTimeOffset) {
                min = new java.util.Date(((Date) min).getTime() - DATE_INCLUSIVE_OFFSET_MS);
            }
        }
        if (max instanceof Date) {
            if (max != null && inclusive && applyDateTimeOffset) {
                max = new java.util.Date(((Date) max).getTime() + DATE_INCLUSIVE_OFFSET_MS);
            }
        }
        cq.where(buildIntersectRangePredicate(root, propNameMin, propNameMax, min, max, inclusive),
                builder.and((Predicate[]) buildPropertyValuePredicates(root, filterCriteria, inclusive, builder).toArray()));
        TypedQuery<T> q = getEntityManager().createQuery(cq);
        List<T> result = q.getResultList();

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(String.format(
                    "Found (intersecting range) %d records [propNameMin=%s; propNameMax=%s; min=%s; max=%s; inclusive=%b; applyDateTimeOffset=%b]",
                    result.size(), propNameMin, propNameMax, min, max, inclusive, applyDateTimeOffset));
        }

        return result;
    }

    /**
     * IndexRangeRetrievable Impl
     */
    @Override
    public List<T> findIndexRange(int start, int max) {
        return findIndexRange(start, max, null);
    }

    @Override
    public List<T> findIndexRange(int start, int max, LinkedHashMap<String, Boolean> orderEntries) {
        return findIndexRange(start, max, orderEntries, null, null);
    }

    @Override
    public List<T> findIndexRange(int start, int max, LinkedHashMap<String, Boolean> sortCriteria,
            Map<String, Object> filterCriteria, AtomicInteger matchingCount) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = builder.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq.select(root);
        cq.orderBy(buildOrderList(root, sortCriteria));
        List<Predicate> p = new ArrayList<Predicate>();
        for (Map.Entry<String, Object> entry : filterCriteria.entrySet()) {
            p.add(buildPropertyValuePredicate(resolvePathString(root, entry.getKey()), entry, false, builder));
        }
        cq.where(p.toArray(new Predicate[p.size()]));

        TypedQuery<T> q = getEntityManager().createQuery(cq);
        q.setMaxResults(max);
        q.setFirstResult(start);
        List<T> result = q.getResultList();

        if (matchingCount != null) {
            CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
            root = countQuery.from(entityClass); //this is a projection, so no actual query
            countQuery.select(builder.countDistinct(root)); //this is a projection, so no actual query
            p.clear();
            for (Map.Entry<String, Object> entry : filterCriteria.entrySet()) {
                p.add(buildPropertyValuePredicate(resolvePathString(root, entry.getKey()), entry, false, builder));
            }
            countQuery.where(p.toArray(new Predicate[p.size()]));
            matchingCount.set(getEntityManager().createQuery(countQuery).getSingleResult().intValue());
        }

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(String.format(
                    "Found (range) %d records [type=%s, start=%d, max=%d]",
                    result.size(), entityClass.getSimpleName(), start, max));
        }

        return result;
    }

    public List<T> findByPropertyComparators(List<Object> operands, LinkedHashMap<String, Boolean> sortCriteria) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = builder.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);

        cq.where(buildPropertyComparatorPredicate(root, operands));
        cq.orderBy(buildOrderList(root, sortCriteria));
        //obtain results
        TypedQuery<T> q = getEntityManager().createQuery(cq);

        List<T> result = q.getResultList();

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(String.format(
                    "Found (range) %d records", result.size()));
        }

        return result;
    }

    /**
     * Builds a list of predicates that is used to filter records by a given
     * property. Version for internal usage
     *
     * @param root The root object to the entity.
     * @param filterCriteria a hash map of entries whose {@code key} contains
     * the property to be filtered and {@code value} contains the desired
     * filter.
     * @param excludeValues If true, the values in {@code filterCriteria} will
     * be negated instead of included in the criteria (equivalent to SQL NOT
     * IN).
     * @param builder The CriteriaBuilder object.
     * @return A list of predicates.
     */
    protected List<Predicate> buildPropertyValuePredicates(Root root, Map<String, Object> filterCriteria, boolean excludeValues, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        for (Map.Entry<String, Object> entry : filterCriteria.entrySet()) {
            predicates.add(buildPropertyValuePredicate(root, entry.getKey(), entry.getValue(), excludeValues, builder));
        }
        return predicates;
    }

    /**
     * Version for internal usage
     *
     * @param root The root object to the entity.
     * @param propName The absolute path to the property name to be filtered
     * (supports joining).
     * @param value The filter criteria value.
     * @param excludeValues If true, {@code value} will be negated instead of
     * included in the criteria (equivalent to SQL NOT IN).
     * @param builder The CriteriaBuilder object.
     * @return A filter predicate.
     */
    protected Predicate buildPropertyValuePredicate(Root root, String propName, Object value, boolean excludeValues, CriteriaBuilder builder) {
        Path path = resolvePathString(root, propName);
        HashMap<String, Object> filterCriteria = new HashMap<String, Object>();
        filterCriteria.put(propName, value);
        Iterator<Map.Entry<String, Object>> iterator = filterCriteria.entrySet().iterator();
        if (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            return buildPropertyValuePredicate(path, entry, excludeValues, builder);
        }
        return null;
    }

    /**
     * Builds a predicate that is used to filter records by a given property.
     *
     * @param path The {@code path} to the property.
     * @param entry The {@code filterCriteria} entry.
     * @param excludeValues True if not-in (exclude values), False if in
     * (include values).
     * @param builder The {@code CriteriaBuilder} object.
     * @return a predicate object that can be used with
     * {@link CriteriaQuery#where(javax.persistence.criteria.Predicate[])}
     */
    protected Predicate buildPropertyValuePredicate(Path<Object> path, Map.Entry<String, Object> entry,
            boolean excludeValues, CriteriaBuilder builder) {
        if (excludeValues) {
            if (entry.getValue() instanceof Collection) {
                if (((Collection<T>) entry.getValue()).size() > 0) {
                    return path.in((Collection<T>) entry.getValue()).not();
                }
            } else if (entry.getValue() instanceof CustomPredicateBuilder) {
                return ((CustomPredicateBuilder) entry.getValue()).buildMatcher(builder, path).not();
            } else {
                return builder.equal(path, entry.getValue()).not();
            }
        } else {
            if (entry.getValue() instanceof Collection) {
                if (((Collection<T>) entry.getValue()).size() > 0) {
                    return path.in((Collection<T>) entry.getValue());
                }
            } else if (entry.getValue() instanceof CustomPredicateBuilder) {
                return ((CustomPredicateBuilder) entry.getValue()).buildMatcher(builder, path);
            } else {
                return builder.equal(path, entry.getValue());
            }
        }
        return null;
    }

    /**
     * Builds a predicate that is used to filter records by comparing a given
     * property.
     *
     * @param root the query root typed to the entity
     * @param propName name of the given property to compare
     * @param min the minimum value that the property must match, if null, there
     * will be no minimum value
     * @param max the maximum value that the property must match, if null, there
     * will be no maximum value
     * @param inclusive if true, the min/max value will be included in the
     * filter range.
     * @return a predicate object that can be used with
     * {@link CriteriaQuery#where(javax.persistence.criteria.Predicate[])}
     */
    @SuppressWarnings("unchecked")
    protected Predicate buildComparableRangePredicate(Root<T> root, String propName, Comparable min, Comparable max, boolean inclusive) { // parasoft-suppress GLOBAL.DPPM "Meant to be used/modified by child classes"
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        Path<Comparable> path = resolvePathString(root, propName); //root.get(propName);

        List<Predicate> restrictions = new ArrayList<Predicate>();
        if (min != null) {
            //define min condition
            if (inclusive) {
                restrictions.add(builder.greaterThanOrEqualTo(path, min));
            } else {
                restrictions.add(builder.greaterThan(path, min));
            }
        }

        if (max != null) {
            //define max condition
            if (inclusive) {
                restrictions.add(builder.lessThanOrEqualTo(path, max));
            } else {
                restrictions.add(builder.lessThan(path, max));
            }
        }
        return builder.and(restrictions.toArray(new Predicate[0]));
    }

    /**
     * Builds a predicate that filters records by 2 given properties and a
     * (comparable) range that both properties must intersect.
     *
     * @param root The query root typed to the entity.
     * @param propNameMin The 'start' property.
     * @param propNameMax The 'end' property.
     * @param min The minimum value both properties must match.
     * @param max The maximum value both properties must match.
     * @param inclusive If true, the min/max value will be included within the
     * filter range.
     * @return a predicate object that can be used with
     * {@link CriteriaQuery#where(javax.persistence.criteria.Predicate[])}
     */
    protected Predicate buildIntersectRangePredicate(Root<T> root, String propNameMin,
            String propNameMax, Comparable min, Comparable max, boolean inclusive) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        Path<Comparable> pathMin = resolvePathString(root, propNameMin);
        Path<Comparable> pathMax = resolvePathString(root, propNameMax);

        Predicate result = null;
        // CASE 1: PropMin <(=) min and PropMax >(=) max
        List<Predicate> case1 = new ArrayList<Predicate>();
        if (min != null) {
            //define min condition
            Predicate p = (inclusive) ? builder.lessThanOrEqualTo(pathMin, min) : builder.lessThan(pathMin, min);
            case1.add(p);
        }
        if (max != null) {
            //define max condition
            Predicate p = (inclusive) ? builder.greaterThanOrEqualTo(pathMax, max) : builder.greaterThan(pathMax, max);
            case1.add(p);
        }
        Predicate predicate1 = builder.and(case1.toArray(new Predicate[0]));

        // CASE 2: PropMin <(=) min and PropMax >(=) min and PropMax <(=) max
        List<Predicate> case2 = new ArrayList<Predicate>();
        if (min != null) {
            //define min condition
            Predicate p = (inclusive) ? builder.lessThanOrEqualTo(pathMin, min) : builder.lessThan(pathMin, min);
            case2.add(p);
        }
        if (max != null) {
            //define max condition
            Predicate p1 = (inclusive) ? builder.greaterThanOrEqualTo(pathMax, min) : builder.greaterThan(pathMax, min);
            case2.add(p1);
            Predicate p2 = (inclusive) ? builder.lessThanOrEqualTo(pathMax, max) : builder.lessThan(pathMax, max);
            case2.add(p2);
        }
        Predicate predicate2 = builder.and(case2.toArray(new Predicate[0]));

        // CASE 3: PropMin >(=) min and PropMax <(=) max
        List<Predicate> case3 = new ArrayList<Predicate>();
        if (min != null) {
            //define min condition
            Predicate p = (inclusive) ? builder.greaterThanOrEqualTo(pathMin, min) : builder.greaterThan(pathMin, min);
            case3.add(p);
        }
        if (max != null) {
            //define max condition
            Predicate p = (inclusive) ? builder.lessThanOrEqualTo(pathMax, max) : builder.lessThan(pathMax, max);
            case3.add(p);
        }
        Predicate predicate3 = builder.and(case3.toArray(new Predicate[0]));

        // CASE 4: PropMin >(=) min and PropMin <(=) max and PropMax >(=) max
        List<Predicate> case4 = new ArrayList<Predicate>();
        if (min != null) {
            //define min condition
            Predicate p1 = (inclusive) ? builder.greaterThanOrEqualTo(pathMin, min) : builder.greaterThan(pathMin, min);
            case4.add(p1);
            Predicate p2 = (inclusive) ? builder.lessThanOrEqualTo(pathMin, max) : builder.lessThan(pathMin, max);
            case4.add(p2);
        }
        if (max != null) {
            //define max condition
            Predicate p = (inclusive) ? builder.greaterThanOrEqualTo(pathMax, max) : builder.greaterThan(pathMax, max);
            case4.add(p);
        }
        Predicate predicate4 = builder.and(case4.toArray(new Predicate[0]));

        result = builder.or(predicate1, predicate2, predicate3, predicate4);

        return result;
    }

    protected Predicate buildPropertyComparatorPredicate(Root root, List<Object> operands) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        Predicate result = null;
        for (Object operand : operands) {
            if (operand instanceof PredicateGroup) {
                PredicateGroup predicateGroup = (PredicateGroup) operand;
                if (predicateGroup.isConjunction()) {
                    result = (result != null) ? builder.and(result, predicateGroup.resolveToPredicate(this, root, builder)) : predicateGroup.resolveToPredicate(this, root, builder);
                } else {
                    result = (result != null) ? builder.or(result, predicateGroup.resolveToPredicate(this, root, builder)) : predicateGroup.resolveToPredicate(this, root, builder);
                }
            } else if (operand instanceof PropertyComparator) {
                PropertyComparator propertyComparator = (PropertyComparator) operand;
                if (propertyComparator.isConjunction()) {
                    result = (result != null) ? builder.and(result, propertyComparator.resolveToPredicate(this, root, builder)) : propertyComparator.resolveToPredicate(this, root, builder);
                } else {
                    result = (result != null) ? builder.or(result, propertyComparator.resolveToPredicate(this, root, builder)) : propertyComparator.resolveToPredicate(this, root, builder);
                }
            }
        }
        return result;
    }

    /**
     * Resolves a path string to a (joined) path.
     *
     * @param root The root of this criteria query
     * @param fqPath The fully-qualified path string, period-separated.
     * @return The (joined) path.
     */
    protected Path resolvePathString(Root root, String fqPath) {
        Path path = null;
        String[] fieldNames = fqPath.split("\\.");
        if (fieldNames.length <= 1) {
            path = root.get(fqPath);
        } else if (fieldNames.length > 1) {
            Join join = root.join(fieldNames[0]);
            for (int i = 1; i < fieldNames.length - 1; i++) {
                path = join.join(fieldNames[i]);
            }
            path = join.get(fieldNames[fieldNames.length - 1]);
        }
        return path;
    }

    /**
     *
     * @param root the query root typed to the entity
     * @param orderCriteria an ordered hash map of entries where key represent
     * the property to be stored, and the value represent sort order (ascending
     * when true, descending when false)
     * @return a list of order objects that could be used with
     * {@link CriteriaQuery#orderBy(java.util.List)}
     */
    protected List<Order> buildOrderList(Root<T> root, LinkedHashMap<String, Boolean> orderCriteria) {
        List<Order> result = new ArrayList<Order>();

        if (orderCriteria != null) {
            CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Building order list...");
            }
            for (Map.Entry<String, Boolean> entry : orderCriteria.entrySet()) {
                try {
                    Path<Object> propExp = resolvePathString(root, entry.getKey());
                    result.add(entry.getValue() ? builder.asc(propExp) : builder.desc(propExp));
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("\torder [prop=" + entry.getKey() + "; asc=" + entry.getValue() + "]");
                    }
                } catch (IllegalArgumentException ex) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.warn(String.format(
                                "Unable to find prop <%s> in entity <%s> to build order list.",
                                entry.getKey(), entityClass.getName()));
                    }
                }
            }
        }
        return result;
    }
}
