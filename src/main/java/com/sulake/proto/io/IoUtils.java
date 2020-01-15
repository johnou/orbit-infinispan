package com.sulake.proto.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public abstract class IoUtils {

    private static final String EMPTY = "";

    private static final int MAXIMUM_STREAM_LENGTH = 1024 * 1024 * 10; // 10 MB

    private IoUtils() {
    }

    public static String convertInputStreamToString(InputStream is) throws IOException {
        if (is == null) {
            return EMPTY;
        } else {
            final StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                final char[] buffer = new char[1024];
                int length;
                while (true) {
                    length = reader.read(buffer);
                    if (length < 0) {
                        break;
                    }
                    sb.append(buffer, 0, length);
                    if (sb.length() > MAXIMUM_STREAM_LENGTH) {
                        throw new IOException("InputStream is larger than the accepted maximum " + MAXIMUM_STREAM_LENGTH);
                    }
                }
            }
            return sb.toString();
        }
    }

    /**
     * Closes given {@link Closeable}, suppressing all exceptions.
     *
     * @param toClose {@code Closeable} to close, can be {@code null}
     */
    public static void silentlyClose(Closeable toClose) {
        if (toClose != null) {
            try {
                toClose.close();
            } catch (IOException ignored) {
                // ignore
            }
        }
    }
}
