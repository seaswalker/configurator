# 意义

一个可以从属性文件、XML和JSON中读取并根据注解自动注入的工具。

为什么存在?

- Spring只支持属性文件

- Apache commons configuration不支持注解

这个小工具柔和了两者的优点。🤪

# 配置注入

从指定的配置文件中读取并将配置注入到类的字段及setter方法上，支持XML，属性文件，json格式，支持将多个配置文件组合为一个虚拟配置。

假如有以下XML:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<china>
    <area ranking="3">960</area>
    <phone>86</phone>
</china>
```

以及如下的Java类定义:

```java
@Component
public class Reporter {
    @Value(key = "china.phone")
    private int phone;
  
    @Value(key = "china.area#ranking")
    public void setRanking(int ranking) {
        this.ranking = ranking;
    }
}
```

便可以以如下的方式进行注入:

```java
@Test
public void xml() {
    Source source = new XMLSource("etc/configurator.xml");
    Injecter injector = new Injecter();
    BeanContainer beanContainer = injector.basePackage("configurator.xml").source(source).inject();
    Reporter reporter = beanContainer.get(Reporter.class);
    System.out.println(reporter);
}
```

# IOC

IOC利用javax.annotation.Resource实现，支持的特性如下:

- @Component组件注册
- 类型转换器TypeConverter自定义
- 按照名称、类型注入
- 初始化方法(@Init)定义及优先级
- 构造器、初始化方法参数解析、注入





