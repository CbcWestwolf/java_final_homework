# 葫芦娃期末作业

采用与示例`huluwa`一样的GUI架构，即：`nju.java.GameBourd`继承`JFrame`、`nju.java.GameBourd`继承`JPanel`.



`M*N`的二维数组的类型是`Things`，它有两种子类：

1. 空地设置为`Empty`；
2. 生命体设置为`Creature`。

在`Ground`类中，有一个`M*N`的`things`数组。如果`things[i][j] instanceof Empty`,说明改地方是空地；否则为相应生命体。