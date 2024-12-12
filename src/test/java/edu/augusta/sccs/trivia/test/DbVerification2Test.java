package edu.augusta.sccs.trivia.test;

import edu.augusta.sccs.trivia.Question;
import edu.augusta.sccs.trivia.mysql.DbConfig2;
import edu.augusta.sccs.trivia.mysql.DbQuestion;
import edu.augusta.sccs.trivia.mysql.QuestionDAO;
import edu.augusta.sccs.trivia.server.Processor;
import edu.augusta.sccs.trivia.setup.PopulateDb;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DbVerification2Test {

    public static final String QUESTION_CSV = "C:/miniprojectdm/MySQLTest3/TriviaQsRound3.csv";
    public static final int NUM_QUESTIONS = 100;
    public static final int QUESTION_SUBSET = 5;

    @BeforeAll
    public static void setup() {
        PopulateDb dbPopulator = new PopulateDb(DbConfig2.getSessionFactory());
        dbPopulator.populateDb(QUESTION_CSV);

        QuestionDAO questionDAO = new QuestionDAO(DbConfig2.getSessionFactory());
        List<DbQuestion> dbQuestions = questionDAO.getAllQuestions();
        assertTrue(dbQuestions.size() > 0);
    }

    @Test
    public void testDbSize() {
        QuestionDAO questionDAO = new QuestionDAO(DbConfig2.getSessionFactory());
        List<DbQuestion> dbQuestions = questionDAO.getAllQuestions();
        assertTrue(dbQuestions.size() == NUM_QUESTIONS);
    }

    @Test
    public void testDbProcessorAll() {
        QuestionDAO questionDAO = new QuestionDAO(DbConfig2.getSessionFactory());
        Processor processor= new Processor(questionDAO, null, null);
        List<Question> questions = processor.getAllQuestions();
        assertTrue(questions.size() == NUM_QUESTIONS);
    }

    @Test
    public void testQueryEasy() {
        QuestionDAO questionDAO = new QuestionDAO(DbConfig2.getSessionFactory());
        Processor processor= new Processor(questionDAO, null, null);
        List<Question> questions = processor.getQuestionsByDifficulty(1, QUESTION_SUBSET);
        assertTrue(questions.size() == QUESTION_SUBSET);
        for(Question q: questions) {
            assertTrue(q.getDifficulty() == 1);
        }
    }

    @Test
    public void testQueryTwo() {
        QuestionDAO questionDAO = new QuestionDAO(DbConfig2.getSessionFactory());
        Processor processor= new Processor(questionDAO, null, null);
        List<Question> questions = processor.getQuestionsByDifficulty(2, QUESTION_SUBSET);
        assertTrue(questions.size() == QUESTION_SUBSET);
        for(Question q: questions) {
            assertTrue(q.getDifficulty() == 2);
        }
    }

    @Test
    public void testQueryThree() {
        QuestionDAO questionDAO = new QuestionDAO(DbConfig2.getSessionFactory());
        Processor processor= new Processor(questionDAO, null, null);
        List<Question> questions = processor.getQuestionsByDifficulty(3, QUESTION_SUBSET);
        assertTrue(questions.size() == QUESTION_SUBSET);
        for(Question q: questions) {
            assertTrue(q.getDifficulty() == 3);
        }
    }

    @Test
    public void testQueryFour() {
        QuestionDAO questionDAO = new QuestionDAO(DbConfig2.getSessionFactory());
        Processor processor= new Processor(questionDAO, null, null);
        List<Question> questions = processor.getQuestionsByDifficulty(4, QUESTION_SUBSET);
        assertTrue(questions.size() == QUESTION_SUBSET);
        for(Question q: questions) {
            assertTrue(q.getDifficulty() == 4);
        }
    }

    @Test
    public void testQueryFive() {
        QuestionDAO questionDAO = new QuestionDAO(DbConfig2.getSessionFactory());
        Processor processor= new Processor(questionDAO, null, null);
        List<Question> questions = processor.getQuestionsByDifficulty(5, QUESTION_SUBSET);
        assertTrue(questions.size() == QUESTION_SUBSET);
        for(Question q: questions) {
            assertTrue(q.getDifficulty() == 5);
        }
    }

    @AfterAll
    public static void teardown() {
        PopulateDb dbPopulator = new PopulateDb(DbConfig2.getSessionFactory());
        dbPopulator.deleteAllQuestions();

        QuestionDAO questionDAO = new QuestionDAO(DbConfig2.getSessionFactory());
        List<DbQuestion> dbQuestions = questionDAO.getAllQuestions();
        assertTrue(dbQuestions.size() == 0);
    }

}
