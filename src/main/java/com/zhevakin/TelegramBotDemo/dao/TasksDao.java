package com.zhevakin.TelegramBotDemo.dao;

import com.zhevakin.TelegramBotDemo.model.Tasks;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class TasksDao {

    @PersistenceContext
    EntityManager entityManager;

    public List<Tasks> getAll() {
        return entityManager.createQuery( "FROM Tasks WHERE done = false",Tasks.class).getResultList();
    }

    public Tasks create(Tasks task) {
        entityManager.persist(task);
        return task;
    }

    public Tasks update(Tasks tasks, boolean done) {
        Query query = entityManager.createQuery("update Tasks Set done = :done WHERE id = :id");
        query.setParameter("id", tasks.getId());
        query.setParameter("done", done);
        query.executeUpdate();
        return tasks;
    }

}
