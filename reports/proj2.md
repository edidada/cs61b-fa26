# CS61B Fall 2026 Project 2：ArrayDeque61B

要求：https://fa26.datastructur.es/projects/proj2/ ，核查日期2026-11-11。
基于官方b428f862f54f2a52b8aa48285a0b018e6bc21ac2；独立edidada/proj2与proj2.yml。
原ArrayDeque61BTest为空，保持不改。新增5项Codex补充测试，不宣称私有grader通过。

使用初始8格循环数组，head指向首元素；满时倍增。按讲义在删除前，容量至少16且
使用率不超过25%时减半；移除槽清null。两端操作除扩缩容外常数时间，均摊O(1)，
get/size/isEmpty为O(1)，resize与toList为O(n)。只在toList使用ArrayList，内部不调用toList。
按任务12让接口扩展Iterable；迭代按逻辑顺序，越界next抛异常。equals逐元素比较
任意Deque61B，toString直接遍历，hashCode与元素相等一致。迭代期间修改未定义，
不声称线程安全或fail-fast。getRecursive依题抛UnsupportedOperationException。

测试覆盖空/非法下标、两端多轮增长缩小到空后重用、50000步独立List差分、
列表拷贝/迭代/equals/toString及独立反射检查初始容量、收缩和释放引用。
反射检查不作为课程接口覆盖测试。Maven/Gradle在Actions各执行，本地只静态检查。
未执行课程要求的逐方法先红后绿教学过程；私人Gradescope覆盖标志和评分未核实。
需最终SHA的成功CI及XML才能登记公开功能验证，不能等同整门课验收。
