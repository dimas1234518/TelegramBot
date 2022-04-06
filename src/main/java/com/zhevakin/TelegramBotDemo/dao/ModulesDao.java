package com.zhevakin.TelegramBotDemo.dao;

import com.zhevakin.TelegramBotDemo.model.Modules;
import com.zhevakin.TelegramBotDemo.model.Users;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public class ModulesDao {

    @PersistenceContext
    EntityManager entityManager;

    public List<Modules> getAll() {
        return entityManager.createQuery("from Modules m", Modules.class).getResultList();
    }

    public Modules getById(long id) {
        return entityManager.find(Modules.class, id);
    }

    public Modules create(Modules modules) {
        entityManager.persist(modules);
        return modules;
    }

    public Modules update(Modules modules, Users users, boolean active) {
        return null;
    }

    //TODO: переделать под HQL
    public List getUsersModules(Users users) {
        Query query = entityManager.createNativeQuery("SELECT m.name, m.id " +
                                     "FROM Users_Modules as u_m " +
                                     "INNER JOIN modules as m " +
                                     "ON u_m.modules_id = m.id " +
                                     "WHERE u_m.users_id=?", Modules.class);
        query.setParameter(1, users.getId());
        return query.getResultList();

    }

}
