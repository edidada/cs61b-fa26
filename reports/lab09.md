# CS61B Fall 2026 Lab 09 实验报告

分支 `edidada/lab09`。BFS 在发现节点时设置距离并入队，记录出队顺序；DFS 用显式栈保存邻接迭代器，实现与递归一致的先序/后序且避免深图调用栈溢出。两者遵循图的邻居插入顺序。Dijkstra 使用提供的 indexed binary heap 优先队列，完成松弛、changePriority、最短距离和路径重建；不可达查询按接口抛异常，负权边拒绝，距离加法检查 int 溢出。

BFS、DFS 时间 O(V+E)，空间 O(V)；Dijkstra 时间 O((V+E)logV)。返回的遍历顺序和路径均为副本。提供的 ExtrinsicMinPQ 不改写。

保留原始测试并移植 Java25 IO/实例 main 到 JDK17。Javalin 使用官方课程对应的 7.2.3（class major 61，兼容 JDK17）。独立 lab09.yml 运行 Maven/Gradle 原测试；Maven job 另外启动真实可视化服务，读取首页、BFS JSON 和最短路径 JSON，检查 Berkeley→Boston=3575 和 Seattle 路径，保存日志和 JSON artifact。只操作本 job 自己的 localhost 进程，结束后关闭。本地没有编译，最终结论以本分支 Actions 为准。
