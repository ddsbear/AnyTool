package com.utils.library.hack;


import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.utils.library.BuildConfig;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;


/**
 * Java reflection helper optimized for hacking non-public APIs.
 * The core design philosophy behind is compile-time consistency enforcement.
 * <p>
 * It's suggested to declare all hacks in a centralized point, typically as static fields in a class.
 * Then call it during application initialization, thus they are verified all together in an early stage.
 * If any assertion failed, a fall-back strategy is suggested.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Hack {

    public static Class<?> ANY_TYPE = $.class;

    private static class $ {
    }

    private static final boolean LAZY_RESOLVE = !BuildConfig.DEBUG;    // Lazy in production if fallback is provided, to reduce initialization cost.

    public static class AssertionException extends Throwable {

        private Class<?> mClass;
        private Field mHackedField;
        private Method mHackedMethod;
        private String mHackedFieldName;
        private @Nullable
        String mHackedMethodName;
        private Class<?>[] mParamTypes;

        AssertionException(final String e) {
            super(e);
        }

        AssertionException(final Exception e) {
            super(e);
        }

        @Override
        public String toString() {
            return getCause() != null ? getClass().getName() + ": " + getCause() : super.toString();
        }

        public String getDebugInfo() {
            final StringBuilder info = new StringBuilder(getCause() != null ? getCause().toString() : super.toString());
            final Throwable cause = getCause();
            if (cause instanceof NoSuchMethodException) {
                info.append(" Potential candidates:");
                final int initial_length = info.length();
                final String name = getHackedMethodName();
                if (name != null) {
                    for (final Method method : getHackedClass().getDeclaredMethods())
                        if (method.getName().equals(name))            // Exact name match
                            info.append(' ').append(method);
                    if (info.length() == initial_length)
                        for (final Method method : getHackedClass().getDeclaredMethods())
                            if (method.getName().startsWith(name))    // Name prefix match
                                info.append(' ').append(method);
                    if (info.length() == initial_length)
                        for (final Method method : getHackedClass().getDeclaredMethods())
                            if (!method.getName().startsWith("-"))    // Dump all but generated methods
                                info.append(' ').append(method);
                } else
                    for (final Constructor<?> constructor : getHackedClass().getDeclaredConstructors())
                        info.append(' ').append(constructor);
            } else if (cause instanceof NoSuchFieldException) {
                info.append(" Potential candidates:");
                final int initial_length = info.length();
                final String name = getHackedFieldName();
                final Field[] fields = getHackedClass().getDeclaredFields();
                for (final Field field : fields)
                    if (field.getName().equals(name))                // Exact name match
                        info.append(' ').append(field);
                if (info.length() == initial_length) for (final Field field : fields)
                    if (field.getName().startsWith(name))            // Name prefix match
                        info.append(' ').append(field);
                if (info.length() == initial_length) for (final Field field : fields)
                    if (!field.getName().startsWith("$"))            // Dump all but generated fields
                        info.append(' ').append(field);
            }
            return info.toString();
        }

        public Class<?> getHackedClass() {
            return mClass;
        }

        AssertionException setHackedClass(final Class<?> hacked_class) {
            mClass = hacked_class;
            return this;
        }

        public Method getHackedMethod() {
            return mHackedMethod;
        }

        AssertionException setHackedMethod(final Method method) {
            mHackedMethod = method;
            return this;
        }

        @CheckResult
        public String getHackedMethodName() {
            return mHackedMethodName;
        }

        @SuppressWarnings("UnusedReturnValue")
        AssertionException setHackedMethodName(final String method) {
            mHackedMethodName = method;
            return this;
        }

        public Class<?>[] getParamTypes() {
            return mParamTypes;
        }

        AssertionException setParamTypes(final Class<?>[] param_types) {
            mParamTypes = param_types;
            return this;
        }

        public Field getHackedField() {
            return mHackedField;
        }

        AssertionException setHackedField(final Field field) {
            mHackedField = field;
            return this;
        }

        public String getHackedFieldName() {
            return mHackedFieldName;
        }

        AssertionException setHackedFieldName(final String field) {
            mHackedFieldName = field;
            return this;
        }

        private static final long serialVersionUID = 1L;
    }

    /**
     * Placeholder for unchecked exception
     */
    public class Unchecked extends RuntimeException {
    }

    /**
     * Use {@link Hack#setAssertionFailureHandler(AssertionFailureHandler) } to set the global handler
     */
    public interface AssertionFailureHandler {
        void onAssertionFailure(AssertionException failure);
    }

    public static class FieldToHack<C> {

        protected @Nullable
        <T> Field findField(final @Nullable Class<T> type) {
            if (mClass == ANY_TYPE)
                return null;        // AnyType as a internal indicator for class not found.
            Field field = null;
            try {
                field = mClass.getDeclaredField(mName);
                if (Modifier.isStatic(mModifiers) != Modifier.isStatic(field.getModifiers())) {
                    fail(new AssertionException(field + (Modifier.isStatic(mModifiers) ? " is not static" : " is static")).setHackedFieldName(mName));
                    field = null;
                } else if (mModifiers > 0 && (field.getModifiers() & mModifiers) != mModifiers) {
                    fail(new AssertionException(field + " does not match modifiers: " + mModifiers).setHackedFieldName(mName));
                    field = null;
                } else if (!field.isAccessible()) field.setAccessible(true);
            } catch (final NoSuchFieldException e) {
                final AssertionException hae = new AssertionException(e);
                hae.setHackedClass(mClass);
                hae.setHackedFieldName(mName);
                fail(hae);
            }

            if (type != null && field != null && !type.isAssignableFrom(field.getType()))
                fail(new AssertionException(new ClassCastException(field + " is not of type " + type)).setHackedField(field));
            return field;
        }

        /**
         * @param modifiers the modifiers this field must have
         */
        protected FieldToHack(final Class<C> clazz, final String name, final int modifiers) {
            mClass = clazz;
            mName = name;
            mModifiers = modifiers;
        }

        protected final Class<C> mClass;
        protected final String mName;
        protected final int mModifiers;
    }

    public static class MemberFieldToHack<C> extends FieldToHack<C> {

        /**
         * Assert the field type.
         */
        public @Nullable
        <T> HackedField<C, T> ofType(final Class<T> type) {
            return ofType(type, false, null);
        }

        public @Nullable
        <T> HackedField<C, T> ofType(final String type_name) {
            try { //noinspection unchecked
                return ofType((Class<T>) Class.forName(type_name, false, mClass.getClassLoader()));
            } catch (final ClassNotFoundException e) {
                fail(new AssertionException(e));
                return null;
            }
        }

        public @NonNull
        HackedField<C, Byte> fallbackTo(final byte value) { //noinspection ConstantConditions
            return ofType(byte.class, true, value);
        }

        public @NonNull
        HackedField<C, Character> fallbackTo(final char value) { //noinspection ConstantConditions
            return ofType(char.class, true, value);
        }

        public @NonNull
        HackedField<C, Short> fallbackTo(final short value) { //noinspection ConstantConditions
            return ofType(short.class, true, value);
        }

        public @NonNull
        HackedField<C, Integer> fallbackTo(final int value) { //noinspection ConstantConditions
            return ofType(int.class, true, value);
        }

        public @NonNull
        HackedField<C, Long> fallbackTo(final long value) { //noinspection ConstantConditions
            return ofType(long.class, true, value);
        }

        public @NonNull
        HackedField<C, Boolean> fallbackTo(final boolean value) { //noinspection ConstantConditions
            return ofType(boolean.class, true, value);
        }

        public @NonNull
        HackedField<C, Float> fallbackTo(final float value) { //noinspection ConstantConditions
            return ofType(float.class, true, value);
        }

        public @NonNull
        HackedField<C, Double> fallbackTo(final double value) { //noinspection ConstantConditions
            return ofType(double.class, true, value);
        }

        /**
         * Fallback to the given value if this field is unavailable at runtime
         */
        public @NonNull
        <T> HackedField<C, T> fallbackTo(final @Nullable T value) {
            @SuppressWarnings("unchecked") final Class<T> type = value == null ? null : (Class<T>) value.getClass();
            //noinspection ConstantConditions
            return ofType(type, true, value);
        }

        private <T> HackedField<C, T> ofType(final Class<T> type, final boolean fallback, final @Nullable T fallback_value) {
            if (LAZY_RESOLVE && fallback) return new LazyHackedField<>(this, type, fallback_value);
            final Field field = findField(type);
            return field != null ? new HackedFieldImpl<C, T>(field) : fallback ? new FallbackField<C, T>(type, fallback_value) : null;
        }

        /**
         * @param modifiers the modifiers this field must have
         */
        private MemberFieldToHack(final Class<C> clazz, final String name, final int modifiers) {
            super(clazz, name, modifiers);
        }
    }

    public static class StaticFieldToHack<C> extends FieldToHack<C> {

        /**
         * Assert the field type.
         */
        public @Nullable
        <T> HackedTargetField<T> ofType(final Class<T> type) {
            return ofType(type, false, null);
        }

        public @Nullable
        <T> HackedTargetField<T> ofType(final String type_name) {
            try { //noinspection unchecked
                return ofType((Class<T>) Class.forName(type_name, false, mClass.getClassLoader()));
            } catch (final ClassNotFoundException e) {
                fail(new AssertionException(e));
                return null;
            }
        }

        /**
         * Fallback to the given value if this field is unavailable at runtime
         */
        public @NonNull
        <T> HackedTargetField<T> fallbackTo(final @Nullable T value) {
            @SuppressWarnings("unchecked") final Class<T> type = value == null ? null : (Class<T>) value.getClass();
            //noinspection ConstantConditions
            return ofType(type, true, value);
        }

        private <T> HackedTargetField<T> ofType(final Class<T> type, final boolean fallback, final @Nullable T fallback_value) {
            if (LAZY_RESOLVE && fallback) return new LazyHackedField<>(this, type, fallback_value);
            final Field field = findField(type);
            return field != null ? new HackedFieldImpl<C, T>(field).onTarget(null) : fallback ? new FallbackField<C, T>(type, fallback_value) : null;
        }

        /**
         * @param modifiers the modifiers this field must have
         */
        private StaticFieldToHack(final Class<C> clazz, final String name, final int modifiers) {
            super(clazz, name, modifiers);
        }
    }

    public interface HackedField<C, T> {
        T get(C instance);

        void set(C instance, @Nullable T value);

        HackedTargetField<T> on(C target);

        Class<T> getType();

        boolean isAbsent();
    }

    public interface HackedTargetField<T> {
        T get();

        void set(T value);

        Class<T> getType();

        boolean isAbsent();
    }

    private static class HackedFieldImpl<C, T> implements HackedField<C, T> {

        @Override
        public HackedTargetFieldImpl<T> on(final C target) {
            return onTarget(target);
        }

        private HackedTargetFieldImpl<T> onTarget(final @Nullable C target) {
            return new HackedTargetFieldImpl<>(mField, target);
        }

        /**
         * Get current value of this field
         */
        @Override
        public T get(final C instance) {
            try {
                @SuppressWarnings("unchecked") final T value = (T) mField.get(instance);
                return value;
            } catch (final IllegalAccessException e) {
                return null;
            }    // Should never happen
        }

        /**
         * Set value of this field
         *
         * <p>No type enforced here since most type mismatch can be easily tested and exposed early.</p>
         */
        @Override
        public void set(final C instance, final @Nullable T value) {
            try {
                mField.set(instance, value);
            } catch (final IllegalAccessException ignored) {
            }    // Should never happen
        }

        @Override
        @SuppressWarnings("unchecked")
        public @Nullable
        Class<T> getType() {
            return (Class<T>) mField.getType();
        }

        @Override
        public boolean isAbsent() {
            return false;
        }

        HackedFieldImpl(final @NonNull Field field) {
            mField = field;
        }

        public @Nullable
        Field getField() {
            return mField;
        }

        private final @NonNull
        Field mField;
    }

    private static class FallbackField<C, T> implements HackedField<C, T>, HackedTargetField<T> {

        @Override
        public T get(final C instance) {
            return mValue;
        }

        @Override
        public void set(final C instance, final @Nullable T value) {
        }

        @Override
        public T get() {
            return mValue;
        }

        @Override
        public void set(final T value) {
        }

        @Override
        public HackedTargetField<T> on(final C target) {
            return this;
        }

        @Override
        public Class<T> getType() {
            return mType;
        }

        @Override
        public boolean isAbsent() {
            return true;
        }

        FallbackField(final Class<T> type, final @Nullable T value) {
            mType = type;
            mValue = value;
        }

        private final Class<T> mType;
        private final T mValue;
    }

    private static class LazyHackedField<C, T> implements HackedField<C, T>, HackedTargetField<T> {

        @Override
        public T get(final C instance) {
            return delegate.get().get(instance);
        }

        @Override
        public void set(final C instance, final @Nullable T value) {
            delegate.get().set(instance, value);
        }

        @Override
        public HackedTargetField<T> on(final C target) {
            return delegate.get().on(target);
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        public T get() {
            return delegate.get().get(null);
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        public void set(final T value) {
            delegate.get().set(null, value);
        }

        @Override
        public Class<T> getType() {
            return delegate.get().getType();
        }

        @Override
        public boolean isAbsent() {
            return delegate.get().isAbsent();
        }

        LazyHackedField(final FieldToHack<C> field, final Class<T> type, final @Nullable T fallback_value) {
            mField = field;
            mType = type;
            mFallbackValue = fallback_value;
        }

        private final FieldToHack<C> mField;
        private final Class<T> mType;
        private final T mFallbackValue;

        private final Supplier<HackedField<C, T>> delegate = Suppliers.memoize(new Supplier<HackedField<C, T>>() {
            @Override
            public HackedField<C, T> get() {
                final Field field = LazyHackedField.this.mField.findField(mType);
                return field != null ? new HackedFieldImpl<C, T>(field) : new FallbackField<C, T>(mType, mFallbackValue);
            }
        });
    }

    public static class HackedTargetFieldImpl<T> implements HackedTargetField<T> {

        @Override
        public T get() {
            try {
                @SuppressWarnings("unchecked") final T value = (T) mField.get(mInstance);
                return value;
            } catch (final IllegalAccessException e) {
                return null;
            }    // Should never happen
        }

        @Override
        public void set(final T value) {
            try {
                mField.set(mInstance, value);
            } catch (final IllegalAccessException ignored) {
            }            // Should never happen
        }

        @Override
        @SuppressWarnings("unchecked")
        public @Nullable
        Class<T> getType() {
            return (Class<T>) mField.getType();
        }

        @Override
        public boolean isAbsent() {
            return false;
        }

        HackedTargetFieldImpl(final Field field, final @Nullable Object instance) {
            mField = field;
            mInstance = instance;
        }

        private final Field mField;
        private final Object mInstance;        // Instance type is already checked
    }

    public interface HackedInvokable<R, C, T1 extends Throwable, T2 extends Throwable, T3 extends Throwable> {
        @CheckResult
        <TT1 extends Throwable> HackedInvokable<R, C, TT1, T2, T3> throwing(Class<TT1> type);

        @CheckResult
        <TT1 extends Throwable, TT2 extends Throwable> HackedInvokable<R, C, TT1, TT2, T3> throwing(Class<TT1> type1, Class<TT2> type2);

        @CheckResult
        <TT1 extends Throwable, TT2 extends Throwable, TT3 extends Throwable> HackedInvokable<R, C, TT1, TT2, TT3> throwing(Class<TT1> type1, Class<TT2> type2, Class<TT3> type3);

        @Nullable
        HackedMethod0<R, C, T1, T2, T3> withoutParams();

        @Nullable
        <A1> HackedMethod1<R, C, T1, T2, T3, A1> withParam(Class<A1> type);

        @Nullable
        <A1, A2> HackedMethod2<R, C, T1, T2, T3, A1, A2> withParams(Class<A1> type1, Class<A2> type2);

        @Nullable
        <A1, A2, A3> HackedMethod3<R, C, T1, T2, T3, A1, A2, A3> withParams(Class<A1> type1, Class<A2> type2, Class<A3> type3);

        @Nullable
        <A1, A2, A3, A4> HackedMethod4<R, C, T1, T2, T3, A1, A2, A3, A4> withParams(Class<A1> type1, Class<A2> type2, Class<A3> type3, Class<A4> type4);

        @Nullable
        <A1, A2, A3, A4, A5> HackedMethod5<R, C, T1, T2, T3, A1, A2, A3, A4, A5> withParams(Class<A1> type1, final Class<A2> type2, final Class<A3> type3, final Class<A4> type4, final Class<A5> type5);

        @Nullable
        HackedMethodN<R, C, T1, T2, T3> withParams(Class<?>... types);
    }

    public interface NonNullHackedInvokable<R, C, T1 extends Throwable, T2 extends Throwable, T3 extends Throwable>
            extends HackedInvokable<R, C, T1, T2, T3> {
        @CheckResult
        <TT1 extends Throwable> NonNullHackedInvokable<R, C, TT1, T2, T3> throwing(Class<TT1> type);

        @CheckResult
        <TT1 extends Throwable, TT2 extends Throwable> NonNullHackedInvokable<R, C, TT1, TT2, T3> throwing(Class<TT1> type1, Class<TT2> type2);

        @CheckResult
        <TT1 extends Throwable, TT2 extends Throwable, TT3 extends Throwable> NonNullHackedInvokable<R, C, TT1, TT2, TT3> throwing(Class<TT1> type1, Class<TT2> type2, Class<TT3> type3);

        @NonNull
        HackedMethod0<R, C, T1, T2, T3> withoutParams();

        @NonNull
        <A1> HackedMethod1<R, C, T1, T2, T3, A1> withParam(Class<A1> type);

        @NonNull
        <A1, A2> HackedMethod2<R, C, T1, T2, T3, A1, A2> withParams(Class<A1> type1, Class<A2> type2);

        @NonNull
        <A1, A2, A3> HackedMethod3<R, C, T1, T2, T3, A1, A2, A3> withParams(Class<A1> type1, Class<A2> type2, Class<A3> type3);

        @NonNull
        <A1, A2, A3, A4> HackedMethod4<R, C, T1, T2, T3, A1, A2, A3, A4> withParams(Class<A1> type1, Class<A2> type2, Class<A3> type3, Class<A4> type4);

        @NonNull
        <A1, A2, A3, A4, A5> HackedMethod5<R, C, T1, T2, T3, A1, A2, A3, A4, A5> withParams(Class<A1> type1, final Class<A2> type2, final Class<A3> type3, final Class<A4> type4, final Class<A5> type5);

        @NonNull
        HackedMethodN<R, C, T1, T2, T3> withParams(Class<?>... types);
    }

    public interface HackedMethod<R, C, T1 extends Throwable, T2 extends Throwable, T3 extends Throwable>
            extends HackedInvokable<R, C, T1, T2, T3> {
        /**
         * Optional
         */
        @CheckResult
        <RR> HackedMethod<RR, C, T1, T2, T3> returning(Class<RR> type);

        /**
         * Fallback to the given value if this field is unavailable at runtime. (Optional)
         */
        @CheckResult
        NonNullHackedMethod<R, C, T1, T2, T3> fallbackReturning(@Nullable R return_value);

        @CheckResult
        <TT1 extends Throwable> HackedMethod<R, C, TT1, T2, T3> throwing(Class<TT1> type);

        @CheckResult
        <TT1 extends Throwable, TT2 extends Throwable> HackedMethod<R, C, TT1, TT2, T3> throwing(Class<TT1> type1, Class<TT2> type2);

        @CheckResult
        <TT1 extends Throwable, TT2 extends Throwable, TT3 extends Throwable> HackedMethod<R, C, TT1, TT2, TT3> throwing(Class<TT1> type1, Class<TT2> type2, Class<TT3> type3);

        @CheckResult
        HackedMethod<R, C, Exception, T2, T3> throwing(Class<?>... types);
    }

    @SuppressWarnings("NullableProblems")    // Force to NonNull
    public interface NonNullHackedMethod<R, C, T1 extends Throwable, T2 extends Throwable, T3 extends Throwable>
            extends HackedMethod<R, C, T1, T2, T3>, NonNullHackedInvokable<R, C, T1, T2, T3> {
        /**
         * Optional
         */
        @CheckResult
        <RR> HackedMethod<RR, C, T1, T2, T3> returning(Class<RR> type);

        /**
         * Whatever exception or none
         */
        @CheckResult
        <TT1 extends Throwable> NonNullHackedMethod<R, C, Exception, T2, T3> throwing();

        @CheckResult
        <TT1 extends Throwable> NonNullHackedMethod<R, C, TT1, T2, T3> throwing(Class<TT1> type);

        @CheckResult
        <TT1 extends Throwable, TT2 extends Throwable> NonNullHackedMethod<R, C, TT1, TT2, T3> throwing(Class<TT1> type1, Class<TT2> type2);

        @CheckResult
        <TT1 extends Throwable, TT2 extends Throwable, TT3 extends Throwable> NonNullHackedMethod<R, C, TT1, TT2, TT3> throwing(Class<TT1> type1, Class<TT2> type2, Class<TT3> type3);
    }

    public static class CheckedHackedMethod<R, C, T1 extends Throwable, T2 extends Throwable, T3 extends Throwable> {

        CheckedHackedMethod(final Invokable invokable) {
            mInvokable = invokable;
        }

        protected HackInvocation<R, C, T1, T2, T3> invoke(final Object... args) {
            return new HackInvocation<>(mInvokable, args);
        }

        /**
         * Whether this hack is absent, thus will be fallen-back when invoked
         */
        public boolean isAbsent() {
            return mInvokable instanceof FallbackInvokable;
        }

        private final Invokable mInvokable;
    }

    public static class HackedMethod0<R, C, T1 extends Throwable, T2 extends Throwable, T3 extends Throwable>
            extends CheckedHackedMethod<R, C, T1, T2, T3> {
        HackedMethod0(final Invokable invokable) {
            super(invokable);
        }

        public @CheckResult
        HackInvocation<R, C, T1, T2, T3> invoke() {
            return super.invoke();
        }
    }

    public static class HackedMethod1<R, C, T1 extends Throwable, T2 extends Throwable, T3 extends Throwable, A1>
            extends CheckedHackedMethod<R, C, T1, T2, T3> {
        HackedMethod1(final Invokable invokable) {
            super(invokable);
        }

        public @CheckResult
        HackInvocation<R, C, T1, T2, T3> invoke(final A1 arg) {
            return super.invoke(arg);
        }
    }

    public static class HackedMethod2<R, C, T1 extends Throwable, T2 extends Throwable, T3 extends Throwable, A1, A2>
            extends CheckedHackedMethod<R, C, T1, T2, T3> {
        HackedMethod2(final Invokable invokable) {
            super(invokable);
        }

        public @CheckResult
        HackInvocation<R, C, T1, T2, T3> invoke(final A1 arg1, final A2 arg2) {
            return super.invoke(arg1, arg2);
        }
    }

    public static class HackedMethod3<R, C, T1 extends Throwable, T2 extends Throwable, T3 extends Throwable, A1, A2, A3>
            extends CheckedHackedMethod<R, C, T1, T2, T3> {
        HackedMethod3(final Invokable invokable) {
            super(invokable);
        }

        public @CheckResult
        HackInvocation<R, C, T1, T2, T3> invoke(final A1 arg1, final A2 arg2, final A3 arg3) {
            return super.invoke(arg1, arg2, arg3);
        }
    }

    public static class HackedMethod4<R, C, T1 extends Throwable, T2 extends Throwable, T3 extends Throwable, A1, A2, A3, A4>
            extends CheckedHackedMethod<R, C, T1, T2, T3> {
        HackedMethod4(final Invokable invokable) {
            super(invokable);
        }

        public @CheckResult
        HackInvocation<R, C, T1, T2, T3> invoke(final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4) {
            return super.invoke(arg1, arg2, arg3, arg4);
        }
    }

    public static class HackedMethod5<R, C, T1 extends Throwable, T2 extends Throwable, T3 extends Throwable, A1, A2, A3, A4, A5>
            extends CheckedHackedMethod<R, C, T1, T2, T3> {
        HackedMethod5(final Invokable invokable) {
            super(invokable);
        }

        public @CheckResult
        HackInvocation<R, C, T1, T2, T3> invoke(final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5) {
            return super.invoke(arg1, arg2, arg3, arg4, arg5);
        }
    }

    public static class HackedMethodN<R, C, T1 extends Throwable, T2 extends Throwable, T3 extends Throwable>
            extends CheckedHackedMethod<R, C, T1, T2, T3> {
        HackedMethodN(final Invokable invokable) {
            super(invokable);
        }

        public @CheckResult
        HackInvocation<R, C, T1, T2, T3> invoke(final Object... args) {
            return super.invoke(args);
        }
    }

    public static class HackInvocation<R, C, T1 extends Throwable, T2 extends Throwable, T3 extends Throwable> {

        HackInvocation(final Invokable invokable, final Object... args) {
            this.invokable = invokable;
            this.args = args;
        }

        public R on(final @NonNull C target) throws T1, T2, T3 {
            return onTarget(target);
        }

        public R statically() throws T1, T2, T3 {
            return onTarget(null);
        }

        @SuppressWarnings("RedundantThrows")
        private R onTarget(final @Nullable C target) throws T1, T2, T3 {
            try {
                @SuppressWarnings("unchecked") final R result = (R) invokable.invoke(target, args);
                return result;
            } catch (final IllegalAccessException e) {
                throw new RuntimeException(e);    // Should never happen
            } catch (final InstantiationException e) {
                throw new RuntimeException(e);
            } catch (final InvocationTargetException e) {
                final Throwable ex = e.getTargetException();
                //noinspection unchecked
                throw (T1) ex;        // The casting is actually no-op after erasure, it throws the exception directly.
            }
        }

        private final Invokable invokable;
        private final Object[] args;
    }

    interface Invokable<C> {
        Object invoke(@Nullable C target, Object[] args) throws InvocationTargetException, IllegalAccessException, InstantiationException;
    }

    private static class HackedMethodImpl<R, C, T1 extends Throwable, T2 extends Throwable, T3 extends Throwable>
            implements NonNullHackedMethod<R, C, T1, T2, T3> {

        HackedMethodImpl(final Class<?> clazz, @Nullable final String name, final int modifiers) {
            //noinspection unchecked, to be compatible with HackedClass.staticMethod()
            mClass = (Class<C>) clazz;
            mName = name;
            mModifiers = modifiers;
        }

        @Override
        public <RR> HackedMethod<RR, C, T1, T2, T3> returning(final Class<RR> type) {
            mReturnType = type;
            @SuppressWarnings("unchecked") final HackedMethod<RR, C, T1, T2, T3> casted = (HackedMethod<RR, C, T1, T2, T3>) this;
            return casted;
        }

        @Override
        public NonNullHackedMethod<R, C, T1, T2, T3> fallbackReturning(final @Nullable R value) {
            mFallbackReturnValue = value;
            mHasFallback = true;
            return this;
        }

        @Override
        public NonNullHackedMethod<R, C, Exception, T2, T3> throwing() {
            mThrowTypes = new Class[]{};
            @SuppressWarnings("unchecked") final NonNullHackedMethod<R, C, Exception, T2, T3> casted = (NonNullHackedMethod<R, C, Exception, T2, T3>) this;
            return casted;
        }

        @Override
        public <TT extends Throwable> NonNullHackedMethod<R, C, TT, T2, T3> throwing(final Class<TT> type) {
            mThrowTypes = new Class[]{type};
            @SuppressWarnings("unchecked") final NonNullHackedMethod<R, C, TT, T2, T3> casted = (NonNullHackedMethod<R, C, TT, T2, T3>) this;
            return casted;
        }

        @Override
        public <TT1 extends Throwable, TT2 extends Throwable> NonNullHackedMethod<R, C, TT1, TT2, T3>
        throwing(final Class<TT1> type1, final Class<TT2> type2) {
            mThrowTypes = new Class<?>[]{type1, type2};
            Arrays.sort(mThrowTypes, CLASS_COMPARATOR);
            @SuppressWarnings("unchecked") final NonNullHackedMethod<R, C, TT1, TT2, T3> cast = (NonNullHackedMethod<R, C, TT1, TT2, T3>) this;
            return cast;
        }

        @Override
        public <TT1 extends Throwable, TT2 extends Throwable, TT3 extends Throwable> NonNullHackedMethod<R, C, TT1, TT2, TT3>
        throwing(final Class<TT1> type1, final Class<TT2> type2, final Class<TT3> type3) {
            mThrowTypes = new Class<?>[]{type1, type2, type3};
            Arrays.sort(mThrowTypes, CLASS_COMPARATOR);
            @SuppressWarnings("unchecked") final NonNullHackedMethod<R, C, TT1, TT2, TT3> cast = (NonNullHackedMethod<R, C, TT1, TT2, TT3>) this;
            return cast;
        }

        @Override
        public HackedMethod<R, C, Exception, T2, T3> throwing(final Class<?>... types) {
            mThrowTypes = types;
            Arrays.sort(mThrowTypes, CLASS_COMPARATOR);
            @SuppressWarnings("unchecked") final HackedMethod<R, C, Exception, T2, T3> cast = (HackedMethod<R, C, Exception, T2, T3>) this;
            return cast;
        }

        @NonNull
        @SuppressWarnings("ConstantConditions")
        @Override
        public HackedMethod0<R, C, T1, T2, T3> withoutParams() {
            final Invokable<C> invokable = buildInvokable();
            return invokable == null ? null : new HackedMethod0<R, C, T1, T2, T3>(invokable);
        }

        @NonNull
        @SuppressWarnings("ConstantConditions")
        @Override
        public <A1> HackedMethod1<R, C, T1, T2, T3, A1> withParam(final Class<A1> type) {
            final Invokable invokable = buildInvokable(type);
            return invokable == null ? null : new HackedMethod1<R, C, T1, T2, T3, A1>(invokable);
        }

        @NonNull
        @SuppressWarnings("ConstantConditions")
        @Override
        public <A1, A2> HackedMethod2<R, C, T1, T2, T3, A1, A2> withParams(final Class<A1> type1, final Class<A2> type2) {
            final Invokable invokable = buildInvokable(type1, type2);
            return invokable == null ? null : new HackedMethod2<R, C, T1, T2, T3, A1, A2>(invokable);
        }

        @NonNull
        @SuppressWarnings("ConstantConditions")
        @Override
        public <A1, A2, A3> HackedMethod3<R, C, T1, T2, T3, A1, A2, A3> withParams(final Class<A1> type1, final Class<A2> type2, final Class<A3> type3) {
            final Invokable invokable = buildInvokable(type1, type2, type3);
            return invokable == null ? null : new HackedMethod3<R, C, T1, T2, T3, A1, A2, A3>(invokable);
        }

        @NonNull
        @SuppressWarnings("ConstantConditions")
        @Override
        public <A1, A2, A3, A4> HackedMethod4<R, C, T1, T2, T3, A1, A2, A3, A4> withParams(final Class<A1> type1, final Class<A2> type2, final Class<A3> type3, final Class<A4> type4) {
            final Invokable invokable = buildInvokable(type1, type2, type3, type4);
            return invokable == null ? null : new HackedMethod4<R, C, T1, T2, T3, A1, A2, A3, A4>(invokable);
        }

        @NonNull
        @SuppressWarnings("ConstantConditions")
        @Override
        public <A1, A2, A3, A4, A5> HackedMethod5<R, C, T1, T2, T3, A1, A2, A3, A4, A5> withParams(final Class<A1> type1, final Class<A2> type2, final Class<A3> type3, final Class<A4> type4, final Class<A5> type5) {
            final Invokable invokable = buildInvokable(type1, type2, type3, type4, type5);
            return invokable == null ? null : new HackedMethod5<R, C, T1, T2, T3, A1, A2, A3, A4, A5>(invokable);
        }

        @NonNull
        @SuppressWarnings("ConstantConditions")
        @Override
        public HackedMethodN<R, C, T1, T2, T3> withParams(final Class<?>... types) {
            final Invokable invokable = buildInvokable(types);
            return invokable == null ? null : new HackedMethodN<R, C, T1, T2, T3>(invokable);
        }

        private @Nullable
        Invokable<C> buildInvokable(final Class<?>... param_types) {
            return LAZY_RESOLVE && mHasFallback ? new LazyInvokable<>(this, param_types) : findInvokable(param_types);
        }

        private @Nullable
        Invokable<C> findInvokable(final Class<?>... param_types) {
            if (mClass == ANY_TYPE)        // AnyType as a internal indicator for class not found.
                return mHasFallback ? new FallbackInvokable<C>(mFallbackReturnValue) : null;

            final int modifiers;
            Invokable<C> invokable;
            final AccessibleObject accessible;
            final Class<?>[] ex_types;
            try {
                if (mName != null) {
                    final Method candidate = mClass.getDeclaredMethod(mName, param_types);
                    Method method = candidate;
                    ex_types = candidate.getExceptionTypes();
                    modifiers = method.getModifiers();
                    if (Modifier.isStatic(mModifiers) != Modifier.isStatic(candidate.getModifiers())) {
                        fail(new AssertionException(candidate + (Modifier.isStatic(mModifiers) ? " is not static" : "is static")).setHackedMethod(method));
                        method = null;
                    }
                    if (mReturnType != null && mReturnType != ANY_TYPE && !candidate.getReturnType().equals(mReturnType)) {
                        fail(new AssertionException("Return type mismatch: " + candidate));
                        method = null;
                    }
                    if (method != null) {
                        invokable = new InvokableMethod<>(method);
                        accessible = method;
                    } else {
                        invokable = null;
                        accessible = null;
                    }
                } else {
                    final Constructor<C> ctor = mClass.getDeclaredConstructor(param_types);
                    modifiers = ctor.getModifiers();
                    invokable = new InvokableConstructor<>(ctor);
                    accessible = ctor;
                    ex_types = ctor.getExceptionTypes();
                }
            } catch (final NoSuchMethodException e) {
                final AssertionException failure = new AssertionException(e).setHackedClass(mClass).setParamTypes(param_types);
                if (mName != null) failure.setHackedMethodName(mName);
                fail(failure);
                return mHasFallback ? new FallbackInvokable<C>(mFallbackReturnValue) : null;
            }

            if (mModifiers > 0 && (modifiers & mModifiers) != mModifiers) {
                final AssertionException failure = new AssertionException(invokable + " does not match modifiers: " + mModifiers);
                if (mName != null) failure.setHackedMethodName(mName);
                fail(failure);
            }

            if (mThrowTypes == null && ex_types.length > 0 || mThrowTypes != null && mThrowTypes.length > 0 && ex_types.length == 0) {
                fail(new AssertionException("Checked exception(s) not match: " + invokable));
                if (ex_types.length > 0)
                    invokable = null;        // No need to fall-back if expected checked exceptions are absent.
            } else if (mThrowTypes != null && mThrowTypes.length > 0) {        // Empty array stands for "whatever exception or none"
                if (mThrowTypes.length > 1) Arrays.sort(ex_types, CLASS_COMPARATOR);
                if (!Arrays.equals(ex_types, mThrowTypes)) {    // TODO: Check derived relation of exceptions
                    fail(new AssertionException("Checked exception(s) not match: " + invokable));
                    invokable = null;
                }
            }

            if (invokable == null) {
                if (!mHasFallback) return null;
                return new FallbackInvokable<>(mFallbackReturnValue);
            }

            if (!accessible.isAccessible()) accessible.setAccessible(true);
            return invokable;
        }

        private final Class<C> mClass;
        private final @Nullable
        String mName;        // Null for constructor
        private final int mModifiers;
        private Class<?> mReturnType;
        private Class<?>[] mThrowTypes;
        private R mFallbackReturnValue;
        private boolean mHasFallback;
        private static final Comparator<Class> CLASS_COMPARATOR = new Comparator<Class>() {
            @Override
            public int compare(final Class lhs, final Class rhs) {
                return lhs.toString().compareTo(rhs.toString());
            }

            @Override
            public boolean equals(final Object object) {
                return this == object;
            }
        };
    }

    private static class InvokableMethod<C> implements Invokable<C> {

        InvokableMethod(final Method method) {
            this.method = method;
        }

        public Object invoke(final @Nullable C target, final Object[] args) throws IllegalAccessException,
                IllegalArgumentException, InvocationTargetException {
            return method.invoke(target, args);
        }

        @Override
        public String toString() {
            return method.toString();
        }

        private final Method method;
    }

    private static class InvokableConstructor<C> implements Invokable<C> {

        InvokableConstructor(final Constructor<C> method) {
            this.constructor = method;
        }

        public Object invoke(final @Nullable C target, final Object[] args) throws InstantiationException,
                IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            return constructor.newInstance(args);
        }

        @Override
        public String toString() {
            return constructor.toString();
        }

        private final Constructor<C> constructor;
    }

    private static class FallbackInvokable<C> implements Invokable<C> {

        FallbackInvokable(final @Nullable Object value) {
            mValue = value;
        }

        @Override
        public Object invoke(final @Nullable C target, final Object[] args) {
            return mValue;
        }

        private final @Nullable
        Object mValue;
    }

    private static class LazyInvokable<C> implements Invokable<C> {

        LazyInvokable(final HackedMethodImpl<?, C, ?, ?, ?> method, final Class<?>[] param_types) {
            mMethod = method;
            mParamTypes = param_types;
        }

        @Override
        public Object invoke(final @Nullable C target, final Object[] args) throws InvocationTargetException, IllegalAccessException, InstantiationException {
            //noinspection ConstantConditions, since fallback is provided
            return mMethod.findInvokable(mParamTypes).invoke(target, args);
        }

        private final HackedMethodImpl<?, C, ?, ?, ?> mMethod;
        private final Class<?>[] mParamTypes;
    }

    public static class HackedClass<C> {

        public @CheckResult
        <T> MemberFieldToHack<C> field(final @NonNull String name) {
            return new MemberFieldToHack<>(mClass, name, 0);
        }

        public @CheckResult
        <T> StaticFieldToHack<C> staticField(final @NonNull String name) {
            return new StaticFieldToHack<>(mClass, name, Modifier.STATIC);
        }

        public @CheckResult
        HackedMethod<Void, C, Unchecked, Unchecked, Unchecked> method(final String name) {
            return new HackedMethodImpl<>(mClass, name, 0);
        }

        public @CheckResult
        HackedMethod<Void, Void, Unchecked, Unchecked, Unchecked> staticMethod(final String name) {
            return new HackedMethodImpl<>(mClass, name, Modifier.STATIC);
        }

        public @CheckResult
        NonNullHackedInvokable<C, Void, Unchecked, Unchecked, Unchecked> constructor() {
            final HackedMethodImpl<C, Void, Unchecked, Unchecked, Unchecked> constructor = new HackedMethodImpl<>(mClass, null, 0);
            return constructor.fallbackReturning(null);    // Always fallback to null.
        }

        HackedClass(final Class<C> clazz) {
            mClass = clazz;
        }

        private final Class<C> mClass;
    }

    public static <T> HackedClass<T> into(final @NonNull Class<T> clazz) {
        return new HackedClass<>(clazz);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> HackedClass<T> into(final String class_name) {
        try {
            return new HackedClass(Class.forName(class_name));
        } catch (final ClassNotFoundException e) {
            fail(new AssertionException(e));
            return new HackedClass(ANY_TYPE);        // Use AnyType as a lazy trick to make fallback working and avoid null.
        }
    }

    @SuppressWarnings("unchecked")
    public static <C> Hack.HackedClass<C> onlyIf(final boolean condition, final Hacking<Hack.HackedClass<C>> hacking) {
        if (condition) return hacking.hack();
        return (Hack.HackedClass<C>) FALLBACK;
    }

    public interface Hacking<T> {
        T hack();
    }

    private static final Hack.HackedClass<?> FALLBACK = new HackedClass<>(ANY_TYPE);

    public static ConditionalHack onlyIf(final boolean condition) {
        return condition ? new ConditionalHack() {
            @Override
            public <T> HackedClass<T> into(@NonNull final Class<T> clazz) {
                return Hack.into(clazz);
            }

            @Override
            public <T> HackedClass<T> into(final String class_name) {
                return Hack.into(class_name);
            }
        } : new ConditionalHack() {
            @SuppressWarnings("unchecked")
            @Override
            public <T> HackedClass<T> into(@NonNull final Class<T> clazz) {
                return (HackedClass<T>) FALLBACK;
            }

            @SuppressWarnings("unchecked")
            @Override
            public <T> HackedClass<T> into(final String class_name) {
                return (HackedClass<T>) FALLBACK;
            }
        };
    }

    public interface ConditionalHack {
        /**
         * WARNING: Never use this method if the target class may not exist when the condition is not met, use {@link #onlyIf(boolean, Hacking)} instead.
         */
        <T> HackedClass<T> into(final @NonNull Class<T> clazz);

        <T> HackedClass<T> into(final String class_name);
    }

    private static void fail(final AssertionException e) {
        if (sFailureHandler != null) sFailureHandler.onAssertionFailure(e);
    }

    /**
     * Specify a handler to deal with assertion failure, and decide whether the failure should be thrown.
     */
    public static AssertionFailureHandler setAssertionFailureHandler(final AssertionFailureHandler handler) {
        final AssertionFailureHandler old = sFailureHandler;
        sFailureHandler = handler;
        return old;
    }

    private Hack() {
    }

    private static AssertionFailureHandler sFailureHandler;


}
