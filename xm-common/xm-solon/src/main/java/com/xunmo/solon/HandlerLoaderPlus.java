package com.xunmo.solon;

import cn.hutool.core.util.ObjectUtil;
import org.noear.solon.Utils;
import org.noear.solon.annotation.After;
import org.noear.solon.annotation.Before;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Options;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.HandlerAide;
import org.noear.solon.core.handle.HandlerSlots;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.handle.MethodTypeUtil;
import org.noear.solon.core.handle.Render;
import org.noear.solon.core.util.ConsumerEx;
import org.noear.solon.core.util.PathUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 通用处理接口加载器（根据bean加载）
 *
 * @author noear
 * @since 1.0
 * */
public class HandlerLoaderPlus extends HandlerAide {
    protected BeanWrap bw;
    protected Render bRender;
    protected Mapping bMapping;
    protected String bPath;
    protected boolean bRemoting;

    protected boolean allowMapping;

    public HandlerLoaderPlus(BeanWrap wrap) {
        final Class<?> clz = wrap.clz();
        if (!clz.isInterface()) {
            bMapping = clz.getAnnotation(Mapping.class);
            if (bMapping == null) {
                Class<?>[] interfaces = clz.getInterfaces();
                if (ObjectUtil.isNotNull(interfaces) && interfaces.length > 0) {
                    for (Class<?> anInterface : interfaces) {
                        bMapping = anInterface.getAnnotation(Mapping.class);
                    }
                }
            }
        }

        if (bMapping == null) {
            initDo(wrap, null, wrap.remoting(), null, true);
        } else {
            String bPath = Utils.annoAlias(bMapping.value(), bMapping.path());
            initDo(wrap, bPath, wrap.remoting(), null, true);
        }
    }

    public HandlerLoaderPlus(BeanWrap wrap, String mapping) {
        initDo(wrap, mapping, wrap.remoting(), null, true);
    }

    public HandlerLoaderPlus(BeanWrap wrap, String mapping, boolean remoting) {
        initDo(wrap, mapping, remoting, null, true);
    }

    public HandlerLoaderPlus(BeanWrap wrap, String mapping, boolean remoting, Render render, boolean allowMapping) {
        initDo(wrap, mapping, remoting, render, allowMapping);
    }

    private void initDo(BeanWrap wrap, String mapping, boolean remoting, Render render, boolean allowMapping) {
        bw = wrap;
        bRender = render;
        this.allowMapping = allowMapping;

        if (mapping != null) {
            bPath = mapping;
        }

        bRemoting = remoting;
    }

    /**
     * mapping expr
     */
    public String mapping() {
        return bPath;
    }

    /**
     * 加载 Action 到目标容器
     *
     * @param slots 接收加载结果的容器（槽）
     */
    public void load(HandlerSlots slots) {
        load(bRemoting, slots);
    }

    /**
     * 加载 Action 到目标容器
     *
     * @param all   加载全部函数（一般 remoting 会全部加载）
     * @param slots 接收加载结果的容器（槽）
     */
    public void load(boolean all, HandlerSlots slots) {
        if (Handler.class.isAssignableFrom(bw.clz())) {
            loadHandlerDo(slots);
        } else {
            loadActionDo(slots, all || bRemoting);
        }
    }

    /**
     * 加载处理
     */
    protected void loadHandlerDo(HandlerSlots slots) {
        if (bMapping == null) {
            throw new IllegalStateException(bw.clz().getName() + " No @Mapping!");
        }

        Handler handler = bw.raw();
        Set<MethodType> v0 = MethodTypeUtil.findAndFill(new HashSet<>(), t -> bw.annotationGet(t) != null);
        if (v0.size() == 0) {
            v0 = new HashSet<>(Arrays.asList(bMapping.method()));
        }

        slots.add(bMapping, v0, handler);
    }

    /**
     * 查找 method
     */
    protected Method[] findMethods(Class<?> clz) {
        return clz.getMethods();
    }


