package edu.augusta.sccs.trivia.test;

import edu.augusta.sccs.trivia.Question;
import edu.augusta.sccs.trivia.mysql.*;
import edu.augusta.sccs.trivia.server.Processor;
import edu.augusta.sccs.trivia.setup.PopulateDb;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DbVerificationTest {

    public static final String QUESTION_CSV = "C:/miniprojectdm/MySQLTest3/TriviaQsRound3.csv";
    public static final int NUM_QUESTIONS = 100;
    public static final int QUESTION_SUBSET = 5;

    @BeforeAll
    public static void setup() {
        PopulateDb dbPopulator = new PopulateDb(DbConfig1.getSessionFactory());
        dbPopulator.populateDb(QUESTION_CSV);

        QuestionDAO questionDAO = new QuestionDAO(DbConfig1.getSessionFactory());
        List<DbQuestion> dbQuestions = questionDAO.getAllQuestions();
        assertTrue(dbQuestions.size() > 0);
    }

    @Test
    public void testDbSize() {
        QuestionDAO questionDAO = new QuestionDAO(DbConfig1.getSessionFactory());
        List<DbQuestion> dbQuestions = questionDAO.getAllQuestions();
        assertTrue(dbQuestions.size() == NUM_QUESTIONS);
    }

    @Test
    public void testDbProcessorAll() {
        QuestionDAO questionDAO = new QuestionDAO(DbConfig1.getSessionFactory());
        Processor processor= new Processor(questionDAO, null, null);
        List<Question> questions = processor.getAllQuestions();
        assertTrue(questions.size() == NUM_QUESTIONS);
    }

    @Test
    public void testQueryEasy() {
        QuestionDAO questionDAO = new QuestionDAO(DbConfig1.getSessionFactory());
        Processor processor= new Processor(questionDAO, null, null);
        List<Question> questions = processor.getQuestionsByDifficulty(1, QUESTION_SUBSET);
        assertTrue(questions.size() == QUESTION_SUBSET);
        for(Question q: questions) {
            assertTrue(q.getDifficulty() == 1);
        }
    }

    @Test
    public void testQueryTwo() {
        QuestionDAO questionDAO = new QuestionDAO(DbConfig1.getSessionFactory());
        Processor processor= new Processor(questionDAO, null, null);
        List<Question> questions = processor.getQuestionsByDifficulty(2, QUESTION_SUBSET);
        assertTrue(questions.size() == QUESTION_SUBSET);
        for(Question q: questions) {
            assertTrue(q.getDifficulty() == 2);
        }
    }

    @Test
    public void testQueryThree() {
        QuestionDAO questionDAO = new QuestionDAO(DbConfig1.getSessionFactory());
        Processor processor= new Processor(questionDAO, null, null);
        List<Question> questions = processor.getQuestionsByDifficulty(3, QUESTION_SUBSET);
        assertTrue(questions.size() == QUESTION_SUBSET);
        for(Question q: questions) {
            assertTrue(q.getDifficulty() == 3);
        }
    }

    @Test
    public void testQueryFour() {
        QuestionDAO questionDAO = new QuestionDAO(DbConfig1.getSessionFactory());
        Processor processor= new Processor(questionDAO, null, null);
        List<Question> questions = processor.getQuestionsByDifficulty(4, QUESTION_SUBSET);
        assertTrue(questions.size() == QUESTION_SUBSET);
        for(Question q: questions) {
            assertTrue(q.getDifficulty() == 4);
        }
    }

    @Test
    public void testQueryFive() {
        QuestionDAO questionDAO = new QuestionDAO(DbConfig1.getSessionFactory());
        Processor processor= new Processor(questionDAO, null, null);
        List<Question> questions = processor.getQuestionsByDifficulty(5, QUESTION_SUBSET);
        assertTrue(questions.size() == QUESTION_SUBSET);
        for(Question q: questions) {
            assertTrue(q.getDifficulty() == 5);
        }
    }

    @Test
    public void testCreatePlayer() {
        PlayerDAO playerDAO = new PlayerDAO(DbConfig1.getSessionFactory());
        DbPlayer player = DbPlayer.createPlayer("waylon", 3);
        playerDAO.addPlayer(player);
        DbPlayer player2 = playerDAO.getPlayerById(player.getUuid());
        assertTrue(player2.getUsername().equals("waylon"));
        playerDAO.deletePlayer(player.getUuid());
    }

    @Test
    public void testCreatePlayerResponse() {
        PlayerDAO playerDAO = new PlayerDAO(DbConfig1.getSessionFactory());
        DbPlayer player = DbPlayer.createPlayer("waylon", 3);
        playerDAO.addPlayer(player);
        int hashPlayer = player.getUuid().hashCode();
        DbPlayer player2 = playerDAO.getPlayerById(player.getUuid());
        assertTrue(player2.getUsername().equals("waylon"));
        assertTrue(hashPlayer == player2.getUuid().hashCode());

        QuestionDAO questionDAO = new QuestionDAO(DbConfig1.getSessionFactory());
        List<DbQuestion> questions = questionDAO.getQuestionsByDifficulty(3, 1);
        assertTrue(questions.size() == 1);

        DbQuestion question = questions.get(0);
        int hashQuestion = question.getUuid().hashCode();
        assertTrue(question.getDifficulty() == 3);

        ResponseDAO responseDAO = new ResponseDAO(DbConfig1.getSessionFactory());
        Processor processor= new Processor(questionDAO, playerDAO, responseDAO);

        DbQuestionResponse response = processor.reportQuestionResponse(player.getUuid(), question.getUuid(), true);

        assertTrue(response.isCorrect());

        DbQuestionResponse responseCheck = responseDAO.getResponseById(response.getUuid());
        DbPlayer player3 = responseCheck.getPlayer();
        assertTrue(player3.getUsername().equals("waylon"));
        assertTrue(player3.getUuid().hashCode() == hashPlayer);
        DbQuestion questionCheck = responseCheck.getQuestion();
        assertTrue(questionCheck.getDifficulty() == 3);
        assertTrue(questionCheck.getUuid().hashCode() == hashQuestion);
        responseDAO.deleteResponse(response.getUuid());
        playerDAO.deletePlayer(player.getUuid());
    }

    @AfterAll
    public static void teardown() {
        PopulateDb dbPopulator = new PopulateDb(DbConfig1.getSessionFactory());
        dbPopulator.deleteAllQuestions();

        QuestionDAO questionDAO = new QuestionDAO(DbConfig1.getSessionFactory());
        List<DbQuestion> dbQuestions = questionDAO.getAllQuestions();
        assertTrue(dbQuestions.size() == 0);
    }

}
