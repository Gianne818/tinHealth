package org.tin.oop2_capstone.database;
import java.sql.Connection;
import java.sql.PreparedStatement;


public class InsertData {

    // Insert a new user
    public boolean insertUser(String username, String password, String fullName) {
        String sql = "INSERT INTO users (username, password, full_name) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, fullName);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Insert a new quiz
    public boolean insertQuiz(String quizName) {
        String sql = "INSERT INTO quizzes (quiz_name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, quizName);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Insert a new question into a quiz
    public boolean insertQuestion(int quizId, String questionText, String correctAnswer) {
        String sql = "INSERT INTO questions (quiz_id, question_text, correct_answer) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quizId);
            pstmt.setString(2, questionText);
            pstmt.setString(3, correctAnswer);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Insert an initial score record for a user on a quiz (default 0)
    public boolean insertScore(int userId, int quizId) {
        String sql = "INSERT INTO scores (user_id, quiz_id, score) VALUES (?, ?, 0)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, quizId);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Insert a student's answer to a question
    public boolean insertAnswer(int userId, int questionId, String answerText) {
        String sql = "INSERT INTO answers (user_id, question_id, answer_text) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, questionId);
            pstmt.setString(3, answerText);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}