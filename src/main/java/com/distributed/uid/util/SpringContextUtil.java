package com.distributed.uid.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * @author typhoon
 * @Description
 * @date 2022-06-22 10:44 Wednesday
 * @since V2.0.0
 */
public class SpringContextUtil implements ApplicationContextAware {
    /**
     * 将applicationContext设置成静态属性,一次初始化后就任何地方就可以使用
     */
    private static ApplicationContext applicationContext;

    /**
     * 获取spring上下文信息
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        checkApplicationContext();
        return applicationContext;
    }

    /*
     * 该方法在spring初始化该bean的时候触发,触发之后该类的静态属性ApplicationContext就获得了spring的上下文信息
     *
     * (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * 根据名称获取bean
     *
     * @param name
     * @return
     */
    public static <T> T getBean(String name) {
        checkApplicationContext();
        return (T) applicationContext.getBean(name);
    }

    /**
     * 根据类型获取bean(如果一个接口有多个实现这种方式获取会有问题)
     *
     * @param requiredType
     * @return
     */
    public static <T> T getBean(Class<T> requiredType) {
        checkApplicationContext();
        return applicationContext.getBean(requiredType);
    }

    /**
     * 检查是否包含name对应的bean
     *
     * @param name
     * @return
     */
    public static boolean containsBean(String name) {
        checkApplicationContext();
        return applicationContext.containsBean(name);
    }

    /**
     * 获取对应类型的所有bean列表
     *
     * @param requiredType
     * @return
     */
    public static <T> Map<String, T> getBeans(Class<T> requiredType) {
        checkApplicationContext();
        return applicationContext.getBeansOfType(requiredType);
    }

    /**
     * 根据名称和类型获取bean
     *
     * @param name
     * @param clazz
     * @return
     * @author Typhoon
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        checkApplicationContext();
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 清空上下文信息
     */
    public static void cleanApplicationContext() {
        applicationContext = null;
    }

    /**
     * 检查上下文是否注册成功
     */
    private static void checkApplicationContext() {
        if (applicationContext == null)
            throw new IllegalStateException("applicationContext未注入,请在配置中定义SpringContextUtil");
    }
}
