# CS61B Fall 2026 Lab 07 实验报告

分支 `edidada/lab07`，完成五组定时实验和 AList 几何扩容。提供无 GUI 的可复现入口，保存原始 CSV 与 PNG 图，避免 headless Actions 无法弹窗；将模板的 Java25 IO.println 和实例 main 改为 JDK17 对应写法。

理论：sp16fp9p1 为 Theta(N^2)，sp16fp9p2 为 Theta(N log N)，msf 为 2T(N/2)+Theta(N)=Theta(N log N)，sp16fp9p5 的幂次循环总内层次数为几何级数，因此 Theta(N^2)。AList 原来的逐项扩容令构建为 Theta(N^2)，倍增容量后构建摊还 Theta(N)、单次 append 摊还 O(1)。

每个规模先预热三次，计时至少约 50ms（最多十万次），用 volatile sink 保留计算结果。实验数据受 JVM/JIT、GC、runner 负载和恒定开销影响，不能以单点时间证明复杂度，也不设置脆弱的绝对速度断言。测试检查十万元素跨多次扩容后顺序、逆序删除和大小。JDK17 的 Maven、Gradle 两套测试由独立 lab07.yml 运行，Maven 另执行实际测量，CSV/PNG 和测试报告上传 artifact。本地没有编译或伪造运行数据。
