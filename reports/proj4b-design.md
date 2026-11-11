# Project 4B：WordNet 技术设计（公开自学版）

规范：https://fa26.datastructur.es/proj4/proj4b/ 。相关行为以4C/4D规范为依据。
这是Codex辅助自学设计，非符合课堂独立完成要求的提交。官方私有Checkpoint概念题未访问/提交，设计文档未送Gradescope，不声称4B整体完成或获得课程分数。用户要求报告采用Markdown，因此本存档不冒充约4页PDF及独立图页的课堂交付。

## 1. Overview

为NGordnet新增WordNet下位词查询：单词按全部词义的synset节点可达性取词，多词求可达词集合交集；k=0时忽略年份，k>0时按给定闭区间内的原始出现次数选择最多k词，最终按Java大小写敏感字母序输出。
图的节点是synset ID；synset里的词仅用于输入索引和输出投影，不能把词当图节点。可达节点中的某个词还有其他意思时，不因此跳转它的其他synset。这避免引入错误的同义词和下位词。
本设计覆盖查询、文件索引及原HTTP/浏览器交互；不设计WordNet在线编辑、不做语义相似度或搜索引擎、不将正式课堂私有评分伪装为本地检查。多义词与重边按真实数据处理；使用自写图，不导入图算法库。

## 2. Data

| 文件 | 内容与提取 | 解析者与持久数据 |
|---|---|---|
| synsets_size*.txt | 最多按逗号分3段：整数ID、空格分隔词组、可能含逗号的释义；只索引前两段 | WordNet：ID到词列表、词到ID集合，图中登记节点 |
| hyponyms_size*.txt | 父ID以及多个直接下位ID；同一父ID可出现多行 | WordNet解析，DirectedGraph合并邻接集合、去重边 |
| word_history_*.csv | 制表分隔：词、年、原始次数、忽略的卷数 | NGramMap：Map<String,TimeSeries> |
| year_history.csv | 逗号分隔：年、语料总次数、其他字段忽略 | NGramMap：TimeSeries总次数；k排名不使用此分母 |

数据从官方Data页面下载，实际Drive档案名目前为sp26（Fall26页面复用）。依赖固定到官方library-fa26提交并校验各jar的Git blob，下载原始文件计算SHA256。只提交代码和报告，不提交约150MB语料。
构造期按行读取，避免readAllLines占用额外整文件内存。synset ID不要求从0连续编号；保留大小写和下划线，释义不参与查询。NGram次数用double存储以匹配4A接口、避免int溢出，所有查询返回副本。

## 3. Components

### DirectedGraph

私有Map<Integer,Set<Integer>>邻接表，addVertex(id)、addEdge(parent,child)、reachable(Collection<Integer>)。使用HashSet去重重复边；多源ArrayDeque BFS，入队时visited去重，避免环导致无限循环，避免深链递归栈溢出。每个查询有独立visited/队列，不改变图；返回新的可达ID集合，包含有效起点本身。

### WordNet

构造器WordNet(synsets,hyponyms)只解析一次。保存DirectedGraph、Map<Integer,List<String>> synsets、Map<String,Set<Integer>> meanings。
hyponyms(String)先查输入词对应所有ID，调用reachable，再将可达ID的词插入TreeSet并返回新List。
hyponyms(Collection<String>)对每个输入词调用上述查询，按词集合retainAll；空输入或任一词未知返回空列表，重复输入词不改变集合语义。交集不在ID层面进行，例如car和bug可通过不同节点共享beetle。

### NGramMap与TimeSeries

复用自写Project4A版本，NGramMap提供countHistory(word,start,end)；每次返回闭区间稀疏副本，缺失年份保持缺失，不补零。TimeSeries继承TreeMap，年份有序；NGramMap内索引只在启动构建，避免每个候选词重读文件。
4C的工厂与处理器只创建WordNet，不读取无关词频文件；4D再注入NGramMap，不使用全局静态实例，也不将其藏在WordNet语义解析器中。

### HyponymsHandler与AutograderBuddy

处理器继承NgordnetQueryHandler，负责查询编排和List.toString，不负责读取原始文件。
工厂getHyponymsHandler(wordHistoryFile,yearHistoryFile,synsetFile,hyponymFile)按传入路径创建依赖，不硬编码Main常量。4C忽略前两条路径，4D使用全部四条路径。
4D处理器保存WordNet和NGramMap引用；先计算多词交集候选集，再对每个候选词countHistory(...).values()求和。仅保留sum>0的词，按总次数降序、相同次数按词字母序确定性打破平局；截前min(k,候选数)项，再字母序输出。官方不要求固定平局次序，确定性规则更便于复现。
Main创建一次依赖、注册hyponyms，再startUp；原Javalin适配器解析words/startYear/endYear/k，提供默认1900/2020/0并将结果编码成JSON字符串。前端按JSON解码后放入textarea。

