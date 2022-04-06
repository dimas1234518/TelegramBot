package com.zhevakin.TelegramBotDemo.dao;

import com.zhevakin.TelegramBotDemo.model.Logs;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class LogsDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Logs> getAll() {
        return entityManager.createQuery("from Logs l order by l.id desc", Logs.class).getResultList();
    }

    public Logs getById(long id) {
        return entityManager.find(Logs.class, id);
    }

    public Logs create(Logs logs) {
        entityManager.persist(logs);
        return logs;
    }

}
