package edu.augusta.sccs.trivia.server;

import edu.augusta.sccs.trivia.*;
import edu.augusta.sccs.trivia.mysql.DbPlayer;
import edu.augusta.sccs.trivia.mysql.DbQuestionResponse;
import edu.augusta.sccs.trivia.mysql.TriviaRepository;
import edu.augusta.sccs.trivia.questionsrepo.QuestionRepository;
import edu.augusta.sccs.trivia.questionsrepo.ServerQuestion;
import io.grpc.Server;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.stub.StreamObserver;


import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ServerEndpoint {

    private static final Logger logger = Logger.getLogger(ServerEndpoint.class.getName());
    private Server server;

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;

        /*
        QuestionRepository questionRepo = new QuestionRepository("questions", 9042, "datacenter1");
        CHANGE: "questions" to "localhost" if running Java on your machine and use "external-db-test.yml"
        NOTE: it appears that data center will always default to "datacenter1" if not specified so there should be little reason to change it
        */

        QuestionRepository questionRepo = new QuestionRepository("questions", 9042, "datacenter1");
        TriviaRepository triviaRepository = new TriviaRepository();

        server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                // add our repositories to the service
                .addService(new TriviaQuestionImpl(questionRepo, triviaRepository))
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
                    }
                } catch (InterruptedException e) {
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

    static class TriviaQuestionImpl extends TriviaQuestionsGrpc.TriviaQuestionsImplBase {
        // Repository instance for database operations
        private final QuestionRepository questionRepo;
        private final TriviaRepository triviaRepository;

        // Constructor injection of repositories
    public TriviaQuestionImpl(QuestionRepository questionRepo, TriviaRepository triviaRepository) {
                this.questionRepo = questionRepo;
                this.triviaRepository = triviaRepository;
            }

        @Override
        public void getQuestions(QuestionsRequest req, StreamObserver<QuestionsReply> responseObserver) {

            try {
                // Get questions from Cassandra matching request criteria
                List<ServerQuestion> questions = questionRepo.getQuestionsByDifficulty(req.getDifficulty(), req.getNumberOfQuestions());

                // Convert Cassandra questions to gRPC format
                QuestionsReply.Builder builder = QuestionsReply.newBuilder();
                for (ServerQuestion q : questions) {
                    builder.addQuestions(QuestionRepository.convertToQuestion(q));
                }

                QuestionsReply reply = builder.build();

                responseObserver.onNext(reply);
                responseObserver.onCompleted();
            } catch (Exception e) {
                // Handle unexpected errors
                logger.severe("Error retrieving questions: " + e.getMessage());
                responseObserver.onError(e);
            }
        }

        @Override
        public void submitAnswer(AnswerSubmission request, StreamObserver<AnswerResponse> responseObserver) {

            try {
                // Get player from database matching given player uuid
                DbPlayer player = triviaRepository.findPlayerByUuid(request.getPlayerId());

                // retrieve question for each answer submitted
                for (QuestionAnswer answer : request.getAnswersList()) {
                    ServerQuestion question = questionRepo.getQuestionById(UUID.fromString(answer.getQuestionUuid()));

                    // Create DbQuestionResponse for each answer in list
                    DbQuestionResponse response = new DbQuestionResponse();
                    response.setUuid(UUID.randomUUID());
                    response.setPlayer(player);
                    response.setQuestionUUID(UUID.fromString(answer.getQuestionUuid()));
                    // validate answer
                    response.setCorrect(question.getAnswer().equalsIgnoreCase(answer.getAnswer().trim()));
                    response.setTimestamp(Instant.ofEpochMilli(answer.getTimestampMillis()));

                    // persist question response
                    triviaRepository.save(response);
                }

                // Send response back to client
                AnswerResponse reply = AnswerResponse.newBuilder()
                        .setSuccess(true)
                        .build();
                responseObserver.onNext(reply);
                responseObserver.onCompleted();
            } catch (Exception e) {
                // Handle unexpected errors
                logger.severe("Error processing answer: " + e.getMessage());
                responseObserver.onError(e);
            }
        }

        @Override
        public void getPlayer(PlayerRequest request,  StreamObserver<PlayerReply> responseObserver) {
            // Get player from database matching given player uuid
            DbPlayer dbPlayer = triviaRepository.findPlayerByUuid(request.getUuid());

            try {
                // Convert database Player to gRPC format
                PlayerReply.Builder builder = PlayerReply.newBuilder();
                Player player = Player.newBuilder()
                        .setUuid(dbPlayer.getUuid().toString())
                        .setUsername(dbPlayer.getUsername())
                        .setLastDifficulty(dbPlayer.getLastDifficulty())
                        .build();

                builder.setPlayer(player);

                // Send response back to client
                PlayerReply reply = builder.build();
                responseObserver.onNext(reply);
                responseObserver.onCompleted();
            }catch (Exception e) {
                // Handle unexpected errors
                logger.severe("Player not found: " + e.getMessage());
                responseObserver.onError(e);
            }
        }

        @Override
        public void registerPlayer(PlayerRegistrationRequest request, StreamObserver<PlayerReply> responseObserver) {

            try {
                // Extract player details from the request
                Player player = request.getPlayer();

                // Create a new DbPlayer for the database
                DbPlayer dbPlayer = new DbPlayer();
                dbPlayer.setUuid(UUID.randomUUID()); // Generate a unique UUID
                dbPlayer.setUsername(player.getUsername());
                dbPlayer.setLastDifficulty(player.getLastDifficulty());

                // Save the new player to the database
                triviaRepository.save(dbPlayer);

                // Build the gRPC Player object to return in the response
                Player grpcPlayer = Player.newBuilder()
                        .setUuid(dbPlayer.getUuid().toString())
                        .setUsername(dbPlayer.getUsername())
                        .setLastDifficulty(dbPlayer.getLastDifficulty())
                        .build();

                // Build and send the PlayerReply response
                PlayerReply reply = PlayerReply.newBuilder()
                        .setPlayer(grpcPlayer)
                        .build();

                responseObserver.onNext(reply);
                responseObserver.onCompleted();

            } catch (Exception e) {
                // Handle unexpected errors
                logger.severe("Error registering player: " + e.getMessage());
                responseObserver.onError(e);
            }
        }
    }
}


