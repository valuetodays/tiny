package test.billy.jee.tinymybatis.proxy;

/**
 * by vt.zd
 * on 2017-03-20 10:34
 */
public class DemoImpl implements IDemo {

    @Override
    public String hello(String name) {
        return "hello, " + name;
    }
}
