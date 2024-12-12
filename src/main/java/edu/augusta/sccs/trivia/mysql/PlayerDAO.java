package edu.augusta.sccs.trivia.mysql;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.UUID;

public class PlayerDAO {

    private SessionFactory sessionFactory;

    public PlayerDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public DbPlayer getPlayerById(UUID uuid) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        DbPlayer player = session.get(DbPlayer.class, uuid.toString());

        session.getTransaction().commit();
        session.close();
        return player;
    }

    public void addPlayer(DbPlayer player) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.persist(player);

        session.getTransaction().commit();
        session.close();
    }

    public void deletePlayer(UUID uuid) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        DbPlayer player = session.get(DbPlayer.class, uuid.toString());
        session.remove(player);

        session.getTransaction().commit();
        session.close();
    }
}
