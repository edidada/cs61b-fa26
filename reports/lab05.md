# CS61B Fall 2026 Lab 05 实验报告

实验分支 `edidada/lab05`。Star 实现 Comparable，按质量用 Double.compare 排序；主程序对课程 CSV 输出最大质量恒星。WordComparators 分别按小写 x、指定字符、指定字符集合的出现次数升序比较；集合做快照，字符表中重复项不重复计数。WordFinder 用单次遍历求最大值，仅在严格更大时替换，因此并列保留最早元素；主程序对 Moby Dick 输出小写元音最多的单词。比较和扫描遵循 Java char 大小写语义。

开启原模板要求取消注释的三个 Star 测试，没有删除断言或降低条件；其余上游测试不改。Maven 和 Gradle 均使用 JDK17，独立 lab05.yml 运行两套构建，并用 Maven 真正执行两个读取课程数据的 main。报告 artifact 与最终 Actions 状态可用于复核。本地未编译，未提交校内平台。
