CREATE USER IF NOT EXISTS 'dbuser' IDENTIFIED BY 'triviadb';
CREATE DATABASE  IF NOT EXISTS `trivia` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `trivia`;
GRANT ALL PRIVILEGES ON trivia.* TO 'dbuser' WITH GRANT OPTION;
FLUSH PRIVILEGES;

DROP TABLE IF EXISTS `players`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `players` (
  `lastDifficulty` tinyint NOT NULL,
  `username` varchar(255) NOT NULL,
  `uuid` varchar(255) NOT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `questions`
--

DROP TABLE IF EXISTS `questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `questions` (
  `answerType` tinyint NOT NULL,
  `difficulty` tinyint NOT NULL,
  `answer` varchar(255) NOT NULL,
  `choices` varchar(255) DEFAULT NULL,
  `question` varchar(255) NOT NULL,
  `uuid` varchar(255) NOT NULL,
  PRIMARY KEY (`uuid`),
  CONSTRAINT `questions_chk_1` CHECK ((`answerType` between 0 and 3))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `responses`
--

DROP TABLE IF EXISTS `responses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `responses` (
  `correct` bit(1) NOT NULL,
  `timestamp` datetime(6) NOT NULL,
  `playerUuid` varchar(255) NOT NULL,
  `questionUuid` varchar(255) NOT NULL,
  `uuid` varchar(255) NOT NULL,
  PRIMARY KEY (`uuid`),
  KEY `PlayerToQuestionResponseForeignKey` (`playerUuid`),
  KEY `QuestionTOQuestionResponseForeignKey` (`questionUuid`),
  CONSTRAINT `PlayerToQuestionResponseForeignKey` FOREIGN KEY (`playerUuid`) REFERENCES `players` (`uuid`),
  CONSTRAINT `QuestionTOQuestionResponseForeignKey` FOREIGN KEY (`questionUuid`) REFERENCES `questions` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
