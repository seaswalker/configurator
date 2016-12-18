package conf;

/**
 * 需要获得{@link XMLConfSource}.
 *
 * @author skywalker
 */
public interface ConfAware {

    void setConf(XMLConfSource conf);

}
