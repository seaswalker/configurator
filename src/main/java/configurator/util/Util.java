package configurator.util;

/**
 * 工具类.
 *
 * @author skywalker
 */
public class Util {

    private Util() {
    }

    /**
     * 判断给定的一组字符串中是否含有空字符串(null或"").
     *
     * @return true, 如果有
     */
    public static boolean isEmpty(String... strings) {
        for (String string : strings) {
            if (string == null || "".equals(string.trim())) {
                return true;
            }
        }
        return false;
    }

}
