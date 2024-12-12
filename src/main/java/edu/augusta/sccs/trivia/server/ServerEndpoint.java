package edu.augusta.sccs.trivia.server;

import edu.augusta.sccs.trivia.*;
import edu.augusta.sccs.trivia.mysql.DbConfig2;
import edu.augusta.sccs.trivia.mysql.QuestionDAO;
import io.grpc.Server;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ServerEndpoint {

    private static final Logger logger = Logger.getLogger(Server.class.getName());


    private Server server;

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
        server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                .addService(new TrivaQuestionImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        System.out.println("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    if (server != null) {
                        server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
                    }                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }


    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            System.out.println("going to await termination");
            server.awaitTermination();
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        final ServerEndpoint server = new ServerEndpoint();
        server.start();
        server.blockUntilShutdown();
    }

    static class TrivaQuestionImpl extends TriviaQuestionsGrpc.TriviaQuestionsImplBase {

        @Override
        public void getQuestions(QuestionsRequest req, StreamObserver<QuestionsReply> responseObserver) {

            int difficulty = req.getDifficulty();
            int numberOfQuestions = req.getNumberOfQuestions();

            QuestionDAO questionDAO = new QuestionDAO(DbConfig2.getSessionFactory());
            Processor processor= new Processor(questionDAO, null, null);

            List<Question> questions = processor.getQuestionsByDifficulty(difficulty, numberOfQuestions);
            QuestionsReply.Builder builder = QuestionsReply.newBuilder();

            for(Question q: questions) {
                builder.addQuestions(q);
            }
            QuestionsReply reply2 = builder.build();

            responseObserver.onNext(reply2);
            responseObserver.onCompleted();
        }
    }
}
