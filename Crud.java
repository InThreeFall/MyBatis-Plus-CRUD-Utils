package com.example.server.util;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.server.config.SpringContextUtil;
import org.apache.ibatis.session.ResultHandler;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 链式调用
 * 因为本人异常处理理解的还不是很透彻，因此都抛出去了..
 *
 * 基于spring，依赖：
 * @Configuration
 * public class SpringContextUtil implements ApplicationContextAware {
 *     private static ApplicationContext applicationContext;
 *     @Override
 *     public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
 *         SpringContextUtil.applicationContext = applicationContext;
 *     }
 *     public static Object getBean(Class<?> c) throws BeansException{
 *         return applicationContext.getBean(c);
 *     }
 * }
 *
 *
 * 使用方法例子：
 * 方式一
 *    public <T> List<?> selectAll(Class<T> entityCls){
 *        Page<T> page = new Page<>(current,size);
 *        Object res = Crud.create()
 *                       .mapper(entityCls,MapperClassPackage)
 *                       .selectList(null);
 *        return ObjectUtil.objToList(res,entityCls); //防止警报，貌似直接转List也可以，应该没问题。
 *        //return (List<?>)res;
 *    }
 * 方式二
 *    public int updateById(Object entity,Class<?> entityCLs){
 *        Object res = Crud.create()
 *                 .mapper(entityCLs,MapperClassPackage)
 *                 .method("updateById",Object.class) //参数1是方法名称，参数二是方法调用所需要的类型
 *                 .invoke(entity); 注意此方法不能传null，请使用(Object)null
 *         return (int)res;
 *    }
 */
public class Crud {
    public static Crud create(){
        return new Crud();
    }
    protected Crud() {
    }

    /**
     * 1.调用mapper，创建mapper操作对象
     * @param entityClass 实体类的class 例如：User.class
     * @param mapperPackage 包路径 例如：MapperClassPackage = "com.example.server.mapper";
     * @return 链式调用
     * @throws ClassNotFoundException
     */
    public InMapper mapper(Class<?> entityClass, String mapperPackage) throws ClassNotFoundException {
        String mapperName = mapperPackage + "." + entityClass.getSimpleName() + "Mapper";
        Class<?> clazz = Class.forName(mapperName); //获得mapper接口的类
        Object mapper = SpringContextUtil.getBean(clazz);
        InMapper inMapper = new InMapper();
        inMapper.mapper = mapper;
        return inMapper;
    }

    public class InMapper {
        InMapper() {
        }
        private Object mapper;

