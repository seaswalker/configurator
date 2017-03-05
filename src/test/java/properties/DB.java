package properties;

import bean.annotation.Component;
import bean.annotation.Value;

@Component
public class DB {

    @Value(key = "db.username")
    private String username;
    @Value(key = "db.password")
    private int password;
    @Value(key = "db.name")
    private String dbName;

    @Override
    public String toString() {
        return "DB{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", dbName='" + dbName + '\'' +
                '}';
    }

}
