# CS61B Fall 2026 Lab 10 实验报告

分支 `edidada/lab10`，实现 equals/hashCode、哈希分布判定与带 infinity 哨兵的 Dijkstra。

SimpleOomage 的每通道为 0..255 的 5 的倍数，转换成 0..51 后按 52 进制编码，覆盖 52^3 个值且无碰撞；穷举全部 140608 种颜色验证。bucket 用 floorMod 处理负哈希，以 N/50 和 N/2.5 判定范围。ComplexOomage 原 base256 算法在 int 中 256^4 溢出，五字节序列的前缀丢失；用 256 个不同前缀+四个零构造反例，改为 Java List 的 base31 hash 后验证分布。

模板 `TestJankyOomage` 的注释明确写着 **should fail**，其对象只产生 0/1 两个哈希值。保留其原始 true 断言，不假装它应通过：正常测试组单独排除此演示类，随后独立运行它并解析 JUnit XML，必须恰有一个断言失败、零执行错误，否则 CI 失败。Maven 和 Gradle 都执行这个预期红灯检查；这是题目指定的反例，不是掩盖回归。模板要求开启的 Simple/Complex 测试已启用。

Dijkstra 保留全部节点预置 infinity 的框架，取出 infinity 时结束，避免无穷距离加权溢出并错误连接不连通分量；有限距离采用检查加法。提供图遍历和不可达测试保留。Java25 IO/实例 main 移植为 JDK17。独立 lab10.yml 保留正常和预期失败的测试报告 artifact。本地没有编译，最终 Actions 状态才是实际验收依据。
