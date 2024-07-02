package com.kasukusakura.gateway.enhancement;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class UnsafeUtils {
    private static Unsafe unsafe;

    public static Unsafe getUnsafe() {
        if (unsafe == null) {
            try {
                Field field = Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                unsafe = (Unsafe) field.get(null);
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }
        return unsafe;
    }

    public static void deepCopying(Object from, Object to, Class<?> starting) {
        if (from == null || to == null) return;

        Class<?> crtClass = starting;
        while (crtClass != null && crtClass != Object.class) {

            for (Field field : crtClass.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;

                Class<?> type = field.getType();
                long offset = unsafe.objectFieldOffset(field);
                if (type == int.class) {
                    unsafe.putInt(to, offset, unsafe.getInt(from, offset));
                } else if (type == boolean.class) {
                    unsafe.putBoolean(to, offset, unsafe.getBoolean(from, offset));
                } else if (type == float.class) {
                    unsafe.putFloat(to, offset, unsafe.getFloat(from, offset));
                } else if (type == double.class) {
                    unsafe.putDouble(to, offset, unsafe.getDouble(from, offset));
                } else if (type == long.class) {
                    unsafe.putLong(to, offset, unsafe.getLong(from, offset));
                } else if (type == short.class) {
                    unsafe.putShort(to, offset, unsafe.getShort(from, offset));
                } else if (type == byte.class) {
                    unsafe.putByte(to, offset, unsafe.getByte(from, offset));
                } else if (type == char.class) {
                    unsafe.putChar(to, offset, unsafe.getChar(from, offset));
                } else {
                    unsafe.putObject(to, offset, unsafe.getObject(from, offset));
                }
            }

            crtClass = crtClass.getSuperclass();
        }
    }

    public static void scanningReplace(Object scanTarget, Object old, Object now) {
        getUnsafe();

        Class<?> loopingClass = scanTarget.getClass();
        while (loopingClass != Object.class) {
            for (Field f : loopingClass.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) continue;
                if (f.getType().isPrimitive()) continue;

                long offset = unsafe.objectFieldOffset(f);
                if (unsafe.getObject(scanTarget, offset) == old) {
                    unsafe.putObject(scanTarget, offset, now);
                }
            }


            loopingClass = loopingClass.getSuperclass();
        }

    }
}
