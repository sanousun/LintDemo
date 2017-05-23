package com.maihaoche.lint;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created with Android Studio.
 * User: dashu
 * Date: 2017/5/18
 * Time: 上午11:03
 * Desc:
 */

public class IOUtils {
    public static void closeQuietly(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException var2) {
        }

    }

    public static void closeQuietly(OutputStream output) {
        try {
            if (output != null) {
                output.close();
            }
        } catch (IOException var2) {
        }

    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        return count > 2147483647L ? -1 : (int) count;
    }

    private static long copyLarge(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0L;

        int n1;
        for (boolean n = false; -1 != (n1 = input.read(buffer)); count += (long) n1) {
            output.write(buffer, 0, n1);
        }

        return count;
    }
}