    /**
     * 加载 Action 处理
     */
    protected void loadActionDo(HandlerSlots slots, boolean all) {
        String m_path;

        if (bPath == null) {
            bPath = "";
        }

        Set<MethodType> b_method = new HashSet<>();

        loadControllerAide(b_method);

        Set<MethodType> m_method;
        Mapping m_map;
        int m_index = 0;

        Map<String, Mapping> interfaceMappingMethodMap = new HashMap<>();
        Map<String, Method> interfaceMethodMap = new HashMap<>();
        Class<?>[] interfaces = bw.clz().getInterfaces();
        if (ObjectUtil.isNotNull(interfaces) && interfaces.length > 0) {
            for (Class<?> anInterface : interfaces) {
                Controller annotation = anInterface.getAnnotation(Controller.class);
                if (annotation != null) {
                    Method[] methods = anInterface.getMethods();
                    for (Method method : methods) {
                        String name = method.getName();
                        Mapping mappingAnnotation = method.getAnnotation(Mapping.class);
                        if (mappingAnnotation != null) {
                            interfaceMethodMap.put(name, method);
                            interfaceMappingMethodMap.put(name, mappingAnnotation);
                        }
                    }
                } else {
                    Method[] methods = anInterface.getMethods();
                    for (Method method : methods) {
                        String name = method.getName();
                        Mapping mappingAnnotation = method.getAnnotation(Mapping.class);
                        if (mappingAnnotation != null) {
                            interfaceMethodMap.put(name, method);
                            interfaceMappingMethodMap.put(name, mappingAnnotation);
                        }
                    }
                }
            }
        }

        //只支持 public 函数为 Action
        for (Method method : findMethods(bw.clz())) {
            boolean isUseParent = false;
            Method parentMethod = null;

            m_map = method.getAnnotation(Mapping.class);
            if (m_map == null && !interfaceMappingMethodMap.isEmpty()) {
                m_map = interfaceMappingMethodMap.get(method.getName());
                parentMethod = interfaceMethodMap.get(method.getName());
                if (parentMethod != null) {
                    isUseParent = true;
                }
            }
            m_index = 0;
            m_method = new HashSet<>();

            //如果没有注解，则只允许 public
            if (m_map == null) {
                if (Modifier.isPublic(method.getModifiers()) == false) {
                    continue;
                }
            }

            //获取 action 的 methodTypes
            if (isUseParent) {
                Method finalParentMethod = parentMethod;
                MethodTypeUtil.findAndFill(m_method, t ->  finalParentMethod.getAnnotation(t) != null);
            } else {
                MethodTypeUtil.findAndFill(m_method, t -> method.getAnnotation(t) != null);
            }

            //构建path and method
            if (m_map != null) {
                m_path = Utils.annoAlias(m_map.value(), m_map.path());

                if (m_method.size() == 0) {
                    //如果没有找到，则用Mapping上自带的
                    m_method.addAll(Arrays.asList(m_map.method()));
                }
                m_index = m_map.index();
            } else {
                m_path = method.getName();

                if (m_method.size() == 0) {
                    //获取 controller 的 methodTypes
                    if (ObjectUtil.isNotNull(interfaces) && interfaces.length > 0) {
                        for (Class<?> anInterface : interfaces) {
                            Controller annotation = anInterface.getAnnotation(Controller.class);
                            if (annotation != null) {
                                MethodTypeUtil.findAndFill(m_method, t -> anInterface.getAnnotation(t) != null);
                            } else {
                                MethodTypeUtil.findAndFill(m_method, t -> anInterface.getAnnotation(t) != null);
                            }
                        }
                    } else {
                        MethodTypeUtil.findAndFill(m_method, t -> bw.clz().getAnnotation(t) != null);
                    }
                }

                if (m_method.size() == 0) {
                    //如果没有找到，则用Mapping上自带的；或默认
                    if (bMapping == null) {
                        m_method.add(MethodType.HTTP);
                    } else {
                        m_method.addAll(Arrays.asList(bMapping.method()));
                    }
                }
            }

            //如果是service，method 就不需要map
            if (m_map != null || all) {
                String newPath = PathUtil.mergePath(bPath, m_path);

                Action action = createAction(bw, method, m_map, newPath, bRemoting);

                //m_method 必须之前已准备好，不再动  //用于支持 Cors

                if (isUseParent) {
                    loadActionAide(parentMethod, action, m_method);
                } else {
                    loadActionAide(method, action, m_method);
                }
                if (b_method.size() > 0 &&
                        m_method.contains(MethodType.HTTP) == false &&
                        m_method.contains(MethodType.ALL) == false) {
                    //用于支持 Cors
                    m_method.addAll(b_method);
                }

                for (MethodType m1 : m_method) {
                    if (m_map == null) {
                        slots.add(newPath, m1, action);
                    } else {
                        if ((m_map.after() || m_map.before())) {
                            if (m_map.after()) {
                                slots.after(newPath, m1, m_index, action);
                            } else {
                                slots.before(newPath, m1, m_index, action);
                            }
                        } else {
                            slots.add(newPath, m1, action);
                        }
                    }
                }
            }
        }
    }


