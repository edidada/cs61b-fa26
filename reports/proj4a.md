# CS61B Project 4A：NGrams

规范：https://fa26.datastructur.es/proj4/proj4a/
数据入口：https://fa26.datastructur.es/proj4/proj4-data/
Starter：Berkeley-CS61B/skeleton-fa26，b428f862f54f2a52b8aa48285a0b018e6bc21ac2。

## 实现

TimeSeries 继承 TreeMap，无实例变量；有序 years/data、闭区间复制、稀疏加法和按年除法均不修改输入，缺少分母年份抛 IllegalArgumentException。
NGramMap 按行读取制表分隔词频和逗号分隔年度总数，索引为 Map<String,TimeSeries>；返回防御性复制，未知词/空范围返回空序列，不补虚构零点。总数用double避免int溢出。
HistoryTextHandler 按输入词顺序输出加权历史，每行（含最后一行）带换行。
HistoryHandler 生成真实加权词频图；重复标签合并绘图，缺失词不伪造数据。Main 接入两个正式处理器，使用14377词文件。

## 复现与验收范围

独立 edidada/proj4a、proj4a.yml，JDK25，Maven/Gradle两条路径。只在Actions编译运行；本地仅编辑及静态检查。
38个官方运行依赖按library-fa26固定提交7d9a380f逐文件Git blob校验；Maven system依赖只用于本模块随项目下载的固定jar，非发布到中央仓库的库。
官方Fall26页面当前数据下载仍指向名为sp26的Drive包149_H-aFsKjVa0IwX4-mnMnJc82G465SG；遵循官方链接并记录实际SHA256，不将文件名误称新生成的Fall26数据。下载数据不提交Git，遵守.gitignore。
原始 TimeSeries 2项、NGramMap 3项测试不改；原HistoryTextHandlerTest整类注释，另在补充测试中执行其示例并注明来源，不计作原执行用例。
补充4项覆盖100轮稀疏算术、不变性与复制、随机文件独立矩阵oracle、文本格式、真实14377词加载<60秒和800x600 PNG。另启动原Javalin服务器，通过HTTP验证静态页、精确文本JSON和真实图表PNG；图像与数据哈希上传artifact。

以最终HEAD Actions结论及台账证据为准。私有Gradescope/style checker未运行，不声称课程评分或全部CS61B完成。本实现为Codex辅助公开自学，不作为要求独立完成的课堂作业提交。

## 首轮发现与修正

0e9128a 的run43031902080两构建JUnit均通过，但HTTP探针错把3词小文件的固定词频当成14377词文件的预期，导致接口验收失败（不是服务器启动失败）。修正探针：直接流式读取实际14377词原始CSV及年度总数，独立构造精确文本期望，不调用NGramMap/TimeSeries；同时保存原始CSV期望值。未改变原始5个执行测试及原注释测试。最终结果需核验修正后的SHA。
