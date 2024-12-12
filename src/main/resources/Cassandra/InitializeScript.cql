-- Create a keyspace
CREATE KEYSPACE IF NOT EXISTS trivia WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : '1' };

-- Create a table
CREATE TABLE IF NOT EXISTS trivia.questions (
question_uuid uuid PRIMARY KEY,
answerType int,
difficulty int,
answer text,
choices text,
question text
);


CREATE INDEX IF NOT EXISTS questions_difficulty_idx ON trivia.questions (difficulty);

