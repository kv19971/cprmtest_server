package com.example.cprmtest.demo.model.dao;


import com.example.cprmtest.demo.exceptions.user.EntityNotFoundException;
import com.example.cprmtest.demo.model.entities.BaseDBEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/* Base dao layer. Wrapper around existing JpaRepository functions for better error handling and ease of use
 */
public abstract class BaseDao<T extends BaseDBEntity> {

    protected abstract CrudRepository<T, Long> getRepository(); // to attach entity specific repository
    protected abstract String getEntityName(); // to attach entity name

    /* Data access exception is thrown whenever there is an error in db
     */
    public T genericSaveEntity(T entity)  {
        return getRepository().save((T) entity);
    }

    public T genericFindById(Long entityId) throws EntityNotFoundException {
        T entity = getRepository().findById(entityId).orElse(null);
        if (entity == null) {
            throw new EntityNotFoundException(getEntityName() + " not found");
        }
        return entity;
    }

    public List<T> genericFindAll() {
        return (List<T>) getRepository().findAll();
    }

}
