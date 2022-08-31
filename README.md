### taskflow
---
>**有向无环图（DAG）**：
>
>1）定义：在图论中，如果一个有向图无法从某个顶点出发经过若干条边回到该点，则这个图是一个[有向无环图（DAG图）](https://baike.baidu.com/item/%E6%9C%89%E5%90%91%E6%97%A0%E7%8E%AF%E5%9B%BE/10972513)
>
>2）有向无环图具有完整严密的拓扑性质，使其具有很强的流程表达能力；通过有向无环图，可以解决两个问题：从逻辑上，对各个节点的依赖关系进行了组织；从技术上，有依赖关系的节点需要等待执行，无依赖关系的可以并发执行
>
>   ![vf1pxP.png](https://s1.ax1x.com/2022/08/29/vfYJvn.png)

---

#### 一、项目介绍
1. **框架简介**

    taskflow是一款轻量、简单易用、可灵活扩展的通用任务编排框架，基于有向无环图(DAG)的方式实现，框架提供了组件复用、同步/异步编排、条件判断、分支选择等能力，可以根据不同的业务场景对任意的业务流程进行编排

2. **使用DAG模型的优势**
   * 任务模块化

        对于DAG任务模型，任务之间没有很强的相关性，每个任务模块职责单一，根据输入进行相应的处理，然后输出相应的结果，可复用性极强
   * 易于调整

        基于已有的DAG模型，如果想要调整编排流程，往往只需要修改个别任务即可，可以通过修改图结构或者个别任务的具体实现，即可完成调整
   * 结构清晰

        将业务的处理流程与具体实现进行解耦，根据流程的定义就可以快速的了解整个系统的概况以及包含哪些模块

3. **开发语言**

   JDK8+
   
4. **核心能力**

    ![vf33Of.png](https://s1.ax1x.com/2022/08/29/vfY9HK.png)
5. **项目目录**
   * taskflow-core: taskflow 引擎核心能力
   * taskflow-example: taskflow 接入实例，提供测试用例

6. **名词解释**

    ![vf83C9.png](https://s1.ax1x.com/2022/08/29/vf8r8A.png)
   * **Operator**：以下简称OP或组件，OP是DAG图中具体的节点，如上图1、2、3等节点；实现IOperator接口并开发相应的业务逻辑就可以完成一个OP的定义
   * **OperatorWrapper**：以下简称wrapper，OP对应的包装类，在wrapper中可以定义节点的名称、节点与节点之间的关系、节点参数的来源等；引入wrapper后可以将OP进行解耦，根据不同的业务场景需要对OP进行组合使用时，通过wrapper描述OP之间的依赖关系，串联成一个编排流程
   * **DagEngine**：DAG执行引擎，根据指定的初始节点（如上图的1、2、3），执行相应的编排流程；DAG执行引擎在初始化时可以指定使用不同的线程池，对业务进行隔离；可以设置整个执行过程的超时时间，达到超时时间阈值时，会结束编排流程的执行，没有执行到的节点不再执行同时执行中的节点也会被中断
   * **强依赖**：节点之间默认的依赖关系，只有前面的节点执行结束后才可以执行后续的节点。如上图中 1、2节点执行完才可以执行4；3执行完才可以执行5
   * **弱依赖**：在示例图中以虚线表示，不同于强依赖的执行逻辑，只要节点依赖的其它节点中有一个执行结束就可以执行当前节点，如上图中，如果3执行完时，4节点还没有执行完，此时依然可以执行5节点

#### 二、目标与收益
1. **通用能力封装**

   * 将项目中常用的功能模块封装成OP组件，这些组件都是通用的，可以在不同的项目中直接使用
   * 框架层面统一的日志上报、降级限流、ABtest策略等
2. **降低开发维护难度**

   * 开发人员只需要实现具体的OP，根据业务逻辑，定义好OP之间的依赖关系（执行流程），不需要编写相对复杂的多线程代码
   * 基于编排框架的项目代码风格统一、业务处理流程比较清晰，根据编排流程的定义就可以快速了解整个服务的概况以及包含哪些模块
   * 代码的可扩展性好，比如要实现具体某个业务需求时，开发相应的OP然后以插件的形式集成到项目中就可以，不会对其它模块产生影响
3. **平台化能力建设**

   * 可视化，在页面上可以直观的展示业务的具体处理流程，也可以拖拽的方式对现有流程进行扩展
   * 配置化，基于封装的通用OP，可以快速以低代码的方式实现新业务流程的接入

#### 三、常见的编排场景

##### 1. 串行请求
![vf83C9.png](https://s1.ax1x.com/2022/08/31/v4QAeO.png)

1、2、3依次串行执行
##### 2. 并行请求
![vf83C9.png](https://s1.ax1x.com/2022/08/31/v4lPhj.png)

1、2、3并行执行
##### 3. 串并行相互依赖
![vf83C9.png](https://s1.ax1x.com/2022/08/31/v4Qgh9.png)

1执行完后，2、3再并行执行 
##### 4. 弱依赖
![vf83C9.png](https://s1.ax1x.com/2022/08/31/v4QRpR.png)

1、2、3中任意一个执行完后，就可以执行4 
##### 5. 准入条件判断
![vf83C9.png](https://s1.ax1x.com/2022/08/31/v4Qf6x.png)

4弱依赖1、2、3节点，每个节点在执行完后都可以执行4的准入条件，判断当前是否已经满足执行节点4的条件，若满足则直接执行
##### 6. 分支选择
![vf83C9.png](https://s1.ax1x.com/2022/08/29/vfJsnP.png)

根据节点的执行结果选择要执行的子节点，如上图所示，最终的执行路径可能是：1->3->6->9 
##### 7. 复杂场景
![vf83C9.png](https://s1.ax1x.com/2022/08/29/vfJUte.png)

流程复杂，没有严格的串行、并行过程 

#### 四、引擎执行逻辑
>DAG图中的节点通过入度(indegree)来表示依赖的节点个数，只有当节点的入度为0时，当前节点才可以执行

![vf83C9.png](https://s1.ax1x.com/2022/08/31/v4l0Cd.png)

>DAG图中的弱依赖不计入节点的入度，如下图中的节点4初始入度等于0，实际的执行流程可能存在三种情况
* 1 -> (2、3）-> 4 -> 5
* 1 -> 2 -> 4 -> 5
* 1 -> 3 -> 4 -> 5

![vf83C9.png](https://s1.ax1x.com/2022/08/30/v49put.png)
#### 五、快速开始
[快速开始](./QuickStart.md)