        /**
         * 2.调用method,选择mapper操作的方法
         * @param methodName 调用方法的名称 例如："selectList"
         * @param parameterTypes 方法所需要参数的类型 多参数 例如： method("update",Object.class,Wrapper.class)
         * @return 链式调用
         * @throws NoSuchMethodException
         * 注意，mapper要调用的方法的参数类型请严格参照Mybatis-plus的参数，如果是 T 则为 Object
         */
        public InMethod method(String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
            Method method = getMethod(methodName,parameterTypes);
            InMethod inMethod = new InMethod();
            inMethod.mapper = mapper;
            inMethod.method = method;
            return inMethod;
        }
        private Method getMethod(String methodName,Class<?>... parameterTypes) throws NoSuchMethodException {
            return mapper.getClass().getDeclaredMethod(methodName, parameterTypes);
        }
        /**
         * 以下为固定参数方法
         */
        public int insert(Object entity) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            return (int)getMethod("insert",Object.class)
                    .invoke(mapper,entity);
        }
        public int deleteById(Serializable id) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            return (int)getMethod("deleteById", Serializable.class)
                    .invoke(mapper,id);
        }
        public int deleteById(Object entity) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            return (int)getMethod("deleteById",Object.class)
                    .invoke(mapper,entity);
        }
        public int deleteByMap(Map<String,Object> columnMap) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            return (int)getMethod("deleteByMap", Map.class)
                    .invoke(mapper,columnMap);
        }
        public int delete(Wrapper<?> queryWrapper) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            return (int)getMethod("delete", Wrapper.class)
                    .invoke(mapper,queryWrapper);
        }
        public int deleteBatchIds(Collection<?> idList) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            return (int)getMethod("deleteBatchIds",Collection.class)
                    .invoke(mapper,idList);
        }
        public int updateById(Object entity) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            return (int)getMethod("updateById",Object.class)
                    .invoke(mapper,entity);
        }
        public int update(Object entity,Wrapper<?> updateWrapper) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            return (int)getMethod("update",Object.class,Wrapper.class)
                    .invoke(mapper,entity,updateWrapper);
        }
        public int update(Wrapper<?> wrapper) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            return (int)getMethod("update",Wrapper.class)
                    .invoke(mapper,wrapper);
        }
        public Object selectById(Serializable id) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            return getMethod("selectById",Serializable.class)
                    .invoke(mapper,id);
        }
        public List<?> selectBatchIds(Collection<? extends Serializable> idList) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            return (List<?>) getMethod("selectBatchIds",Collection.class)
                    .invoke(mapper,idList);
        }
        public List<?> selectByMap(Map<String,Object> columnMap) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            return (List<?>) getMethod("selectByMap",Map.class)
                    .invoke(mapper,columnMap);
        }
        public void selectByMap(Map<String,Object> columnMap, ResultHandler<?> resultHandler) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            getMethod("selectByMap",Map.class,ResultHandler.class)
                    .invoke(mapper,columnMap,resultHandler);
        }
        public Object selectOne(Wrapper<?> queryWrapper) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            return getMethod("selectOne",Wrapper.class)
                    .invoke(mapper,queryWrapper);
        }
        public Object selectOne(Wrapper<?> queryWrapper,boolean throwEx) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            return getMethod("selectOne",Wrapper.class,boolean.class)
                    .invoke(mapper,queryWrapper,throwEx);
        }
        public boolean exists(Wrapper<?> queryWrapper) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            return (boolean)getMethod("exists",Wrapper.class)
                    .invoke(mapper,queryWrapper);
        }
        public Long selectCount(Wrapper<?> queryWrapper) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            return (Long) getMethod("selectCount",Wrapper.class)
                    .invoke(mapper,queryWrapper);
        }
        public List<?> selectList(Wrapper<?> queryWrapper) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            return (List<?>) getMethod("selectList",Wrapper.class)
                    .invoke(mapper,queryWrapper);
        }
        public void selectList(Wrapper<?> queryWrapper,ResultHandler<?> resultHandler) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            getMethod("selectList",Wrapper.class,ResultHandler.class)
                    .invoke(mapper,queryWrapper,resultHandler);
        }
//        public List<Map<String,Object>> selectMaps(Wrapper<?> queryWrapper) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//            return (List<Map<String, Object>>) getMethod("selectMaps",queryWrapper.getClass())
//                    .invoke(mapper,queryWrapper);
//        }
        public void selectMaps(Wrapper<?> queryWrapper,ResultHandler<?> resultHandler) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            getMethod("selectMaps",Wrapper.class,ResultHandler.class)
                    .invoke(mapper,queryWrapper,resultHandler);
        }
        public List<?> selectObjs(Wrapper<?> queryWrapper) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            return (List<?>) getMethod("selectObjs",Wrapper.class)
                    .invoke(mapper,queryWrapper);
        }
        public IPage<?> selectPage(Page<?> page,Wrapper<?> queryWrapper) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            return (IPage<?>) getMethod("selectPage",Page.class,Wrapper.class)
                    .invoke(mapper,page,queryWrapper);
        }

//        public IPage<Map<String,Object>> selectMapsPage(Page<?> page,Wrapper<?> queryWrapper) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//            return (IPage<Map<String, Object>>) getMethod("selectMapsPage",page.getClass(),queryWrapper.getClass())
//                    .invoke(mapper,page,queryWrapper);
//        }

    }

    public class InMethod {
        private Object mapper;
        private Method method;

        /**
         * 3.调用invoke，执行方法 返回方法的运行结果
         * @param args
         * @return
         * @throws InvocationTargetException
         * @throws IllegalAccessException
         */
        public Object invoke(Object... args) throws InvocationTargetException, IllegalAccessException {
            return method.invoke(mapper, args);
        }
    }
}