## 4. Request Flows

```mermaid
sequenceDiagram
    participant Factory as AutograderBuddy
    participant W as WordNet
    participant G as DirectedGraph
    participant N as NGramMap (4D)
    participant H as HyponymsHandler
    Factory->>W: construct(synsetFile,hyponymFile)
    W->>G: addVertex / addEdge, parse once
    opt Project 4D
        Factory->>N: construct(wordHistoryFile,yearHistoryFile)
    end
    Factory->>H: inject W [and N in 4D]
    Note over H: returned handler receives NgordnetQuery
    H->>W: hyponyms(words)
    loop each input word
        W->>G: reachable(all meaning IDs)
        G-->>W: reachable synset IDs, including starts
        Note over W: project IDs to words; intersect word sets
    end
    W-->>H: alphabetically sorted candidates
    alt k == 0
        H-->>Factory: List.toString (ignore both years)
    else k > 0, Project 4D
        loop each candidate
            H->>N: countHistory(word,startYear,endYear)
            N-->>H: sparse closed-range counts copy
        end
        Note over H: sum raw counts; discard zero; select top k; sort alphabetically
        H-->>Factory: selected List.toString
    end
```

单词k=0：查所有起点→BFS→投影→词去重排序。多词k=0：分别计算上述集合，再求交集；不能先求起点交集、也不能把已经返回的词重新作为输入递归扩张。
k>0：先得到完整交集，再取时间范围内的累计原始次数；不能分别对各输入词先截top k再求交集，否则会丢失真正公共候选；不能用加权词频排序，因为年度总量不同会改变排名。
无匹配输出[]。k大于正次数候选数时只输出已有候选，未在NGrams中出现的词不补齐。k=0仍返回有语义关系但没有词频记录的词。start>end自然产生空时间范围；负k不属于官方必测输入，处理器可选择明确拒绝。

### 可复现追踪案例

size16规范案例：change对应synset2与8，两起点可达词合并后为[alteration, change, demotion, increase, jump, leap, modification, saltation, transition, variation]；change与occurrence按词求交集后去掉demotion、variation，得到8词。来自另一个词义的flashback等不得混入。
不同节点同词案例：car→beetle(汽车义节点)，bug→beetle(昆虫义节点)，节点交集为空但词交集为[beetle]。
真实82191节点规范案例：video与recording返回[video, video_recording, videocassette, videotape]。
4D计划验收：EECS数据CS61A、2010–2020、k=4返回[CS170, CS61A, CS61B, CS61C]；完整数据food/cake、1950–1990、k=5返回[cake, cookie, kiss, snap, wafer]。这些正k结果在4B阶段是预期案例，不能标记成已执行。

## 5. Complexity

设V为synset数、E为去重边数、P为synset中总词条出现数、C为NGrams记录数、Y为一个词的年份数、q为查询词数、R为交集候选数。
图/词索引启动期预期O(V+E+P)时间及空间（忽略文本字符扫描成本）；4D词频按TreeMap插入期望O(C log Y)时间、O(C)存储。文件和临时行占用流式常量额外内存，不整文件复制。
单词k=0：可达部分Vr/Er的BFS O(Vr+Er)，收集Pr个词到TreeSet O(Pr log Pr)，临时O(Vr+Pr)。
q词k=0：最坏O(q(V+E+P log P))；交集retainAll使用有序集合和二分成员查找的成本可包含在qP log P中；临时峰值O(V+P)，不保存q份完整可达缓存。
k>0：上述语义查询外，加上R次区间复制/求和约O(R(log Y+Yrange))，全部候选排序O(R log R)，最后k项排序O(k log k)，临时O(R+Yrange)。将来候选非常多时可换容量k堆降低选择成本，但本设计优先易核验的全排序。
服务共享的索引构造后只读，单请求临时数据互相独立；不缓存所有词对的交集，也不声称共享runner测时能证明渐近界限。

## 6. Verification and Remaining Work

按用户要求，每个项目独立分支和YAML。本4B工作流只归档设计Markdown，成功表示设计文档已上传，不能代替人工评审或Checkpoint通过。
4C计划/实施使用原始2项不改测试，额外Floyd-Warshall随机图独立oracle、跨义陷阱、重边/非连续ID/深链/环、公开大数据示例、HTTP和原前端按钮；结果以4C最终HEAD Actions为准。
4D另加独立分支/YAML与正k计数oracle、缺失词频/年份边界/交集后排名/大数据案例回归；本设计文档不提前声称这些通过。
私有Checkpoint概念题、Gradescope设计评阅、私有代码grader和官方style检查仍待用户有相应访问权限时验收。保存本设计是跨机器学习复现材料，不向课堂系统提交AI生成文档。
