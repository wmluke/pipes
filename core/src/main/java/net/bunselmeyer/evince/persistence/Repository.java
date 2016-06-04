package net.bunselmeyer.evince.persistence;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import java.util.List;

public interface Repository<T> {

    T read(Integer id);

    RepositoryResult<T> find();

    RepositoryResult<T> find(T example);

    RepositoryResult<T> find(Criteria criteria);

    T create(T entity);

    void delete(T entity);

    void update(T entity);


    public static interface RepositoryResult<M> {

        RepositoryResult<M> limit(int page, int perPage);

        RepositoryResult<M> orderBy(Order... orders);

        List<M> list();

    }
}
