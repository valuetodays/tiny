package test.billy.jee.tinymybatis.proxy;

import org.junit.Test;

/**
 * by vt.zd
 * on 2017-03-20 10:36
 */
public class JdkDynamicProxyTest {

    @Test
    public void testProxy() {
        IDemo demo = new DemoImpl();
        MainInvocationHandler mainInvocationHandler = new MainInvocationHandler(demo);
        IDemo proxy = (IDemo)mainInvocationHandler.getProxy();
        System.out.println(proxy.getClass().getName());
        System.out.println(proxy.hello("Billy"));
    }

}
