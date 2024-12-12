package edu.augusta.sccs.trivia.mysql;

import edu.augusta.sccs.trivia.AnswerType;
import edu.augusta.sccs.trivia.Question;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;
import java.util.UUID;

public class QuestionDAO {
    private final SessionFactory sessionFactory;

    public QuestionDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<DbQuestion> getAllQuestions() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<DbQuestion> cq = builder.createQuery(DbQuestion.class);
        cq.select(cq.from(DbQuestion.class));
        List<DbQuestion> questions = session.createQuery(cq).getResultList();

        session.getTransaction().commit();
        session.close();
        return questions;
    }

    public List<DbQuestion> getQuestionsByDifficulty(int difficulty, int numQuestions) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<DbQuestion> cq = builder.createQuery(DbQuestion.class);
        Root<DbQuestion> root = cq.from(DbQuestion.class);
        cq.select(root);
        cq.where(builder.equal(root.get("difficulty"), difficulty));

        List<DbQuestion> questions = session.createQuery(cq).setMaxResults(numQuestions).getResultList();
        //List<DbQuestion> questions = session.createQuery(cq).getResultList();

        session.getTransaction().commit();
        session.close();
        return questions;
    }

    public void deleteQuestion(UUID uuid) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        DbQuestion question = session.get(DbQuestion.class, uuid.toString());
        session.remove(question);

        session.getTransaction().commit();
        session.close();
    }

    public DbQuestion getQuestionById(UUID uuid) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        DbQuestion question = session.get(DbQuestion.class, uuid.toString());

        session.getTransaction().commit();
        session.close();
        return question;
    }


    public static Question convertToQuestion(DbQuestion dbQuestion) {
        return Question.newBuilder()
                .setUuid(dbQuestion.getUuid().toString())
                .setDifficulty(dbQuestion.getDifficulty())
                .setQuestion(dbQuestion.getQuestion())
                .setAnswer(dbQuestion.getAnswer())
                .setAnswerType(dbQuestion.getAnswerType())
                .addChoices(dbQuestion.getChoices())
                .build();
    }
}
