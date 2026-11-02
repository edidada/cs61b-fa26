# CS61B Fall 2026 Lab 06 实验报告

分支 `edidada/lab06`，实现加权 quick-union 与完整路径压缩。数组中根保存负集合大小，非根保存父索引；union 将小树接到大树，大小相同时严格将第一个参数的根接到第二个参数的根。已连接或相同元素的合并不改变连通关系；find 压缩整条访问路径。parent 只读取，不隐式压缩。索引越界抛 IllegalArgumentException，构造器拒绝负元素数。

初始化 O(n)，额外空间 O(n)，加权合并与路径压缩的单操作摊还时间 O(alpha(n))。独立 lab06.yml 使用 JDK17 对 Maven、Gradle 执行原始 UnionFindTest，检查初始状态、非法输入、同集合合并、权重和并列规则、完整压缩及多轮所有方法组合。依赖固定官方 algs4 blob。本地不编译；最终执行结论见该分支 Actions 与测试报告 artifact。
