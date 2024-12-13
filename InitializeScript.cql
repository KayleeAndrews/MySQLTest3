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

CREATE INDEX ON trivia.questions (difficulty);

-- Insert data from CSV
COPY trivia.questions (answerType, difficulty, answer, choices, question, question_uuid)
FROM 'Questions.csv'
WITH HEADER = TRUE;