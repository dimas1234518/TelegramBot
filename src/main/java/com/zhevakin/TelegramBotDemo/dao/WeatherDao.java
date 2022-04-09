package com.zhevakin.TelegramBotDemo.dao;

import com.zhevakin.TelegramBotDemo.model.Users;
import com.zhevakin.TelegramBotDemo.model.WeatherModule;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Transactional
@Repository
public class WeatherDao {

    @PersistenceContext
    EntityManager entityManager;

    public WeatherModule getById(long id) { return entityManager.find(WeatherModule.class, id); }

    public WeatherModule getByIdUsers(long id) {
        Query query = entityManager.createQuery("FROM WeatherModule WHERE user_id = :user", WeatherModule.class);
        query.setParameter("user",id);
        return (WeatherModule) query.getSingleResult();
    }

    public WeatherModule create(WeatherModule weatherModule) {
        if (getByIdUsers(weatherModule.getUsers().getId()) != null) return weatherModule;
        entityManager.persist(weatherModule);
        return weatherModule;
    }


    public void update(WeatherModule weatherModule, boolean b) {
    }

    //TODO: подумать как лучше оптимизировать преобразование в начало и конец часа
    @SneakyThrows
    public List<WeatherModule> getAll() {
        Query query = entityManager.createQuery( "FROM WeatherModule WHERE done = false AND time >= :begin AND time <= :end",
                WeatherModule.class);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        format.format(date);
        String beginDate = getBeginHour(format.format(date));
        String endDate = getEndHour(format.format(date));
        query.setParameter("begin", format.parse(beginDate));
        query.setParameter("end",format.parse(endDate));
        return query.getResultList();
    }

    private String getEndHour(String date) {
        String[] inputDate = date.split(":");
        inputDate[1] = "59";
        inputDate[2] = "59";
        return inputDate[0] + ":" + inputDate[1] + ":" + inputDate[2];
    }

    private String getBeginHour(String date) {

        String[] inputDate = date.split(":");
        inputDate[1] = "00";
        inputDate[2] = "00";
        return inputDate[0] + ":" + inputDate[1] + ":" + inputDate[2];
    }
}
