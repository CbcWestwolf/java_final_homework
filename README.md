# 葫芦娃期末作业

**陈博钏 151220007**



## 关于jar文件的打开

使用`mvn clean test package`构建后，`target`目录中的`Huluwa.jar`是可以直接打开的。如果出现双击打开之后界面过大，最下方的说明栏被遮住，请用`java -jar Huluwa.jar`命令打开。



## 项目目录文件说明

* javadoc文件夹：存放javadoc文档
* default.fight：存放一次精彩的战斗
* UML类图：程序中角色类图的继承关系
* 状态图：程序状态的定义与转换




## 实现效果

1. 按下`空格键`之后，正方按`鹤翼`阵型出发；反方以`长蛇`阵型出发；
2. 打斗开始前或者打斗结束后按下`L`可实现读取文件并回放；
3. 回放和打斗过程中按下`P`可暂停；
4. 游戏目标：击败所有敌人




### 程序状态图

![状态图](E:\NutstoreFiles\NJU\JuniorFall\java程序设计\final_homework\状态图.jpg)



## 框架选择

采用与示例`huluwa`相似的GUI架构，即：一个继承了`JFrame`的类包含了一个继承了`JPanel`的类。

具体说明如下

* `Main`类：程序的入口，继承了`JFrame`类；
* `Ground`类：游戏绘图类，继承了`JPanel`类；
* `BackEnd`类：游戏逻辑类；
* `creatures`包
  * `Creatures`类：抽象类，定义了角色的共同属性
  * `good`包
    * `Good`类：抽象类，定义了正方角色的基本属性
    * `Grandpa`类：定义了爷爷的属性
    * `GourdDolls`类：定义了葫芦娃的属性
  * `bad`包
    * `Bad`类：抽象类，定义了反方角色的基本属性
    * `ScorpionKing`类：定义了蝎子大王的基本属性
    * `SnakeQueen`类：定义了蛇精的基本属性
    * `Toad`类：定义了小马仔（蛤蟆精）的基本属性
* `tools`包
  * `ConstantValue`接口：定义了游戏中基本的常量
  * `FileFilterTest`类：一个用来筛选`.fight`文件格式的类
  * `FileOperation`类：文件操作类，用于读取存档和写入存档
  * `GourddollsName`：葫芦娃名字的枚举类型
  * `Status`：游戏状态的枚举类型



## 封装与继承——UML类图说明

抽象类*Creatures*有两个抽象子类*Good*和*Bad*，*Good*有子类**Grandpa**和**GourdDolls**，分别代表爷爷和葫芦娃；*Bad*有子类**ScorpionKing**、**SnakeQueen**和**Toad**，分别代表蝎子大王、蛇精和蝎子精（小马仔）。

![UML类图](E:\NutstoreFiles\NJU\JuniorFall\java程序设计\final_homework\UML类图.png)



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
* `FileOperation`类只负责文件读写

### 里氏替换原则

所有的*Creatures*都能被*Good*或者*Bad*替换；所有的*Good*都能被**Grandpa**或者**GourdDolls**替换；所有的*Bad*都能被**ScorpionKing**、**SnakeQueen**或者**Toad**替换。

### 开放封闭原则

`Grandpa`与`GourdDolls`同为`Good`的子类，但是之所以不直接用`Good`类，是因为`GourdDolls`类还有一个独有的域`id`以标志不同葫芦娃，这体现了**一个类应该对扩展开放**的原则。



## 集合与泛型

程序中用到的集合是`ArrayList`，其中主要是`ArrayList<Good>`、`ArrayList<Bad>`和`ArrayList<Creatures>`，分别用于存储正方(`GoodCreatures`)、反方(`BacCreatures`)与死亡的正反方(`DeadCreatures`)。

之所以用集合而不是数组，是因为每个时钟周期的检查中，会将`GoodCreatures`和`BadCreatures`中已经死亡的角色移动到`DeadCreatures`中，而数组无法添加删除元素。



## 注解

用`@author`和`@see`等注解编写了javadoc，详情请见javadoc文档



## 输入输出

`FileOperation`类中采用的输入输出类有

* File：用于获取文件
* `FileReader`与`BufferedReader`：用于读取文件
* `FileWriter`与`BufferedWriter`：用户写入文件



## 线程安全

* `BackEnd`类中的线程安全
  * `check()`方法使用`synchronized`修饰，因为该方法需要随时检查`GoodCreatures`、`BacCreatures`和`DeadCreatures`，如果被打断，将会出现线程不安全的情况。
  * `replaying()`方法使用`synchronized`修饰，因为该方法实现了对角色位置、图片的设置，一旦被打断，可能出现角色位置或图片不一致的情况。
* `FileOperation`类中的线程安全：`writeFile()`方法使用`synchronized`修饰，因为该方法涉及写文件。





## 单元测试

* 对角色的测试以`Grandpa`为代表，有3个测试方法`testString`、`testDead`、`testLocation`和`testImage()`，分别测试`Grandpa`的`toString()`、`isDead()`、`setBlood()`、`getBlood()`、`getX()`、`getY()`、`setX()`、`setY()`、`getPower()`、`getImage()`、`setImage()`等方法的正确性。由于其它角色的设置与`Grandpa`具有相似性，便不需再进行单元测试；
* 由于`BackEnd`、`Ground`和`Main`等与GUI相关，所以不进行单元测试。



## 战斗设定

[这里](https://www.zhihu.com/question/34581237?from=androidqq)分析了葫芦娃中妖怪的战斗能力，可见就战力来看：
**蝎子大王>>蛤蟆精>蛇精**。

**除了攻击力不同，所有角色的其它属性都是相同的。**

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

****



## 最精彩的战斗

存放在项目路径的`default.fight`中：葫芦娃大获全胜，爷爷运筹帷幄！
