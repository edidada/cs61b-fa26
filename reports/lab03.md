# CS61B Fall 2026 Lab 03 实验报告

分支 `edidada/lab03`，基于官方 2026 秋季 skeleton，保留原测试。

## 实现与复杂度

IntList 的 replaceID 迭代原地替换，replaceRD 递归原地替换，replaceRND 递归复制并替换，输入为 null 时返回 null。三者时间 O(n)，迭代额外空间 O(1)，递归版本使用 O(n) 调用栈；复制版本新建链表，避免别名污染。stairify 只插入缺失的中间值，保留原有节点和值；用 long 计算差值以避免 int 溢出。

SLList.insert 从哨兵前驱开始遍历，在头部、中间或超过尾部的位置插入一个节点并更新 size；空表同样适用。遍历时间 O(min(position,n))，插入额外空间 O(1)。

## 验证

JDK17，`python scripts/setup_dependencies.py` 后执行 `mvn -B -pl lab03 test` 或 `gradle :lab03:test`。独立 workflow `lab03.yml` 对两个构建工具运行上游 IntListTest 和 SLListTest，输出测试报告 artifact。本地未编译，未向 Gradescope 提交。最终结论以对应分支 Actions 为准。
