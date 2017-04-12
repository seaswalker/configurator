package configurator.properties;

import configurator.bean.annotation.Component;
import configurator.bean.annotation.Value;

import java.util.Map;

@Component
public class DB {

    @Value(key = "db.username")
    private String username;
    @Value(key = "db.password")
    private int password;
    @Value(key = "db.name")
    private String dbName;
    @Value(key = "db.*")
    private Map<String, String> all;

    @Override
    public String toString() {
        return "DB{" +
                "username='" + username + '\'' +
                ", password=" + password +
                ", dbName='" + dbName + '\'' +
                ", all=" + all +
                '}';
    }

}
