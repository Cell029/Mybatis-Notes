package com.cell.param.utils;

import org.apache.ibatis.javassist.CannotCompileException;
import org.apache.ibatis.javassist.ClassPool;
import org.apache.ibatis.javassist.CtClass;
import org.apache.ibatis.javassist.CtMethod;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class GenerateDaoProxy {
    // 动态生成 DAO 的实现类,并将实现类的对象创建出来然后返回
    public static Object getMapper(SqlSession sqlSession, Class daoInterface) { // 传入 SqlSession 和接口对象
        // 类池
        ClassPool pool = ClassPool.getDefault();
        // 生成代理类(com.cell.bank.dao.AccountDao -> com.cell.bank.dao.impl.AccountDaoImpl)
        CtClass ctClass = pool.makeClass(daoInterface.getPackageName() + ".impl." + daoInterface.getSimpleName() + "Impl");
        // 制造接口
        CtClass ctInterface = pool.makeClass(daoInterface.getName());
        // 代理类实现接口
        ctClass.addInterface(ctInterface);
        // 获取接口中的所有方法
        Method[] methods = daoInterface.getDeclaredMethods();
        Arrays.stream(methods).forEach(method -> {
            // 例如:Account selectByActno(String actno);然后把这个方法拼接成public Account selectByActno(String actno) {...}
            // 拼接方法的签名(返回值类型)
            StringBuilder methodStr = new StringBuilder();
            String returnTypeName = method.getReturnType().getName();
            methodStr.append(returnTypeName);
            methodStr.append(" ");
            String methodName = method.getName();
            methodStr.append(methodName);
            methodStr.append("(");
            // 拼接方法的形式参数列表
            // 获取所有参数
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                // 获取参数的类型名
                methodStr.append(parameterTypes[i].getName());
                methodStr.append(" arg");
                methodStr.append(i);
                if (i != parameterTypes.length - 1) {
                    methodStr.append(",");
                }
            }
            methodStr.append("){");
            // 方法体当中的代码
            // 获取 sqlId（这里非常重要： namespace 必须是接口的全限定接口名，否则无法通过反射定位到具体的 namespace,就不能获取到 sqlId）
            // 因为 sqlId 是程序员写的,所以开发者不知道具体是什么,所以就明确规定必须和接口中的方法名一致,通过获取方法名来间接获取 sqlId
            String sqlId = daoInterface.getName() + "." + methodName;
            // 需要知道是什么类型的 sql 语句,所以通过反射获取到 mybatis-config.xml 文件中的标签,然后定位到 sqlMapper.xml 文件中的 sql 类型的标签,例如 <select>
            String sqlCommondTypeName = sqlSession.getConfiguration().getMappedStatement(sqlId).getSqlCommandType().name();
            methodStr.append("org.apache.ibatis.session.SqlSession sqlSession = com.cell.bank.utils.SqlSessionUtil.openSession();");
            if ("SELECT".equals(sqlCommondTypeName)) {
                // 具体拼接哪个方法取决于 sqlMapper.xml 文件中的那个 sql 语句的 id
                methodStr.append("Object obj = sqlSession.selectOne(\"" + sqlId + "\", arg0);"); // 上面拼接形参时使用的是 arg + i
                methodStr.append("return (" + returnTypeName + ")obj;"); // 强转成当前反射的方法的返回值类型
            } else if ("UPDATE".equals(sqlCommondTypeName)) {
                methodStr.append("int count = sqlSession.update(\"" + sqlId + "\", arg0);");
                methodStr.append("return count;");
            }
            methodStr.append("}");
            System.out.println(methodStr);
            try {
                // 创建CtMethod对象
                CtMethod ctMethod = CtMethod.make(methodStr.toString(), ctClass);
                ctMethod.setModifiers(Modifier.PUBLIC);
                // 将方法添加到类
                ctClass.addMethod(ctMethod);
            } catch (CannotCompileException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            // 创建代理对象
            Class<?> aClass = ctClass.toClass();
            Constructor<?> defaultCon = aClass.getDeclaredConstructor();
            Object o = defaultCon.newInstance();
            return o;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
