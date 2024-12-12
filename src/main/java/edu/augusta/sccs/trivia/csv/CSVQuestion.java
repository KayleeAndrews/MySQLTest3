package edu.augusta.sccs.trivia.csv;

import com.opencsv.bean.CsvBindByName;

public class CSVQuestion {
    @CsvBindByName(column="Difficulty")
    private int difficulty;

    @CsvBindByName(column="QuestionTxt")
    private String questionText;

    @CsvBindByName(column="AnswerTxt")
    private String answerText;

    @CsvBindByName(column="AnswerType")
    private String answerType;

    @CsvBindByName(column="Choices")
    private String choices;

    public int getDifficulty() {
        return difficulty;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getAnswerText() {
        return answerText;
    }

    public String getAnswerType() {
        return answerType;
    }

    public String getChoices() {
        return choices;
    }

    @Override
    public String toString() {
        return "edu.augusta.sccs.trivia.csv.CSVQuestion{" +
                "difficulty=" + difficulty +
                ", questionText='" + questionText + '\'' +
                ", answerText='" + answerText + '\'' +
                ", answerType='" + answerType + '\'' +
                ", choices='" + choices + '\'' +
                '}';
    }
}
