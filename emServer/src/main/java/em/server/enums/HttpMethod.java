package em.server.enums;

public enum HttpMethod {
    OPTIONS,
    GET,
    HEAD,
    POST,
    PUT,
    PATCH,
    DELETE,
    TRACE,
    CONNECT,
    ANY;

    public static final String rageExpMethods = getRegExpString();

    private static String getRegExpString() {
        String result = "";
        HttpMethod[] values = HttpMethod.values();

        for(int index = 0; index < values.length; index++) {
            result += index == values.length - 1 ? values[index].toString() : values[index].toString() + "|";
        }

        return result;
    }
}