    protected void loadControllerAide(Set<MethodType> methodSet) {
        for (Annotation anno : bw.clz().getAnnotations()) {
            if (anno instanceof Before) {
                addDo(((Before) anno).value(), (b) -> this.before(bw.context().getBeanOrNew(b)));
            } else if (anno instanceof After) {
                addDo(((After) anno).value(), (f) -> this.after(bw.context().getBeanOrNew(f)));
            } else {
                for (Annotation anno2 : anno.annotationType().getAnnotations()) {
                    if (anno2 instanceof Before) {
                        addDo(((Before) anno2).value(), (b) -> this.before(bw.context().getBeanOrNew(b)));
                    } else if (anno2 instanceof After) {
                        addDo(((After) anno2).value(), (f) -> this.after(bw.context().getBeanOrNew(f)));
                    } else if (anno2 instanceof Options) {
                        //用于支持 Cors
                        methodSet.add(MethodType.OPTIONS);
                    }
                }
            }
        }
    }

    protected void loadActionAide(Method method, Action action, Set<MethodType> methodSet) {
        for (Annotation anno : method.getAnnotations()) {
            if (anno instanceof Before) {
                addDo(((Before) anno).value(), (b) -> action.before(bw.context().getBeanOrNew(b)));
            } else if (anno instanceof After) {
                addDo(((After) anno).value(), (f) -> action.after(bw.context().getBeanOrNew(f)));
            } else {
                for (Annotation anno2 : anno.annotationType().getAnnotations()) {
                    if (anno2 instanceof Before) {
                        addDo(((Before) anno2).value(), (b) -> action.before(bw.context().getBeanOrNew(b)));
                    } else if (anno2 instanceof After) {
                        addDo(((After) anno2).value(), (f) -> action.after(bw.context().getBeanOrNew(f)));
                    } else if (anno2 instanceof Options) {
                        //用于支持 Cors
                        if (methodSet.contains(MethodType.HTTP) == false &&
                                methodSet.contains(MethodType.ALL) == false) {
                            methodSet.add(MethodType.OPTIONS);
                        }
                    }
                }
            }
        }
    }

    /**
     * 构建 Action
     */
    protected Action createAction(BeanWrap bw, Method method, Mapping mp, String path, boolean remoting) {
        if (allowMapping) {
            return new Action(bw, this, method, mp, path, remoting, bRender);
        } else {
            return new Action(bw, this, method, null, path, remoting, bRender);
        }
    }

    /**
     * 附加触发器（前后置处理）
     */
    private static <T> void addDo(T[] ary, ConsumerEx<T> fun) {
        if (ary != null) {
            for (T t : ary) {
                try {
                    fun.accept(t);
                } catch (RuntimeException ex) {
                    throw ex;
                } catch (Throwable ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
