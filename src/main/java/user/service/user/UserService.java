package user.service.user;

import org.hibernate.Session;
import org.hibernate.Transaction;
import user.service.entity.User;
import user.service.logging.AppLogger;
import user.service.util.HibernateUtil;

import java.util.List;

public class UserService {

    public void save(User user) {

        Transaction transaction = null;

        try (Session session = HibernateUtil
                .getSessionFactory()
                .openSession()) {

            transaction = session.beginTransaction();

            session.persist(user);

            transaction.commit();
        } catch (Exception e) {
            if ( transaction != null ) {
                transaction.rollback();
            }
            throw e;
        }

        AppLogger.LOG.info("Сохранён: {}", user.toString());
    }

    public User findById(Long id) {

        try (Session session = HibernateUtil
                .getSessionFactory()
                .openSession()) {

            return session.find(User.class, id);

        }

    }

    public void update(User updatedUser) {

        Transaction transaction = null;

        try (Session session = HibernateUtil
                .getSessionFactory()
                .openSession()) {

            transaction = session.beginTransaction();

            session.merge(updatedUser);

            transaction.commit();

        } catch (Exception e) {
            if ( transaction != null ) {
                transaction.rollback();
            }

            throw e;
        }

        AppLogger.LOG.info("Обновлён: {}", updatedUser.toString());
    }

    public void delete(Long id) {

        Transaction transaction = null;

        try (Session session = HibernateUtil
                .getSessionFactory()
                .openSession()) {

            transaction = session.beginTransaction();

            User user = findById(id);

            if (user != null) {
                session.remove(user);
                AppLogger.LOG.info("Удалён пользователь с ID={}", id);
            }

            transaction.commit();

        } catch (Exception e) {

            AppLogger.LOG.warn("Попытка удалить несуществующего пользователя с ID={}", id);

            if ( transaction != null ) {
                transaction.rollback();
            }

            throw e;
        }
    }

    public List<User> findAll() {
        try (Session session = HibernateUtil
                .getSessionFactory()
                .openSession()) {

            return session.createQuery("from User", User.class)
                    .getResultList();
        }
    }
}