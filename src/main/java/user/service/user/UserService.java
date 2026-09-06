package user.service.user;

import org.hibernate.Session;
import org.hibernate.Transaction;
import user.service.dao.UserDao;
import user.service.entity.User;
import user.service.logging.AppLogger;
import user.service.util.HibernateUtil;

import java.util.List;

public class UserService implements UserDao {
    @Override
    public void save(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
        }
        catch (Exception ex) {
            if ( transaction != null ) {
                transaction.rollback();
            }

            AppLogger.LOG.error("Ошибка сохранения пользователя: {}", user.toString(), ex);
            throw new RuntimeException("Ошибка сохранения пользователя", ex);
        }

        AppLogger.LOG.info("Сохранён: {}", user.toString());
    }

    @Override
    public User findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.find(User.class, id);
        }
        catch (Exception ex) {
            AppLogger.LOG.error("Не удалось найти пользователя с id: {}", id, ex);
            return null;
        }
    }

    @Override
    public void update(User updatedUser) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(updatedUser);
            transaction.commit();
        } catch (Exception ex) {
            if ( transaction != null ) {
                transaction.rollback();
            }

            AppLogger.LOG.error("Не удалось обновить пользователя: {}", updatedUser, ex);
            throw new RuntimeException("Ошибка обновления пользователя", ex);
        }

        AppLogger.LOG.info("Обновлён пользователь: {}", updatedUser.toString());
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = findById(id);

            if (user != null) {
                session.remove(user);
            }

            transaction.commit();
            AppLogger.LOG.info("Удалён пользователь с ID={}", id);
        } catch (Exception ex) {
            if ( transaction != null ) {
                transaction.rollback();
            }

            AppLogger.LOG.warn("Попытка удалить несуществующего пользователя с ID={}", id);
            throw new RuntimeException("Ошибка удаления пользователя.", ex);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from User", User.class).getResultList();
        } catch (Exception ex) {
            AppLogger.LOG.error("Ошибка получения всех пользователей", ex);
            return List.of();
        }
    }
}