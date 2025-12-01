# Sudoku 项目完整开发指南

> 本文档面向编程初学者，详细解释项目的每个部分。

---

## 目录

1. [项目概述](#1-项目概述)
2. [技术栈介绍](#2-技术栈介绍)
3. [项目结构详解](#3-项目结构详解)
4. [核心代码解析](#4-核心代码解析)
5. [设计模式应用](#5-设计模式应用)
6. [数据库设计](#6-数据库设计)
7. [如何运行项目](#7-如何运行项目)
8. [常见问题解答](#8-常见问题解答)

---

## 1. 项目概述

### 这是什么项目？

这是一个用 Java 开发的数独（Sudoku）游戏。数独是一种数字填充游戏，需要在 9×9 的格子中填入 1-9 的数字，使得每行、每列、每个 3×3 小方格内的数字都不重复。

### 项目能做什么？

| 功能 | 说明 |
|------|------|
| 生成数独谜题 | 自动生成不同难度的数独题目 |
| 验证输入 | 检查玩家输入是否符合规则 |
| 提示功能 | 当卡住时给出提示 |
| 自动求解 | 一键解决当前谜题 |
| 保存/加载 | 保存游戏进度，下次继续 |
| 玩家统计 | 记录游戏次数、最佳时间等 |
| 排行榜 | 显示所有玩家的最佳成绩 |

---

## 2. 技术栈介绍

### 2.1 Java

Java 是一种面向对象的编程语言。本项目使用 Java 21。

**为什么用 Java？**
- 跨平台：一次编写，到处运行
- 成熟的 GUI 库（Swing）
- 丰富的数据库支持

### 2.2 Maven

Maven 是一个项目管理工具，帮助我们：
- 管理项目依赖（自动下载需要的库）
- 编译代码
- 运行测试
- 打包项目

**pom.xml 文件说明：**
```xml
<dependencies>
    <!-- Apache Derby 数据库 -->
    <dependency>
        <groupId>org.apache.derby</groupId>
        <artifactId>derby</artifactId>
    </dependency>
    
    <!-- JUnit 测试框架 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-engine</artifactId>
    </dependency>
</dependencies>
```

### 2.3 Swing

Swing 是 Java 的 GUI（图形用户界面）工具包。

**常用组件：**
| 组件 | 作用 |
|------|------|
| `JFrame` | 主窗口 |
| `JPanel` | 容器面板 |
| `JButton` | 按钮 |
| `JTextField` | 文本输入框 |
| `JLabel` | 文本标签 |
| `JMenuBar` | 菜单栏 |

### 2.4 Apache Derby

Derby 是一个嵌入式数据库，不需要单独安装数据库服务器。

**优点：**
- 零配置，开箱即用
- 数据存储在本地文件夹中
- 支持标准 SQL

---

## 3. 项目结构详解

```
pdcgameproject/
│
├── src/main/java/com/sudoku/          # 源代码目录
│   │
│   ├── SudokuGameApplication.java     # 【入口】程序启动点
│   │
│   ├── model/                         # 【模型层】数据和业务逻辑
│   │   ├── Puzzle.java                # 抽象类：所有谜题的基类
│   │   ├── Solvable.java              # 接口：可求解的谜题
│   │   ├── SudokuPuzzle.java          # 数独谜题的具体实现
│   │   ├── Hint.java                  # 提示信息
│   │   ├── GameHistory.java           # 游戏历史记录
│   │   ├── PlayerStats.java           # 玩家统计数据
│   │   └── SavedGame.java             # 保存的游戏
│   │
│   ├── gui/                           # 【视图层】用户界面
│   │   ├── SudokuMainFrame.java       # 主窗口框架
│   │   ├── SudokuGamePanel.java       # 游戏面板（数独网格）
│   │   └── GameController.java        # 游戏控制器
│   │
│   ├── database/                      # 【数据层】数据库操作
│   │   ├── DatabaseManager.java       # 数据库连接管理
│   │   └── GameDAO.java               # 数据访问对象
│   │
│   ├── exception/                     # 自定义异常
│   │   └── SudokuException.java
│   │
│   └── util/                          # 工具类
│       └── ValidationUtils.java       # 输入验证工具
│
├── src/test/java/com/sudoku/          # 测试代码目录
│   ├── model/SudokuPuzzleTest.java    # 数独逻辑测试
│   ├── database/GameDAOTest.java      # 数据库操作测试
│   ├── gui/SudokuGamePanelTest.java   # GUI测试
│   └── util/ValidationUtilsTest.java  # 验证工具测试
│
├── pom.xml                            # Maven 配置文件
├── README.md                          # 项目说明
└── sudokuDB/                          # Derby 数据库文件夹（自动生成）
```

---

## 4. 核心代码解析

### 4.1 程序入口 - SudokuGameApplication.java

```java
public class SudokuGameApplication {
    public static void main(String[] args) {
        // 1. 设置界面外观
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        
        // 2. 设置对话框按钮为英文
        UIManager.put("OptionPane.okButtonText", "OK");
        UIManager.put("OptionPane.cancelButtonText", "Cancel");
        
        // 3. 在事件调度线程中启动GUI
        SwingUtilities.invokeLater(() -> {
            initializeApplication();
        });
    }
}
```

**关键点：**
- `main()` 是程序的起点
- `SwingUtilities.invokeLater()` 确保 GUI 在正确的线程中运行

### 4.2 数独逻辑 - SudokuPuzzle.java

```java
public class SudokuPuzzle extends Puzzle implements Solvable {
    
    // 检查某个位置能否放置某个数字
    public boolean isValidMove(int row, int col, int value) {
        // 检查行是否有重复
        for (int c = 0; c < 9; c++) {
            if (grid[row][c] == value) return false;
        }
        
        // 检查列是否有重复
        for (int r = 0; r < 9; r++) {
            if (grid[r][col] == value) return false;
        }
        
        // 检查3x3方格是否有重复
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int r = boxRow; r < boxRow + 3; r++) {
            for (int c = boxCol; c < boxCol + 3; c++) {
                if (grid[r][c] == value) return false;
            }
        }
        
        return true;  // 没有冲突，可以放置
    }
}
```

**这段代码做了什么？**
1. 检查同一行是否已有相同数字
2. 检查同一列是否已有相同数字
3. 检查同一个 3×3 小方格是否已有相同数字

### 4.3 界面组件 - SudokuGamePanel.java

```java
public class SudokuGamePanel extends JPanel {
    
    // 颜色常量 - 深色主题
    private static final Color BG_PRIMARY = new Color(25, 25, 28);      // 主背景色
    private static final Color ACCENT_PRIMARY = new Color(99, 102, 241); // 强调色
    
    // 创建一个单元格
    private JTextField createCell(int row, int col) {
        JTextField cell = new JTextField();
        cell.setHorizontalAlignment(JTextField.CENTER);  // 文字居中
        cell.setFont(new Font("Segoe UI", Font.BOLD, 24)); // 设置字体
        cell.setBackground(USER_CELL_COLOR);              // 设置背景色
        
        // 添加键盘监听器 - 当用户输入时触发
        cell.addKeyListener(new CellKeyListener(row, col));
        
        return cell;
    }
}
```

**这段代码做了什么？**
- 定义界面颜色
- 创建输入单元格
- 设置字体和样式
- 添加事件监听器

### 4.4 数据库操作 - GameDAO.java

```java
public class GameDAO {
    
    // 保存游戏历史
    public boolean saveGameHistory(GameHistory gameHistory) {
        String sql = """
            INSERT INTO game_history 
            (player_name, difficulty, completion_time, moves_count, hints_used)
            VALUES (?, ?, ?, ?, ?)
            """;
        
        int rowsAffected = dbManager.executeUpdate(sql,
            gameHistory.getPlayerName(),
            gameHistory.getDifficulty(),
            gameHistory.getCompletionTime(),
            gameHistory.getMovesCount(),
            gameHistory.getHintsUsed()
        );
        
        return rowsAffected > 0;
    }
}
```

**DAO 是什么？**
- DAO = Data Access Object（数据访问对象）
- 专门负责与数据库交互
- 将数据库操作与业务逻辑分离

---

## 5. 设计模式应用

### 5.1 单例模式 (Singleton)

**用在哪里？** `DatabaseManager.java`

**为什么用？** 确保整个程序只有一个数据库连接实例。

```java
public class DatabaseManager {
    private static DatabaseManager instance;  // 唯一实例
    
    private DatabaseManager() {  // 私有构造函数，防止外部创建
        initializeDatabase();
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
}
```

### 5.2 MVC 模式 (Model-View-Controller)

**三层结构：**

| 层 | 文件 | 职责 |
|---|------|------|
| Model（模型） | `SudokuPuzzle.java` | 数据和业务逻辑 |
| View（视图） | `SudokuGamePanel.java` | 用户界面显示 |
| Controller（控制器） | `GameController.java` | 协调模型和视图 |

**好处：**
- 代码分离，易于维护
- 可以单独修改界面而不影响逻辑
- 方便测试

### 5.3 DAO 模式 (Data Access Object)

**用在哪里？** `GameDAO.java`

**好处：**
- 将数据库操作封装在一个类中
- 业务代码不需要知道数据库细节
- 方便切换数据库类型

---

## 6. 数据库设计

### 6.1 数据库表结构

#### game_history（游戏历史）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | INTEGER | 主键，自增 |
| player_name | VARCHAR(100) | 玩家名称 |
| difficulty | INTEGER | 难度 (1=Easy, 2=Medium, 3=Hard) |
| completion_time | BIGINT | 完成时间（毫秒） |
| date_played | TIMESTAMP | 游戏日期 |
| moves_count | INTEGER | 移动次数 |
| hints_used | INTEGER | 使用提示次数 |

#### player_stats（玩家统计）
| 字段 | 类型 | 说明 |
|------|------|------|
| player_name | VARCHAR(100) | 主键，玩家名称 |
| games_played | INTEGER | 游戏总次数 |
| games_won | INTEGER | 获胜次数 |
| total_time | BIGINT | 总游戏时间 |
| best_time | BIGINT | 最佳时间 |
| average_time | BIGINT | 平均时间 |

#### saved_games（保存的游戏）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | INTEGER | 主键，自增 |
| player_name | VARCHAR(100) | 玩家名称 |
| game_name | VARCHAR(100) | 存档名称 |
| grid_data | VARCHAR(200) | 当前网格数据 |
| original_grid | VARCHAR(200) | 原始网格数据 |
| difficulty | INTEGER | 难度 |
| elapsed_time | BIGINT | 已用时间 |

---

## 7. 如何运行项目

### 7.1 环境准备

1. **安装 Java 21**
   - 下载：https://adoptium.net/
   - 安装后验证：`java -version`

2. **安装 Maven**
   - 下载：https://maven.apache.org/
   - 安装后验证：`mvn -version`

### 7.2 运行步骤

```bash
# 1. 进入项目目录
cd pdcgameproject

# 2. 编译项目
mvn clean compile

# 3. 运行游戏
mvn exec:java

# 4. 运行测试（可选）
mvn test
```

### 7.3 常用命令

| 命令 | 作用 |
|------|------|
| `mvn clean` | 清理编译产物 |
| `mvn compile` | 编译源代码 |
| `mvn test` | 运行单元测试 |
| `mvn exec:java` | 运行程序 |
| `mvn package` | 打包成 JAR 文件 |

---

## 8. 常见问题解答

### Q1: 运行时报错 "Database driver not found"
**解决：** 运行 `mvn clean compile` 重新编译，确保依赖下载完成。

### Q2: Statistics 没有数据
**原因：** 需要先完成一局游戏，数据才会保存。
**解决：** 开始新游戏 → 完成或点击 Solve → 查看 Statistics。

### Q3: 如何修改界面颜色？
**位置：** `SudokuGamePanel.java` 文件顶部的颜色常量
```java
private static final Color BG_PRIMARY = new Color(25, 25, 28);
```

### Q4: 如何添加新难度级别？
**步骤：**
1. 修改 `SudokuPuzzle.java` 中的 `generatePuzzle()` 方法
2. 修改 `GameController.java` 中的难度选择逻辑
3. 更新界面显示

### Q5: 数据存在哪里？
**位置：** 项目根目录下的 `sudokuDB/` 文件夹
**注意：** 删除此文件夹会清空所有数据！

---

## 总结

这个项目展示了：

1. **面向对象编程**：类、继承、接口、封装
2. **设计模式**：Singleton、MVC、DAO
3. **GUI 开发**：Swing 组件和事件处理
4. **数据库操作**：JDBC 和 SQL
5. **软件工程**：代码组织、测试、版本控制

希望这份指南能帮助你理解整个项目！如有疑问，欢迎查阅源代码或提问。

---

*文档版本：1.0 | 最后更新：2025年12月*
