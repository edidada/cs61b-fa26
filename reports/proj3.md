# CS61B Fall 2026 Project 3：Percolation

要求：https://fa26.datastructur.es/projects/proj3/ ，2026-11-11核查。
基于官方b428f862f54f2a52b8aa48285a0b018e6bc21ac2；独立edidada/proj3与proj3.yml。
使用官方WeightedQuickUnionUF，两个并查集分别跟踪渗流与顶部连通。
前者有虚拟顶/底，后者只连顶，防止渗流后孤立底部格点被倒灌误判full。
仅开放格点连四邻居，重复open不增加计数，N<=0拒绝，坐标越界抛IndexOutOfBounds。
构造O(N²)空间/时间，每操作常数次并查集调用；计数O(1)，不自写UF替代要求。

保留原PercolationTest与官方PercolationStats，不修改测试或统计公式。
新增4项补充测试，独立BFS洪泛oracle核对N=1–12多轮每步的全部格点，
覆盖重复open、计数、full、渗流、单格、倒灌、边界及已提供统计类单格性质。
Maven/Gradle在Actions运行并保存XML，本地不编译/模拟。
私人grader/style评分、交互GUI、可选阈值统计研究和运行时间分析尚未完成；
通过功能测试不能替代这些项目。最终提交CI成功后才登记公开功能验证。
