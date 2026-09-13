package user.service.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import user.service.entity.User;
import user.service.logging.AppLogger;
import user.service.util.HibernateUtil;

import java.util.List;

public class UserDaoImpl implements UserDao {

    @Override
    public void save(User user) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            session.persist(user);

            transaction.commit();

            AppLogger.LOG.info("Сохранён: {}", user);

        } catch (Exception ex) {

            if (transaction != null &&
                    transaction.getStatus().canRollback()) {
                transaction.rollback();
            }

            AppLogger.LOG.error(
                    "Ошибка сохранения пользователя: {}",
                    user,
                    ex
            );

            throw new RuntimeException(
                    "Ошибка сохранения пользователя",
                    ex
            );
        }
    }

    @Override
    public User findById(Long id) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            User user = session.find(User.class, id);

            transaction.commit();

            return user;

        } catch (Exception ex) {

            if (transaction != null &&
                    transaction.getStatus().canRollback()) {
                transaction.rollback();
            }

            AppLogger.LOG.error(
                    "Не удалось найти пользователя с id: {}",
                    id,
                    ex
            );

            throw new RuntimeException(
                    "Ошибка поиска пользователя",
                    ex
            );
        }
    }

    @Override
    public List<User> findAll() {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            List<User> users = session
                    .createQuery("from User", User.class)
                    .getResultList();

            transaction.commit();

            return users;

        } catch (Exception ex) {

            if (transaction != null &&
                    transaction.getStatus().canRollback()) {
                transaction.rollback();
            }

            AppLogger.LOG.error(
                    "Ошибка получения всех пользователей",
                    ex
            );

            throw new RuntimeException(
                    "Ошибка получения пользователей",
                    ex
            );
        }
    }

    @Override
    public void update(User user) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            session.merge(user);

            transaction.commit();

            AppLogger.LOG.info("Обновлён пользователь: {}", user);

        } catch (Exception ex) {

            if (transaction != null &&
                    transaction.getStatus().canRollback()) {
                transaction.rollback();
            }

            AppLogger.LOG.error(
                    "Не удалось обновить пользователя: {}",
                    user,
                    ex
            );

            throw new RuntimeException(
                    "Ошибка обновления пользователя",
                    ex
            );
        }
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            User user = session.find(User.class, id);

            if (user != null) {
                session.remove(user);
                AppLogger.LOG.info(
                        "Удалён пользователь с ID={}",
                        id
                );
            } else {
                AppLogger.LOG.warn(
                        "Пользователь с ID={} не найден",
                        id
                );
            }

            transaction.commit();

        } catch (Exception ex) {

            if (transaction != null &&
                    transaction.getStatus().canRollback()) {
                transaction.rollback();
            }

            AppLogger.LOG.error(
                    "Ошибка удаления пользователя с ID={}",
                    id,
                    ex
            );

            throw new RuntimeException(
                    "Ошибка удаления пользователя",
                    ex
            );
        }
    }
}