package com.sudoku.database;

import org.junit.jupiter.api.Test;
import java.sql.*;

/**
 * Utility test to query and display database contents.
 */
class QueryDatabaseTest {
    
    @Test
    void queryAllData() {
        try {
            DatabaseManager dbManager = DatabaseManager.getInstance();
            Connection conn = dbManager.getConnection();
            Statement stmt = conn.createStatement();
            
            System.out.println("\n========================================");
            System.out.println("=== PLAYER_STATS (玩家统计数据) ===");
            System.out.println("========================================");
            ResultSet rs = stmt.executeQuery("SELECT * FROM player_stats ORDER BY best_time");
            int count = 0;
            while (rs.next()) {
                count++;
                long bestTime = rs.getLong("best_time");
                String bestTimeStr = bestTime > 0 ? String.format("%d:%02d", bestTime/60000, (bestTime/1000)%60) : "N/A";
                System.out.printf("%d. 玩家: %-15s | 游戏: %d次 | 胜利: %d次 | 最佳: %s%n",
                    count,
                    rs.getString("player_name"),
                    rs.getInt("games_played"),
                    rs.getInt("games_won"),
                    bestTimeStr);
            }
            if (count == 0) {
                System.out.println("(暂无数据)");
            }
            rs.close();
            
            System.out.println("\n========================================");
            System.out.println("=== GAME_HISTORY (最近游戏历史) ===");
            System.out.println("========================================");
            rs = stmt.executeQuery("SELECT * FROM game_history ORDER BY date_played DESC FETCH FIRST 10 ROWS ONLY");
            count = 0;
            while (rs.next()) {
                count++;
                int diff = rs.getInt("difficulty");
                String diffStr = diff == 1 ? "Easy" : (diff == 2 ? "Medium" : "Hard");
                long time = rs.getLong("completion_time");
                System.out.printf("%d. 玩家: %-15s | 难度: %-6s | 用时: %d:%02d | 日期: %s%n",
                    count,
                    rs.getString("player_name"),
                    diffStr,
                    time/60000, (time/1000)%60,
                    rs.getTimestamp("date_played").toString().substring(0, 16));
            }
            if (count == 0) {
                System.out.println("(暂无数据)");
            }
            rs.close();
            
            stmt.close();
            System.out.println("\n========================================\n");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
