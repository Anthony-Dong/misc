package com.chat.core.util;


import java.util.*;

/**
 * 字符串分割, 字符串连接 , 字符串分割不适用于正则表达式分割
 * Java提供的 StringTokenizer 也类似
 * 本节的方法引用自 Commons-long-3
 */
public final class StringUtils {
    private StringUtils() {
    }

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private static final String EMPTY_STRING = "";

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }


    /**
     * join string.
     *
     * @param array String array.
     * @return String.
     */
    public static String join(String[] array) {
        if (array==null||array.length==0) {
            return EMPTY_STRING;
        }
        StringBuilder sb = new StringBuilder();
        for (String s : array) {
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * 字符串拼接
     */
    public static String joinStr(String sign, String... strArr) {
        Optional<String> optional = Arrays.stream(strArr).filter(Objects::nonNull
        ).reduce((a, b) -> a + sign + b);
        return optional.orElse(EMPTY_STRING);
    }


    /**
     * char 类型 , 是否保留空.默认保留.
     */
    public static String[] split(final String str, final char delim) {
        return split(str, delim, false);
    }


    /**
     * 这个是commons-long3 包中的. 引用于大多数用 char进行分割的数据
     *
     * @param preserveAllTokens true 则 a,,b,c 以","为分隔符 , 结果是 "a" "" "b" "c"
     *                          false 则  a,,b,c   为 "a" "b" "c"
     */
    public static String[] split(final String str, final char separatorChar, final boolean preserveAllTokens) {
        // Performance tuned for 2.0 (JDK1.4)
        if (str == null) {
            return EMPTY_STRING_ARRAY;
        }
        final int len = str.length();
        if (len == 0) {
            return EMPTY_STRING_ARRAY;
        }
        final List<String> list = new ArrayList<>();
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match || preserveAllTokens) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }
                start = ++i;
                continue;
            }
            lastMatch = false;
            match = true;
            i++;
        }
        if (match || preserveAllTokens && lastMatch) {
            list.add(str.substring(start, i));
        }
        return list.toArray(EMPTY_STRING_ARRAY);
    }


    /**
     * 分割字符串方法.  引用自 Commons long 3
     */
    public static String[] split(final String str, final String separatorChars) {
        return split(str, separatorChars, str.length(), true);
    }

    /**
     * 翻个字符串方法.  引用自 Commons long 3
     */
    public static String[] split(final String str, final String separatorChars, final int max) {
        return split(str, separatorChars, max, true);
    }

    /**
     * 分割字符串
     */
    public static String[] split(final String str, final String separatorChars, final int max, final boolean preserveAllTokens) {
        // Performance tuned for 2.0 (JDK1.4)
        // Direct code is quicker than StringTokenizer.
        // Also, StringTokenizer uses isSpace() not isWhitespace()

        if (str == null) {
            return null;
        }
        final int len = str.length();
        if (len == 0) {
            return EMPTY_STRING_ARRAY;
        }
        final List<String> list = new ArrayList<>();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            // Null separator means use whitespace
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // Optimise 1 character case
            final char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else {
            // standard case
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (match || preserveAllTokens && lastMatch) {
            list.add(str.substring(start, i));
        }
        return list.toArray(EMPTY_STRING_ARRAY);
    }

}
