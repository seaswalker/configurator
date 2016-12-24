package xml;

import bean.Component;
import bean.Value;

import java.util.Arrays;
import java.util.HashMap;

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
    private HashMap<Object, String> all;

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public int getRanking() {
        return ranking;
    }

    @Value(key = "area", attr = "ranking")
    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Value(key = "*")
    public void setAll(HashMap<Object, String> all) {
        this.all = all;
    }

    @Override
    public String toString() {
        return "Reporter{" +
                "area=" + area +
                ", ranking=" + ranking +
                ", phone=" + phone +
                ", domain='" + domain + '\'' +
                ", leaders=" + Arrays.toString(leaders) +
                ", leaderCount=" + leaderCount +
                ", all=" + all +
                '}';
    }
}
