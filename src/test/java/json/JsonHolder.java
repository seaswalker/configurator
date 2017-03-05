package json;

import bean.annotation.Component;
import bean.annotation.Value;

import java.util.Arrays;

/**
 * conf.json载体.
 *
 * @author skywalker
 */
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

    @Override
    public String toString() {
        return "JsonHolder{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", version=" + version +
                ", waitACK=" + waitACK +
                ", intervalEnabled=" + intervalEnabled +
                ", intervalUnit='" + intervalUnit + '\'' +
                ", intervalValue=" + intervalValue +
                ", msisdns=" + Arrays.toString(msisdns) +
                ", locIds=" + Arrays.toString(locIds) +
                '}';
    }

}
