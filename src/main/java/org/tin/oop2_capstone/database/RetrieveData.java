package org.tin.oop2_capstone.database;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class RetrieveData {

    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("password")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Quiz> getQuizzesForStudent(int userId) {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT q.quiz_id, q.quiz_name, COALESCE(s.score, 0) AS score "
                + "FROM quizzes q "
                + "LEFT JOIN scores s ON q.quiz_id = s.quiz_id AND s.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                quizzes.add(new Quiz(
                        rs.getInt("quiz_id"),
                        rs.getString("quiz_name"),
                        rs.getInt("score")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return quizzes;
    }

    public List<Question> getQuestionsForQuiz(int quizId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE quiz_id = ? ORDER BY question_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quizId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                questions.add(new Question(
                        rs.getInt("question_id"),
                        rs.getInt("quiz_id"),
                        rs.getString("question_text"),
                        rs.getString("correct_answer")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questions;
    }

    public String getSavedAnswer(int userId, int questionId) {
        String sql = "SELECT answer_text FROM answers WHERE user_id = ? AND question_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, questionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString("answer_text");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
