package com.zhevakin.TelegramBotDemo.dao;

import com.zhevakin.TelegramBotDemo.model.Users;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public class UsersDao {

    @PersistenceContext
    EntityManager entityManager;

    public List<Users> getAll() {
        return entityManager.createQuery("from Users u", Users.class).getResultList();
    }

    public Users getById(long id) {
        return entityManager.find(Users.class, id);
    }

    public Users create(Users users) {
        Users currentUser = entityManager.find(Users.class,users.getId());
        if (currentUser != null) return null;
        entityManager.persist(users);
        return users;
    }

    public Users updateCity(Users users, String city) {
        Query query = entityManager.createQuery("update Users Set city = :city WHERE id = :id");
        query.setParameter("id", users.getId());
        query.setParameter("city", city);
        query.executeUpdate();
        return users;
    }


}
