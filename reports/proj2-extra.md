# CS61B Project 2 Extra：声音合成

官方规范：https://fa26.datastructur.es/projects/proj2_extra/
Starter：Berkeley-CS61B/skeleton-fa26，b428f862f54f2a52b8aa48285a0b018e6bc21ac2。

复用 Project 2 自己实现的 ArrayDeque61B（55acd184），接口继承 Iterable。
GuitarString 固定 44100 Hz、0.996 衰减，按 round(SR/frequency) 初始化零环形缓冲；pluck 每格独立白噪声，tic 计算相邻均值并衰减。
五键 asdfg 对应 C4/D4/E4/G4/A4，各弦持续更新并相加，因此允许叠音。
ToneMatrix 的 16 行使用从 C3 开始的五声音阶，底行最低；每列 6000 个采样，16 列循环。鼠标仅按下边沿切换，窗口仅点击或游标移动时重绘。

## 验证与边界

不在本地编译或运行实验。独立 proj2-extra.yml 使用 JDK25、Maven 和 Gradle 两条 GitHub Actions 路径。
官方 GuitarStringTest 4 项保持不变；补充 4 项检查多频率 20000 步数组递推对照、键盘叠音、鼠标/游标/音阶以及 50000 采样单声道 16bit PCM WAV。
固定官方 algs4 Git blob 校验，防止依赖漂移。音频文件上传 Actions artifact。

当前 CI 结果须以对应提交的 Actions 为准。数值及 WAV 验证不能证明声卡播放、真实键盘鼠标窗口体验或听感；这些人工验收仍待完成。官方没有 autograder；可选多音序器、其他乐器没有实现。
这是 Codex 辅助的公开自学实现，不声称个人独立课堂提交。
