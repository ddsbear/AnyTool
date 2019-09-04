package com.utils.library.interfaces;

import com.utils.library.permission.Consumer;

/**
 * {@link Consumer} with {@link ThrowableConsumer}
 * <p>
 * Created by Oasis on 2016/11/24.
 */
public interface ThrowableConsumer<T, E extends Throwable> {
    void accept(T t) throws E;
}