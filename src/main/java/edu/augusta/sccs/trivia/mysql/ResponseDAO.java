package edu.augusta.sccs.trivia.mysql;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.UUID;

public class ResponseDAO {
    private SessionFactory sessionFactory;

    public ResponseDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    public DbQuestionResponse reportQuestionResponse(DbPlayer player, DbQuestion question, boolean correct) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        DbQuestionResponse response = DbQuestionResponse.createResponse(player, question, correct);
        session.persist(response);

        session.getTransaction().commit();
        session.close();
        return response;
    }

    public void deleteResponse(UUID uuid) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        DbQuestionResponse response = session.get(DbQuestionResponse.class, uuid.toString());
        session.remove(response);

        session.getTransaction().commit();
        session.close();
    }

    public DbQuestionResponse getResponseById(UUID uuid) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        DbQuestionResponse response = session.get(DbQuestionResponse.class, uuid.toString());

        session.getTransaction().commit();
        session.close();
        return response;
    }

}
