package configurator.xml;

import configurator.bean.annotation.Value;

import java.util.Arrays;
import java.util.HashMap;

public class Reporter {

    @Value(key = "areab", defaultValue = "1100")
    private int area;
    private int ranking;
    @Value(key = "china.phone")
    private int phone;
    @Value(key = "china.domain")
    private String domain;
    @Value(key = "china.leaders.leader")
    private String[] leaders;
    @Value(key = "china.leaders#count")
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

    @Value(key = "china.area#ranking")
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
