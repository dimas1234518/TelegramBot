package com.zhevakin.TelegramBotDemo.dao;

import com.zhevakin.TelegramBotDemo.model.Tasks;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class TasksDao {

    @PersistenceContext
    EntityManager entityManager;

    public List<Tasks> getAll() { return entityManager.createQuery("FROM tasks t",Tasks.class).getResultList(); }

    public Tasks create(Tasks task) {
        entityManager.persist(task);
        return task;
    }

}
