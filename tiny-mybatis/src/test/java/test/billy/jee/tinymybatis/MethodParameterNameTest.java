package test.billy.jee.tinymybatis;

import org.junit.Test;
import test.billy.jee.tinymybatis.dao.DemoDAO;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * TODO(这里用一句话描述这个类的作用)
 *
 * @author liulei
 * @date 2017-03-31 09:52
 */
public class MethodParameterNameTest {

    @Test
    public void test() {
        Method method = null;
        Class<?> c = DemoDAO.class;
        Method[] declaredMethods = c.getDeclaredMethods();
        for (int i = 0; i < declaredMethods.length; i++) {
            Method declaredMethod = declaredMethods[i];
            String name = declaredMethod.getName();
//            System.out.println(name);
            if ("getById".equals(name)) {
                method = declaredMethod;
                break;
            }
        }


//        System.out.println(method.getName());
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        for (int i = 0; i < genericParameterTypes.length; i++) {
            System.out.println(genericParameterTypes[i].getTypeName());
        }

        TypeVariable<Method>[] typeParameters = method.getTypeParameters();
        for (int i = 0; i < typeParameters.length; i++) {
            TypeVariable<Method> typeParameter = typeParameters[i];
            String name = typeParameter.getName();
            System.out.println(name);
        }

    }

}
