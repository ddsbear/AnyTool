package com.dds.http.io;


import androidx.annotation.Nullable;

import java.io.Closeable;
import java.io.InputStream;

/** Simple static methods to perform common IO operations. */
public final class IoUtils {

	public static void closeQuietly(final @Nullable Closeable closeable) {
		if (closeable == null) return;
		try {
			closeable.close();
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception ignored) {}
	}

	public static void closeQuietly(final @Nullable InputStream stream) {
		if (stream == null) return;
		try {
			stream.close();
		} catch (final RuntimeException e) {
			throw e;
		} catch (final Exception ignored) {}
	}
}
