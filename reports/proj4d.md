# CS61B Project 4D：按原始词频选择下位词

规范：https://fa26.datastructur.es/proj4/proj4d/ 。基于独立4C分支7404bf0，自写图和4A词频索引。公开自学、Codex辅助，不向课堂系统提交，不声称符合独立作业要求或获得私有评分。

## 实现

先对WordNet可达词求交集，k=0返回全部候选且忽略年份；k>0对候选词在闭区间内的原始次数求和，过滤零和缺失词频，再按累计次数降序截取最多k项，最后按字母序输出。并列次数按词名确定性选择，官方允许任意平局顺序。负k明确拒绝。
词频采用double匹配4A接口，可准确保存当前数据的超int整数；不使用归一化权重选词。工厂通过构造参数注入数据源，首次正k请求同步懒加载NGramMap一次；之后只读复用，k=0不需要词频路径存在。不使用全局静态NGramMap。

## 验证

独立proj4d.yml，JDK25 Maven/Gradle，保留4C原2项和补充4项，另补3项：交集后排名/原始次数与权重不同的反例/零次数/年份边界/一次加载、200组long矩阵独立排名oracle（包含30亿次数）、真实EECS及完整WordNet food/cake规范案例。
真实Javalin HTTP与Chromium原始Hyponyms按钮同时覆盖k=0与k=5，无本地实验。固定官方38个jar逐blob校验，下载语料不提交Git，记录哈希。runner已有系统库，浏览器安装不重复执行apt以避开镜像DNS故障；实际Chromium启动和按钮断言仍必须通过。
本分支包含继承的4C工作流文件，只有proj4d.yml订阅edidada/proj4d。最终以当前HEAD远程结论和artifact为准。

## 未完成与范围

官方Gradescope私有grader和style checker未执行。4B私有概念题和人工设计评阅未完成。已进一步接入4A History/HistoryText和可选Hypohist/HypohistText五条路由，新增文本按钮。Hypohist复用同一排名选择函数再委托历史处理器，避免解析列表字符串；浏览器验收全部按钮、所选五词标签和图像解码，保存截图。Main一次创建图和词频索引并共享，工厂单独使用时仍懒加载。复现脚本scripts/fetch_proj4c.sh、scripts/verify_proj4d_http.sh、scripts/verify_proj4d_browser.cjs均在仓库，无新增Python文件。
