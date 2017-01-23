# configurer
主要实现了以下两个功能:

## 配置注入

目前支持xml、属性文件(properties)和json.

如果没有显式指定key，那么:

- 对于Field，取其字段名
- 对于Method，如果是setter方法，取其set的属性名，比如setIp，那么取ip作为key

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
    
  	@Value(key = "area", attr = "ranking")
    public void setRanking(int ranking) {
        this.ranking = ranking;
    }
  
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
	Source source = new XMLSource("etc/test.xml");
	Injecter injecter = new Injecter();
	BeanContainer beanContainer = injecter.enbaleConf().basePackage("xml").source(source).inject();
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

## json

配置文件:

```json
{
  "ip": "192.168.0.235",
  "port": 8080,
  "version": 1.0,
  "wait_ack": true,
  "interval": {
    "enabled": false,
    "unit": "s",
    "value": 10
  },
  "msisdns": ["1368392", "12334123"],
  "loc_ids": [22343, 34211, 2333]
}
```

Java类:

```java
@Component
public class JsonHolder {
    private String ip;
    @Value
    private int port;
    @Value
    private double version;
    @Value(key = "wait_ack")
    private boolean waitACK;
    @Value(key = "interval.enabled")
    private boolean intervalEnabled;
    @Value(key = "interval.unit")
    private String intervalUnit;
    @Value(key = "interval.value")
    private int intervalValue;
    @Value
    private String[] msisdns;
    @Value(key = "loc_ids")
    private String[] locIds;

    @Value
    public void setIp(String ip) {
        this.ip = ip;
    }
}
```

入口:

```java
@Test
public void test() throws LoadException {
	Source source = new JsonSource("etc/conf.json");
	Injecter injecter = new Injecter();
	BeanContainer beanContainer = injecter.enbaleConf().basePackage("xml").source(source).inject();
	JsonHolder holder = beanContainer.get(JsonHolder.class);
	System.out.println(holder);
}
```





