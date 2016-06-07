package net.bunselmeyer.middleware.pipes.persistence;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.cache.internal.StandardQueryCache;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;

import java.util.List;
import java.util.function.Supplier;

public class SimpleRepository<T> implements Repository<T> {
    private final Supplier<Session> sessionSupplier;
    private final Class<T> persistentClass;

    private String queryCacheRegion;

    public SimpleRepository(Supplier<Session> sessionSupplier, Class<T> persistentClass) {
        this.sessionSupplier = sessionSupplier;
        this.persistentClass = persistentClass;
        queryCacheRegion = StandardQueryCache.class.getName();
    }

    protected Session getCurrentSession() {
        return sessionSupplier.get();
    }

    protected Class<T> getPersistentClass() {
        return persistentClass;
    }

    protected Example createExample(T example) {
        return Example.create(example).excludeZeroes();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T read(Integer id) {
        return (T) getCurrentSession().get(getPersistentClass(), id);
    }

    @Override
    public RepositoryResult<T> find() {
        Criteria criteria = createCriteria(true);
        return find(criteria);
    }

    @Override
    public RepositoryResult<T> find(T example) {
        Criteria criteria = createCriteria(true, createExample(example));
        return find(criteria);
    }

    @Override
    public RepositoryResult<T> find(Criteria criteria) {
        return createRepositoryResult(criteria);
    }

    @Override
    public T create(T entity) {
        getCurrentSession().saveOrUpdate(entity);
        return entity;
    }

    @Override
    public void delete(T entity) {
        getCurrentSession().delete(entity);
    }

    @Override
    public void update(T entity) {
        getCurrentSession().merge(entity);
        getCurrentSession().flush();
    }

    @SuppressWarnings("unchecked")
    protected List<T> findByCriteria(Criterion... criterion) {
        return createCriteria(criterion).list();
    }

    protected Criteria createCriteria(Criterion... criterion) {
        return createCriteria(true, criterion);
    }

    protected Criteria createCriteria(boolean cacheable, Criterion... criterion) {
        Criteria criteria = getCurrentSession().createCriteria(getPersistentClass());
        for (Criterion crit : criterion) {
            criteria.add(crit);
        }
        criteria.setCacheable(cacheable);
        criteria.setCacheRegion(queryCacheRegion);
        criteria.setReadOnly(getCurrentSession().isDefaultReadOnly());
        return criteria;
    }

    protected RepositoryResult<T> createRepositoryResult(Criteria criteria) {
        return new RepositoryResultImpl<>(criteria);
    }

    private static class RepositoryResultImpl<M> implements RepositoryResult<M> {

        private final Criteria criteria;

        RepositoryResultImpl(Criteria criteria) {
            this.criteria = criteria;
        }

        @Override
        public RepositoryResult<M> limit(int page, int perPage) {
            int start = (page - 1) * perPage;
            criteria.setFirstResult(start).setMaxResults(perPage);
            return this;
        }

        @Override
        public RepositoryResult<M> orderBy(Order... orders) {
            for (Order order : orders) {
                criteria.addOrder(order);
            }
            return this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<M> list() {
            return criteria.list();
        }

    }
}
