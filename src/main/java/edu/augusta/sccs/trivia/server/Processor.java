package edu.augusta.sccs.trivia.server;

import edu.augusta.sccs.trivia.Question;
import edu.augusta.sccs.trivia.mysql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Processor {
    private QuestionDAO questionDAO;
    private PlayerDAO playerDAO;
    private ResponseDAO responseDAO;

    public Processor(QuestionDAO questionDAO, PlayerDAO playerDAO, ResponseDAO responseDAO) {
        this.questionDAO = questionDAO;
        this.playerDAO = playerDAO;
        this.responseDAO = responseDAO;
    }

    public List<Question> getAllQuestions() {
        List<DbQuestion> dbQuestions = questionDAO.getAllQuestions();
        List<Question> questions = new ArrayList<>();

        for(DbQuestion dbQuestion : dbQuestions) {
            questions.add(QuestionDAO.convertToQuestion(dbQuestion));
        }

        return questions;
    }

    public List<Question> getQuestionsByDifficulty(int difficulty, int numQuestions) {
        List<DbQuestion> dbQuestions = questionDAO.getQuestionsByDifficulty(difficulty, numQuestions);
        List<Question> questions = new ArrayList<>();

        for(DbQuestion dbQuestion : dbQuestions) {
            questions.add(QuestionDAO.convertToQuestion(dbQuestion));
        }

        return questions;
    }

    public DbQuestionResponse reportQuestionResponse(UUID playerUuid, UUID questionUuid, boolean correct) {
        DbPlayer player = playerDAO.getPlayerById(playerUuid);
        DbQuestion question = questionDAO.getQuestionById(questionUuid);

        return responseDAO.reportQuestionResponse(player, question, correct);

    }


}
