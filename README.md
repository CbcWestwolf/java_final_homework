# 葫芦娃期末作业

##### 要求

- README.md
- 封装
- 继承
- 多态
- 设计原则
- 异常处理
- 集合类型+泛型
- 注解
- 线程安全
- 单元测试
- 说明战斗事宜（例如行进速度有多快，是不是每个生物体速度一样，死亡概率谁大谁小等）
- 最精彩的某一次战斗过程记录文件连同代码一并提交
- 用注释生成文档（Javadoc）



## 实现效果

1. 按下`空格键`之后，正方按`鹤翼`阵型出发；反方以`长蛇`阵型出发；
2. 游戏目标：
   - 击败所有敌人
3. 打斗开始前或者打斗结束后按下`L`可实现读取文件并回放
4. 回放和打斗过程中按下`P`可暂停


### 程序状态图如下

![状态图](E:\NutstoreFiles\NJU\JuniorFall\java程序设计\final_homework\状态图.jpg)

## 框架选择
采用与示例`huluwa`相似的GUI架构，即：一个继承了`JFrame`的类包含了一个继承了`JPanel`的类。

## 葫芦娃妖怪战力分析
[这里](https://www.zhihu.com/question/34581237?from=androidqq)分析了葫芦娃中妖怪的战斗能力，可见就战力来看：
**蝎子大王>>蛤蟆精>蛇精**，其中蝎子大王和蛇精应该实现为单例模式。

### 攻击安排
1. 蝎子精80；
2. 蛇精20；
3. 蛤蟆精（小马仔）50；
4. 葫芦娃50~100（随机生成）；
5. 爷爷10

### 攻击者优先
主动攻击时，对方下降的生命值=攻击者的power，但自己下降的生命值=对方的power的一半

### 血量

所有角色的血量都是100

## 继承——UML类图说明

抽象类*Creatures*有两个抽象子类*Good*和*Bad*，*Good*有子类**Grandpa**和**GourdDolls**，分别代表爷爷和葫芦娃；*Bad*有子类**ScorpionKing**、**SnakeQueen**和**Toad**，分别代表蝎子大王、蛇精和蝎子精（小马仔）。

![UNL类图](E:\NutstoreFiles\NJU\JuniorFall\java程序设计\final_homework\UNL类图.png)



## 封装

详情见javadoc文档



## 多态

`Ground`类中的3个`ArrayList`均使用泛型实现，体现了多态：

```java
private ArrayList<Good> goodCreatures = null;
private ArrayList<Bad> badCreatures = null;
private ArrayList<Creatures> deadCreatures = null; 
```



## 设计原则

### 单一职责原则

* `Main`类只负责建立`Ground`类和`BackEnd`类


* `Ground`类只负责游戏画面显示
* `BackEnd`类只负责游戏的逻辑
* `Creatures`类（包括其子类）只负责角色的定义
* `ConstantValue`接口只负责提供全局常量
* `FileFilterTest`类只负责过滤类型为".fight"的文件
* ​

### 里氏替换原则

所有的*Creatures*都能被*Good*或者*Bad*替换；所有的*Good*都能被**Grandpa**或者**GourdDolls**替换；所有的*Bad*都能被**ScorpionKing**、**SnakeQueen**或者**Toad**替换。



