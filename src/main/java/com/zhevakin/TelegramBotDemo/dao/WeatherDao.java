package com.zhevakin.TelegramBotDemo.dao;

import com.zhevakin.TelegramBotDemo.model.Users;
import com.zhevakin.TelegramBotDemo.model.WeatherModule;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public class WeatherDao {

    @PersistenceContext
    EntityManager entityManager;

    public WeatherModule getById(long id) { return entityManager.find(WeatherModule.class, id); }

    public WeatherModule getByIdUsers(Users users) {
        Query query = entityManager.createQuery("FROM WeatherModule WHERE user_id = :user", WeatherModule.class);
        query.setParameter("user",users.getId());
        return (WeatherModule) query.getSingleResult();
    }

    public WeatherModule create(WeatherModule weatherModule) {
        if (getByIdUsers(weatherModule.getUsers()) != null) return weatherModule;
        entityManager.persist(weatherModule);
        return weatherModule;
    }


    public void update(WeatherModule weatherModule, boolean b) {
    }

    public List<WeatherModule> getAll() {
        Query query = entityManager.createQuery( "FROM WeatherModule WHERE done = false AND time BEETWEN :begin AND :end",
                WeatherModule.class);
        query.setParameter("begin", "");
        query.setParameter("end","");
        return query.getResultList();
    }
}
