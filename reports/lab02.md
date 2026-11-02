# CS61B Fall 2026 Lab 02 实验报告

上游：[官方 skeleton](https://github.com/Berkeley-CS61B/skeleton-fa26)，实验分支 `edidada/lab02`。

## 实现

完成 Cat 的状态、比较、静态总重和输出；evens 创建新列表，doubleAll 原地更新；countWords 使用哈希表计数。Bomb.java 与全部上游测试保持不变，只在 BombMain 填入三个输入。根据 Java String.hashCode、固定种子的 Random 和 Fisher-Yates 洗牌推导：phase0=39291226，phase1=[0,9,3,0,8]，phase2=793227803。本次采用源码分析和确定性计算，没有声称执行交互式 IDE 调试。

## 构建和验证

JDK 17；Maven 多模块优先，并同时提供 Gradle。algs4 来自官方 library-fa26 的固定 Git blob `e1703efb7a09c244fdfb0ee3d08aa7c28e1b408c`，下载后验证 Git blob 哈希。先运行 `python scripts/setup_dependencies.py`，然后 `mvn -B -pl lab02 test` 或 `gradle :lab02:test`。

独立 workflow：`.github/workflows/lab02.yml`，两个构建工具均执行原始测试。测试输出作为 artifact 保留。没有在本地编译；最终通过状态以本分支对应 Actions 运行及报告为准。不包含校内 Gradescope 提交。
