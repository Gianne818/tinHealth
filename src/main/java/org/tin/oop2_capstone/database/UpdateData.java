package org.tin.oop2_capstone.database;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class UpdateData {
    public void saveAnswer(int userId, int questionId, String answerText) {
        // Upsert: insert or update if already exists
        String checkSql = "SELECT answer_id FROM answers WHERE user_id = ? AND question_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setInt(1, userId);
            check.setInt(2, questionId);
            var rs = check.executeQuery();
            if (rs.next()) {
                // update
                String updateSql = "UPDATE answers SET answer_text = ? WHERE user_id = ? AND question_id = ?";
                try (PreparedStatement upd = conn.prepareStatement(updateSql)) {
                    upd.setString(1, answerText);
                    upd.setInt(2, userId);
                    upd.setInt(3, questionId);
                    upd.executeUpdate();
                }
            } else {
                // insert
                String insertSql = "INSERT INTO answers (user_id, question_id, answer_text) VALUES (?, ?, ?)";
                try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                    ins.setInt(1, userId);
                    ins.setInt(2, questionId);
                    ins.setString(3, answerText);
                    ins.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveScore(int userId, int quizId, int score) {
        String checkSql = "SELECT score_id FROM scores WHERE user_id = ? AND quiz_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setInt(1, userId);
            check.setInt(2, quizId);
            var rs = check.executeQuery();
            if (rs.next()) {
                String updateSql = "UPDATE scores SET score = ? WHERE user_id = ? AND quiz_id = ?";
                try (PreparedStatement upd = conn.prepareStatement(updateSql)) {
                    upd.setInt(1, score);
                    upd.setInt(2, userId);
                    upd.setInt(3, quizId);
                    upd.executeUpdate();
                }
            } else {
                String insertSql = "INSERT INTO scores (user_id, quiz_id, score) VALUES (?, ?, ?)";
                try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                    ins.setInt(1, userId);
                    ins.setInt(2, quizId);
                    ins.setInt(3, score);
                    ins.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
