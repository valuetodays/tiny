#tiny-mybatis

java中有一项技术叫`反射`，各种框架的实现都使用了反射。就像Spring……等类似的框架。如果您有阅读框架源代码的经验，可能会注意到SpringMVC框架的最终目的是

    method.invoke(obj, args);

如上的obj`必需`得是一个具体的类或是代理类。

但是框架中也有特殊的，比如我才发现的一个orm框架mybatis，它却是拦截method.invoke()来进行自己独特的处理。

mybatis使用了诸多技术，如xxxxx。

而tiny-mybatis项目则是我个人阅读了mybatis-3.3.6源码（注：是部分源码）后根据自己的理解整的一个小项目，为的就是方便理解加深对mybatis的理解。
在此，我要感谢tiny-spring这个项目，是它让我对Spring的ioc多理解了一部分。为此，我也整个tiny-mybatis来。


### 1-jdk动态代理

业务中mybatis需要我们提供DAO层的接口类，但是不用提供实现类，但是我们依然可以使用

    UserDAO dao = sqlSession.getMapper(UserDAO.class);
    
来获取对应的接口类，其它这时候的dao对象并不是我们的UserDAO接口，而是一个代理类。（接口是无法进行调用其方法的）

动态代理的好处是可以进行切面编程(aop)，其实现方式有jdk动态代理和cglib动态代理。

要进行jdk动态代理，代理类(MethodProxyHandler)必需继承类`InvocationHandler`并重写其invoke方法，。
执行测试类，测试通过。

### 2-存储接口集合

我们先定义一个配置类Configuration，用以保存所用到的接口，不过它并不直接操作接口，而是把添加与删除的任务交给另一个类MapperRegistry来处理。MethodProxyHandler类的作用是代理接口类。所以当前三个类的关系是Configuration包含一个MapperRegistry，MapperRegistry中包含一个接口与MethodProxy的对应集合。

执行测试类，输出demoDAO是代理类，类名是com.sun.proxy.$Proxy2。接着输出demoDAO.hello()的返回值是null，因为我们在MethodProxyHandler中没有对其做任何处理而仅仅是返回了null。


### 3-添加接口对应配置文件

接下来添加DemoDAO对应的配置文件，用以处理上面的返回值问题。

为此我们添加一个类MapperFileRegistry，以保存接口类与其配置文件的对应关系。
MapperFileRegistry中包含namespace和sql代码片段。
MapperRegistry中包含了接口与其代理类的集合。MapperFileRegistry中包含了接口与其配置文件(MapperFile)的集合。
同时MethodProxyHandler的构造方法中添加MapperFile。


### 4-参数传递

sql语句中（一般）都会传递参数，如

    <select id="getById">
        select hello from t_hello where id=${id}
    </select>
    
此处我们先实现把${id}替换成具体的数值，暂不考虑使用#{id}换成?（即预编译）的情况。

最先应该解决的问题是把一个字符串中的${id}替换成具体的数据。mybatis中提供了一种解决办法，我们稍作调整就可以拿来用。请看`test.billy.jee.tinymybatis.parsing.PropertyParserTest.testParsing`来看一下。
我们修改了com.billy.jee.tinymybatis.MethodProxyHandler.invoke方法，让它调用execute方法，最终在execute方法中处理参数替换的问题

- 我们为了获取接口类的参数的名称，使用Param注解；
- 在execute方法中把接口中方法的参数名称和传递的值进行绑定处理到Map对象中；
- 根据调用的方法名和Mapper文件中的标签的id相匹配，找到具体的sql语句代码；
- 再处理sql语句（字符串）与参数集合，用以生成最终的sql语句；




