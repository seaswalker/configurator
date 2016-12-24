# configurer
主要实现了以下两个功能:

## 配置注入

目前实现了对xml文件了属性文件(properties)的支持。

### xml注入

配置文件示例:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<china>
    <area ranking="3">960</area>
    <phone>86</phone>
    <domain>cn</domain>
    <leaders count="3">
        <leader>xi</leader>
        <leader>li</leader>
        <leader>hu</leader>
    </leaders>
</china>
```

java类:

```java
@Component
public class Reporter {
    @Value(key = "area")
    private int area;
    private int ranking;
    @Value(key = "phone")
    private int phone;
    @Value(key = "domain")
    private String domain;
    @Value(key = "leaders.leader")
    private String[] leaders;
    @Value(key = "leaders", attr = "count")
    private int leaderCount;
    
  	@Value(key = "*")
    public void setAll(HashMap<Object, String> all) {
        this.all = all;
    }
}
```

入口:

```java
@Test
public void xml() {
	ConfSource confSource = new XMLConfSource("etc/test.xml");
	Injecter injecter = new Injecter();
	BeanContainer beanContainer = injecter.enbaleConf().basePackage("xml").source(confSource).inject();
	Reporter reporter = beanContainer.get(Reporter.class);
	System.out.println(reporter);
}
```

### 属性文件注入

与XML注入类似。

## IOC

IOC利用javax.annotation.Resource实现，支持按bean name, bean type注入。

示例如下：

学生类:

```java
@Component(scope = Scope.PROTOTYPE)
public class Student {
    private String name;
    private int age;

    public Student() {
        this.name = "skywalker";
        this.age = 10;
    }
}
```

老师类:

```java
@Component
public class Teacher implements BeanContainerAware {
    @Resource
    private Student student;

    public void printStudent() {
        System.out.println(student);
    }

    @Override
    public void setBeanContainer(BeanContainer beanContainer) {
        System.out.println("setBeanContainer调用");
    }
}
```

入口:

```java
@Test
public void getByName() {
	Injecter injecter = new Injecter();
	BeanContainer container = injecter.basePackage("ioc").enableIoc().inject();
	Teacher teacher = (Teacher) container.get("teacher");
	teacher.printStudent();
}
```



