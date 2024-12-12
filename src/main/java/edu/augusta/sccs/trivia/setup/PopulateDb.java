package edu.augusta.sccs.trivia.setup;


import com.opencsv.bean.CsvToBeanBuilder;
import edu.augusta.sccs.trivia.csv.CSVQuestion;
import edu.augusta.sccs.trivia.mysql.DbConfig1;
import edu.augusta.sccs.trivia.mysql.DbQuestion;
import edu.augusta.sccs.trivia.mysql.QuestionDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class PopulateDb {

    static Logger log = LogManager.getLogger(PopulateDb.class.getName());

    private SessionFactory sessionFactory;

    public PopulateDb(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void populateDb(String csvFile) {
        List<CSVQuestion> questions = null;
        try {
            FileReader fileReader = new FileReader(csvFile);
            questions = new CsvToBeanBuilder<CSVQuestion>(fileReader).withType(CSVQuestion.class).build().parse();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        List<DbQuestion> dbQuestions = new ArrayList<>();

        for (CSVQuestion q : questions) {
            dbQuestions.add(DbQuestion.createDBQuestion(q));
        }

        try {

            // Initialize Session Object
            Session session = sessionFactory.openSession();

            session.beginTransaction();

            // persist() method of JPA
            for (DbQuestion q : dbQuestions) {
                session.persist(q);
            }

            session.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void deleteAllQuestions() {
        try {
            QuestionDAO dao = new QuestionDAO(sessionFactory);
            List<DbQuestion> questions = dao.getAllQuestions();
            for (DbQuestion q : questions) {
                dao.deleteQuestion(q.getUuid());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

        PopulateDb dbPopulator = new PopulateDb(DbConfig1.getSessionFactory());
        dbPopulator.populateDb("C:/Users/tyler/Downloads/MySQLTest3/TriviaQsRound3.csv");

    //    EntityManagerFactory emf = DbConfig.getConfig().buildSessionFactory();
     //   EntityManager em = emf.createEntityManager();
     //   em.getTransaction().begin();


        List<DbQuestion> questions = null;
        try {
            QuestionDAO dao = new QuestionDAO(DbConfig1.getSessionFactory());
            questions = dao.getAllQuestions();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("Questions in DB: " + questions.size());

      //  em.getTransaction().commit();
      //  em.close();
    }

}