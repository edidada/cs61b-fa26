# CS61B Project 4C：WordNet k=0

规范：https://fa26.datastructur.es/proj4/proj4c/ 。这是Codex辅助公开自学实现，不作为要求独立完成的课堂提交。

## 实现与边界

DirectedGraph 是自写邻接表，重复边去重，迭代多源BFS，visited防止环及深链递归溢出。
WordNet 只解析一次文件；synset行以最多3段分割保留含逗号定义，词与多个synset映射，支持不连续ID。边为父synset到直接下位synset，同一父ID多行合并。
查询先从输入词的所有词义节点遍历，再投影成词，去重并按Java大小写敏感自然序排序。多词查询对词集合求交集，不能对节点求交集，也不能通过已到达词重新跳转其他词义。
HyponymsHandler只负责查询与List.toString格式，AutograderBuddy不加载k=0无关的NGrams文件。Main注册真实hyponyms路由并载入82191节点文件。
复用自写4A NGramMap/TimeSeries，替换原staff包装器，避免依赖staff二进制；本次k=0不调用它们。4D的正k词频排名尚未实现，4C按阶段要求忽略k与年份。

## 验证计划与证据范围

独立edidada/proj4c与proj4c.yml，JDK25 Maven/Gradle，Actions运行，本地只编辑静态检查。
原始单词和多词测试各1项保持不变；补充4项：20000深链和环、不同节点同词交集/禁止跨义跳转/大小写/无词频文件/复制、40个随机图Floyd-Warshall独立oracle、真实82191节点官方示例及删除源文件后查询证明只解析一次。
原Javalin服务HTTP验证4组请求，Chromium实际点击原HTML Hyponyms按钮（多词、k=0忽略年份、未知词、无页面脚本错误）并保存截图。
38个运行jar来自固定官方library-fa26提交，逐Git blob校验。数据仅远程下载并保存SHA256，不提交Git；Fall26官方链接实际名为sp26压缩包，按来源如实记录。
最终验收以对应HEAD Actions与课程台账为准；私有Gradescope/style checker未运行，不声称官方分数。初始Hello占位交互教学阶段没有单独运行，直接实现最终处理器并测试。

## 复杂度

设V/E为synset/边数，P为总词条数。启动期哈希索引期望O(V+E+P)时间和空间。单词查询遍历可达节点和边O(Vr+Er)，将Wr个结果词插入TreeSet需要O(Wr log Wr)；查询临时空间O(Vr+Wr)。q词查询执行q次遍历并依次求词交集，最坏O(q(V+E+P log P))，峰值临时空间O(V+P)。单词结果返回新List，不暴露内部集合。
