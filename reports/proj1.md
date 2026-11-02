# CS61B Fall 2026 Project 1 实验报告

分支 `edidada/proj1`，实现 Deque61B 的全部接口。永久循环双向哨兵统一空表、首尾和单节点操作；add/remove/getFirst/getLast/size/isEmpty 都为 O(1)，get 从更近一侧迭代，getRecursive 按要求递归，越界返回 null。toList 创建独立快照。内部自行维护节点，没有用 JDK 链表代替实现。

保留上游基础测试，补充固定种子 61 的五千轮随机操作，和 JDK LinkedList 独立对照每步的值、顺序、大小、空状态、两端查看及迭代/递归索引；另验证空表复用、快照不受后续修改影响和五万次首尾操作。递归索引天然受 JVM 栈深限制，不把它声称为无限深安全。

原测试引用的 jh61b 取自官方固定 library-fa26 commit，Git blob `c3e9cbc43b3edd244eb8119ad0444a70b600ae0f`，验证后安装本地 Maven 仓库；class major55 可在 JDK17 使用。Maven/Gradle 由独立 proj1.yml 运行并保存报告。本地未编译，最终验收以本分支 Actions 为准。
