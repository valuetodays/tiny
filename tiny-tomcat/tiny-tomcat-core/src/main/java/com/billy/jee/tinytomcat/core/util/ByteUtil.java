package com.billy.jee.tinytomcat.core.util;

/**
 * @author liulei@bshf360.com
 * @since 2018-04-25 15:00
 */
public class ByteUtil {
    private ByteUtil() {}

    public static int byteArrayToInt(byte[] b) {
        AssertUtil.checkNull(b);
        AssertUtil.checkEqual(b.length, 4);
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }
}
