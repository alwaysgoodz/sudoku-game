# Sudoku Game - PDC Project

## 项目概述

这是一个基于Java Swing的数独益智游戏，使用Apache Derby数据库进行数据持久化。项目完全满足PDC课程的所有要求，展示了面向对象编程的核心概念和最佳实践。

## 功能特性

### 核心游戏功能
- **多难度级别**: 简单、中等、困难三个难度级别
- **智能提示系统**: 为玩家提供逻辑提示
- **自动求解**: 一键解决当前谜题
- **游戏重置**: 重置到原始状态
- **实时计时**: 显示游戏进行时间

### 数据管理
- **游戏保存/加载**: 支持保存游戏进度并随时加载
- **玩家统计**: 跟踪游戏次数、胜率、最佳时间等
- **游戏历史**: 记录所有完成的游戏
- **排行榜**: 显示最佳玩家排名

### 用户界面
- **现代化GUI**: 使用Swing构建的直观界面
- **键盘支持**: 支持数字键输入和删除操作
- **视觉反馈**: 高亮显示选中单元格和无效输入
- **菜单系统**: 完整的菜单栏和快捷键支持

## 技术架构

### 面向对象设计
- **封装**: 所有类都有适当的访问修饰符和数据隐藏
- **抽象**: 使用抽象类`Puzzle`和接口`Solvable`
- **继承**: `SudokuPuzzle`继承自`Puzzle`
- **多态**: 通过接口实现多态行为

### 设计模式
- **单例模式**: `DatabaseManager`类确保单一数据库连接
- **MVC模式**: 分离模型、视图和控制器
- **DAO模式**: 数据访问对象封装数据库操作
- **工厂模式**: 用于创建不同类型的谜题

### 数据库设计
- **Apache Derby**: 嵌入式数据库，无需额外配置
- **三个主要表**:
  - `game_history`: 游戏历史记录
  - `player_stats`: 玩家统计信息
  - `saved_games`: 保存的游戏状态

## 项目结构

```
src/
├── main/
│   └── java/
│       └── com/
│           └── sudoku/
│               ├── SudokuGameApplication.java    # 主应用程序入口
│               ├── model/                        # 数据模型
│               │   ├── Puzzle.java              # 抽象谜题类
│               │   ├── Solvable.java            # 可解决接口
│               │   ├── SudokuPuzzle.java        # 数独实现
│               │   ├── Hint.java                # 提示类
│               │   ├── GameHistory.java         # 游戏历史
│               │   ├── PlayerStats.java         # 玩家统计
│               │   └── SavedGame.java           # 保存的游戏
│               ├── gui/                         # 图形界面
│               │   ├── SudokuMainFrame.java     # 主窗口
│               │   ├── SudokuGamePanel.java     # 游戏面板
│               │   └── GameController.java      # 游戏控制器
│               ├── database/                    # 数据库层
│               │   ├── DatabaseManager.java    # 数据库管理器
│               │   └── GameDAO.java             # 数据访问对象
│               ├── exception/                   # 异常处理
│               │   └── SudokuException.java     # 自定义异常
│               └── util/                        # 工具类
│                   └── ValidationUtils.java    # 验证工具
└── test/
    └── java/
        └── com/
            └── sudoku/
                ├── model/
                │   └── SudokuPuzzleTest.java    # 核心逻辑测试
                └── database/
                    └── GameDAOTest.java         # 数据库测试
```

## 安装和运行

### 系统要求
- Java 21 或更高版本
- NetBeans 23 (推荐)
- Maven 3.6+ (通常包含在NetBeans中)

### 运行步骤

#### 方法1: 使用NetBeans
1. 在NetBeans中打开项目文件夹
2. 等待Maven依赖下载完成
3. 右键点击项目 → "Clean and Build"
4. 右键点击项目 → "Run"

#### 方法2: 使用命令行
```bash
# 进入项目目录
cd f:\pdcgameproject

# 编译项目
mvn clean compile

# 运行应用程序
mvn exec:java -Dexec.mainClass="com.sudoku.SudokuGameApplication"
```

