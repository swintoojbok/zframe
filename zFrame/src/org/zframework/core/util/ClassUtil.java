package org.zframework.core.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * TODO ClassUtil
 * 
 * @author xiaocao000 
 * Date：2009-3-14
 * @version 1.0
 */
public class ClassUtil
{
    public static boolean isJavaBasicType(Class<?> clazz)
    {
        if (clazz == null)
        {
            return false;
        }

        return clazz.isPrimitive() || Number.class.isAssignableFrom(clazz)
                || Character.class.isAssignableFrom(clazz)
                || Boolean.class.isAssignableFrom(clazz)
                || CharSequence.class.isAssignableFrom(clazz)
                || Enum.class.isAssignableFrom(clazz)
                || Date.class.isAssignableFrom(clazz)
                || Calendar.class.isAssignableFrom(clazz);
    }

    /**
     * 判断指定的类是否为Collection（或者其子类或者其子接口）。
     * 
     * @param clazz 需要判断的类
     * @return true：是Collection false：非Collection
     */
    public static boolean isCollection(Class<?> clazz)
    {
        if (clazz == null)
        {
            return false;
        }

        return Collection.class.isAssignableFrom(clazz);
    }

    /**
     * 判断指定的类是否为Map（或者其子类或者其子接口)。
     * 
     * @param clazz 需要判断的类
     * @return true：是Map false：非Map
     */
    public static boolean isMap(Class<?> clazz)
    {
        if (clazz == null)
        {
            return false;
        }

        return Map.class.isAssignableFrom(clazz);
    }

    /**
     * 判断指定的类是否为Java基本型别的数组。
     * 
     * @param clazz 需要判断的类
     * @return true：是Java基本型别的数组 false：非Java基本型别的数组
     */
    public static boolean isPrimitiveArray(Class<?> clazz)
    {
        if (clazz == null)
            return false;

        if (clazz == byte[].class)
            return true;
        else if (clazz == short[].class)
            return true;
        else if (clazz == int[].class)
            return true;
        else if (clazz == long[].class)
            return true;
        else if (clazz == char[].class)
            return true;
        else if (clazz == float[].class)
            return true;
        else if (clazz == double[].class)
            return true;
        else if (clazz == boolean[].class)
            return true;
        else
        { // element is an array of object references
            return false;
        }
    }

    /**
     * 判断指定的类是否为Java基本型别的数组。
     * 
     * @param clazz 需要判断的类
     * @return true：是Java基本型别的数组 false：非Java基本型别的数组
     */
    public static boolean isPrimitiveWrapperArray(Class<?> clazz)
    {
        if (clazz == null)
            return false;

        if (clazz == Byte[].class)
            return true;
        else if (clazz == Short[].class)
            return true;
        else if (clazz == Integer[].class)
            return true;
        else if (clazz == Long[].class)
            return true;
        else if (clazz == Character[].class)
            return true;
        else if (clazz == Float[].class)
            return true;
        else if (clazz == Double[].class)
            return true;
        else if (clazz == Boolean[].class)
            return true;
        else
        { // element is an array of object references
            return false;
        }
    }

    /**
     * 获取包括该类本身但不包含java.lang.Object的所有超类。
     * 
     * @param clazz Class
     * @return 超类数组
     */
    public static Class<?>[] getAllClass(Class<?> clazz)
    {
        List<Class<?>> clazzList = new ArrayList<Class<?>>();
        getAllSupperClass0(clazzList, clazz);
        return clazzList.toArray(new Class<?>[] {});
    }

    private static void getAllSupperClass0(List<Class<?>> clazzList,
            Class<?> clazz)
    {
        if (clazz.equals(Object.class))
        {
            return;
        }
        clazzList.add(clazz);
        getAllSupperClass0(clazzList, clazz.getSuperclass());
    }
    
    /**
     * 获取该类所有实现的接口数组。
     * 
     * @param clazz Class
     * @return 该类所有实现的接口数组
     */
    public static Class<?>[] getAllInterface(Class<?> clazz)
    {
        List<Class<?>> clazzList = new ArrayList<Class<?>>();
        Class<?>[] interfaces = clazz.getInterfaces();

        for (Class<?> interfaceClazz : interfaces)
        {
            clazzList.add(interfaceClazz);
            getAllSupperInterface0(clazzList, interfaceClazz);
        }

        return clazzList.toArray(new Class<?>[] {});
    }

    private static void getAllSupperInterface0(List<Class<?>> clazzList,
            Class<?> clazz)
    {
        if (clazz.equals(Object.class))
        {
            return;
        }

        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> interfaceClazz : interfaces)
        {
            clazzList.add(interfaceClazz);
            getAllSupperInterface0(clazzList, interfaceClazz);
        }
    }

    /**
     * 获取包括该类本身以及所有超类（不含java.lang.Object）和实现的接口中定义的属性。
     * 
     * @param clazz Class
     * @return 属性数组
     */
    public static Field[] getAllField(Class<?> clazz)
    {
        List<Field> fieldList = new ArrayList<Field>();
        Class<?>[] supperClazzs = ClassUtil.getAllClass(clazz);
        for (Class<?> aClazz : supperClazzs)
        {
            ObjectUtil.addAll(fieldList, aClazz.getDeclaredFields());
        }
        
        Class<?>[] supperInterfaces = ClassUtil.getAllInterface(clazz);
        for (Class<?> aInterface : supperInterfaces)
        {
            ObjectUtil.addAll(fieldList, aInterface.getDeclaredFields());
        }

        return fieldList.toArray(new Field[] {});
    }
    /**
     * 获取该类中所有字段
     * @param clazz
     * @return
     */
    public static Field[] getFields(Class<?> clazz)
    {
    	List<Field> fieldList = new ArrayList<Field>();
    	ObjectUtil.addAll(fieldList, clazz.getDeclaredFields());
    	return fieldList.toArray(new Field[] {});
    }
}
