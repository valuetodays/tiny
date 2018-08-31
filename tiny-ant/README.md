### tiny-ant

生命在于折腾。

该项目由Jakarta Ant 1.1（现已更名为Apache Ant）简化而来，目的是https://my.oschina.net/valuetodays/blog/758972。

它的核心配置文件是build.xml，根节点是project，可以有id、name、default属性，default指待运行的目标(即配置文件中的Target)的名称。大致看了一下代码，发现ant做的事如下：

- 解析build.xml文件 （javax.xml.parsers.SAXParserFactory）
- 排序依赖目标 （使用拓扑排序）
- 按顺序依次运行相关目标 （运行）

#### 解析build.xml文件

解析build.xml文件即是把文件中的各节点转换成对应的类，即Project类。

#### 排序依赖目标

每个目标都有零个或多个依赖目标，而依赖目标又会有多个依赖目标，有点拗口，但事实如此。举个例子，A依赖B，B依赖C和D，那么执行结果就是C-D-B-A或D-C-B-A了。得到CDBA或DCBA的过程就得用到算法了，ant
用的是拓扑排序，各位可百度了解。

#### 运行目标

运行目标即执行目标里的各个任务(Task)，可以自定义任务，也可使用预定任务，为精简项目，只保留了如下几个任务：

- echo
- mkdir
- property
- touch

2018-02-06
 