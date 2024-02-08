# MyBatis-Plus-CRUD-Utils
适用与Spring框架下的MyBatis-Plus，利用反射简化增删改查

使用方法：
1.将SpringContextUtil.java和Crud.java放在项目中。
2.1推荐
```Java

          String MapperClassPackage = "com.example.server.mapper";
          Class<?> userCls = User.class; //假如有一个User实体
          Object res = Crud.create()
                  .mapper(userCls,MapperClassPackage)   //第一个参数为User.class,第二个参数为包名 
                  .selectList(null);
          List<User> user = (List<User>) res; //虽然有警报，但是不影响 
      //这里传入的是MyBatis-Plus中继承BaseMapper类的Mapper
      //其方法“selectList”，
      //参数，Wrapper<?> queryWrapper,这里传入的是null
```
2.2
```Java
          Object res = Crud.create()
                .mapper(entityCLs,MapperClassPackage)
                .method("selectList", Wrapper.class)
                .invoke((Object) null);
        //method传入方法名，和其参数类型，可多参数 比如 method("update",Object.class,Wrapper.class)
        //invoke是调用方法，并传入参数，这里注意，如果要传null时请同上,调用需要几个参数就给几个参数，比如invoke(user,(Object)null)
  
```
