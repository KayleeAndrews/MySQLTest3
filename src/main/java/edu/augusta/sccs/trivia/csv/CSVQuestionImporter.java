package edu.augusta.sccs.trivia.csv;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class CSVQuestionImporter {


    public static void main(String[] args) {
        List<CSVQuestion> questions = null;
        try {
            FileReader fileReader = new FileReader("C:/Users/tyler/Downloads/MySQLTest3/TriviaQsRound3.csv");
            questions = new CsvToBeanBuilder<CSVQuestion>(fileReader).withType(CSVQuestion.class).build().parse();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        int i = 0;
        for(CSVQuestion q: questions) {
            System.out.println(++i + q.toString());
        }
    }
}