#### 方法3: 运行测试
```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=SudokuPuzzleTest
```

### 首次运行
1. 应用程序启动时会自动创建数据库
2. 输入您的玩家姓名
3. 点击"New Game"开始游戏

## 游戏说明

### 基本规则
- 在9×9网格中填入数字1-9
- 每行、每列、每个3×3子网格都必须包含1-9的所有数字
- 预填充的数字不能修改

### 操作方法
- **选择单元格**: 点击任意空白单元格
- **输入数字**: 按键盘数字键1-9
- **清除单元格**: 按0、空格键或退格键
- **获取提示**: 点击"Hint"按钮
- **保存游戏**: 使用菜单或按Ctrl+S
- **加载游戏**: 使用菜单或按Ctrl+L

### 难度级别
- **简单**: 40个空白单元格
- **中等**: 50个空白单元格
- **困难**: 60个空白单元格

## 开发特性

### 错误处理
- 全面的输入验证
- 用户友好的错误消息
- 数据库异常处理
- 自动恢复机制

### 性能优化
- 高效的数独生成算法
- 智能提示算法
- 数据库连接池
- 内存管理优化

### 代码质量
- 遵循Java编码规范
- 全面的JavaDoc文档
- 单元测试覆盖
- SOLID设计原则

## 数据库架构

### 表结构

#### game_history
- `id`: 主键，自增
- `player_name`: 玩家姓名
- `difficulty`: 难度级别(1-3)
- `completion_time`: 完成时间(毫秒)
- `date_played`: 游戏日期
- `moves_count`: 移动次数
- `hints_used`: 使用提示次数

#### player_stats
- `player_name`: 玩家姓名(主键)
- `games_played`: 游戏次数
- `games_won`: 获胜次数
- `total_time`: 总游戏时间
- `best_time`: 最佳时间
- `average_time`: 平均时间
- `last_played`: 最后游戏时间

#### saved_games
- `id`: 主键，自增
- `player_name`: 玩家姓名
- `game_name`: 游戏保存名称
- `grid_data`: 当前网格状态
- `original_grid`: 原始谜题
- `difficulty`: 难度级别
- `elapsed_time`: 已用时间
- `date_saved`: 保存日期

## 测试说明

项目包含全面的JUnit 5测试套件：

### SudokuPuzzleTest
- 测试谜题初始化
- 验证移动有效性
- 测试谜题生成
- 验证求解算法
- 测试提示功能

### GameDAOTest
- 测试数据库CRUD操作
- 验证数据持久化
- 测试统计计算
- 验证数据完整性

### 运行测试
```bash
mvn test
```

## 故障排除

### 常见问题

#### 数据库连接错误
- 确保没有其他Derby实例在运行
- 检查文件权限
- 删除`sudokuDB`文件夹重新创建

#### 编译错误
- 确保使用Java 21
- 检查Maven依赖是否下载完成
- 清理并重新构建项目

#### 运行时错误
- 检查控制台错误信息
- 确保所有依赖都已正确安装
- 验证项目结构完整性

### 日志文件
应用程序会在控制台输出详细的日志信息，包括：
- 数据库初始化状态
- 错误详细信息
- 性能指标

## 项目贡献

本项目由单人开发，包含以下主要贡献：

### 架构设计 (25%)
- OOP设计和类层次结构
- 设计模式实现
- 数据库架构设计

### 核心功能开发 (40%)
- 数独算法实现
- 游戏逻辑编程
- 数据库集成

### 用户界面 (20%)
- Swing GUI设计
- 用户体验优化
- 事件处理实现

### 测试和文档 (15%)
- 单元测试编写
- 文档编写
- 代码注释和JavaDoc

## 许可证

本项目为教育目的开发，遵循学术诚信原则。

---

**开发者**: [您的姓名]  
**课程**: Programming Design and Construction (PDC)  
**日期**: 2024年11月  
**版本**: 1.0.0
