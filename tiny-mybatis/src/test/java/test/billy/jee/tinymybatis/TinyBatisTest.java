package test.billy.jee.tinymybatis;

import com.billy.jee.tinymybatis.Configuration;
import org.junit.Test;
import test.billy.jee.tinymybatis.dao.DemoDAO;
import test.billy.jee.tinymybatis.entity.DemoEntity;

import java.util.List;

/**
 * by vt.zd
 * on 2017-03-20 14:05
 */
public class TinyBatisTest {


    /**
     * 把接口放进Configuration里，然后取出其代理类，并进行调用，结果返回其sql语句，暂不进行数据库操作
     */
    @Test
    public void testConfiguration() {
        Configuration config = new Configuration();
        config.addMapper(DemoDAO.class);

        DemoDAO demoDAO = config.getMapper(DemoDAO.class);
        System.out.println(demoDAO.getClass().getName());
        // System.out.println(demoDAO.hello());
    }


    /**
     * 根据*Mapper.xml来把接口及其配置文件放进Config里，并进行调用，结果返回其sql语句，暂不进行数据库操作
     *
     * @throws Exception
     */
    @Test
    public void testMapperFileRegistry() throws Exception {
        Configuration config = new Configuration();
        config.addMapperFile("mapper/DemoMapper.xml");

        DemoDAO demoDAO = config.getMapper(DemoDAO.class);
        System.out.println(demoDAO.hello());
    }


    /**
     * 根据*Mapper.xml来把接口及其配置文件放进Config里，并进行参数(1个)调用，结果返回其sql语句，暂不进行数据库操作
     * 暂不进行数据库操作
     *
     * @throws Exception
     */
    @Test
    public void testGetById() throws Exception {
        Configuration config = new Configuration();
        config.addMapperFile("mapper/DemoMapper.xml");

        DemoDAO demoDAO = config.getMapper(DemoDAO.class);
        System.out.println(demoDAO.getById(13));
    }



    /**
     * 根据*Mapper.xml来把接口及其配置文件放进Config里，并进行参数(对象)调用，结果返回其sql语句，暂不进行数据库操作
     *
     *
     * @throws Exception
     */
    @Test
    public void getByEntity() throws Exception {
        Configuration config = new Configuration();
        config.addMapperFile("mapper/DemoMapper.xml");
        config.addEntity(DemoEntity.class);

        DemoDAO demoDAO = config.getMapper(DemoDAO.class);
        DemoEntity demoEntity = new DemoEntity();
        demoEntity.setId(2);
        demoEntity.setName("haha");
        demoDAO.getByEntity(demoEntity).forEach(
            demoEntityTmp -> {
                System.out.println(demoEntityTmp);
            }
        );
    }

    /**
     * 使用alias替代实体名，条件全部进行了拼接，进行了db操作
     *
     * @throws Exception
     */
    @Test
    public void addAlias() throws Exception {
        Configuration config = new Configuration();
        config.addAlias(DemoEntity.class); // alias should be before add mapperFile
        config.addMapperFile("mapper/DemoMapper.xml");

        DemoDAO demoDAO = config.getMapper(DemoDAO.class);
        DemoEntity demoEntity = new DemoEntity();
        demoEntity.setId(2);
        demoEntity.setName("haha");
        demoDAO.getByEntity(demoEntity).forEach(
                demoEntityTmp -> {
                    System.out.println(demoEntityTmp);
                }
        );
    }

    /**
     * <p>使用aliasPackage</p>
     *
     * 以前使用
     * <pre>
     *    config.addAlias(test.billy.jee.tinymybatis.entity.DemoEntity.class);
     *    config.addAlias(test.billy.jee.tinymybatis.entity.Demo2Entity.class);
     *    config.addAlias(test.billy.jee.tinymybatis.entity.Demo3Entity.class);
     *    config.addAlias(test.billy.jee.tinymybatis.entity.po.DemoPO.class);
     * </pre>
     * 现在改用
     * <pre>
     *     config.addAliasPackage("test.billy.jee.tinymybatis.entity");
     * </pre>
     * 来替代，这样就可以为指定包下的所有类添加别名
     *
     * <p><b>支持多层目录</b></p>
     *
     * @throws Exception
     */
    @Test
    public void addAliasPackage() throws Exception {
        Configuration config = new Configuration();
        config.addAliasPackage("test.billy.jee.tinymybatis.entity");
        config.addMapperFile("mapper/DemoMapper.xml");

        DemoDAO demoDAO = config.getMapper(DemoDAO.class);
        DemoEntity demoEntity = new DemoEntity();
        demoEntity.setId(2);
        demoEntity.setName("haha");
        demoDAO.getByEntity(demoEntity).forEach(
                demoEntityTmp -> {
                    System.out.println(demoEntityTmp);
                }
        );
    }

    /**
     * <p>使用MapperDirectory</p>
     *
     * 以前使用
     * <pre>
     *    config.addMapperFile("mapper/DemoMapper.xml");
     *    config.addMapperFile("mapper/Demo2Mapper.xml");
     *    config.addMapperFile("mapper/Demo3Mapper.xml");
     * </pre>
     * 现在改用
     * <pre>
     *     config.addMapperDirectory("mapper");
     * </pre>
     * 来替代。
     *
     * TODO 暂不支持多层目录
     *
     * @throws Exception
     */
    @Test
    public void addMapperDirectory() throws Exception {
        Configuration config = new Configuration();
        config.addAliasPackage("test.billy.jee.tinymybatis.entity");
//        config.addMapperFile("mapper/DemoMapper.xml");
//        config.addMapperFile("mapper/Demo2Mapper.xml");
//        config.addMapperFile("mapper/Demo3Mapper.xml");
        config.addMapperDirectory("mapper");

        DemoDAO demoDAO = config.getMapper(DemoDAO.class);
        DemoEntity demoEntity = new DemoEntity();
        demoEntity.setId(2);
        demoEntity.setName("haha");
        demoDAO.getByEntity(demoEntity).forEach(
                demoEntityTmp -> {
                    System.out.println(demoEntityTmp);
                }
        );
    }



    /**
     * 测试使用<if></if>指令 进行数据库操作
     *
     * @throws Exception
     */
    @Test
    public void testGetWithIf() throws Exception {
        Configuration config = new Configuration();
        config.addAliasPackage("test.billy.jee.tinymybatis.entity");
        config.addMapperDirectory("mapper");

        DemoDAO demoDAO = config.getMapper(DemoDAO.class);
        DemoEntity demoEntity = new DemoEntity();
        demoEntity.setId(1);
        demoEntity.setType(1);
        List<DemoEntity> byEntityWithIf = demoDAO.getByEntityWithIf(demoEntity);
        if (byEntityWithIf != null) {
            for (DemoEntity entity : byEntityWithIf) {
                System.out.println(entity);
            }
        }
    }


    /**
     * 测试使用Include
     * @throws Exception
     */
    @Test
    public void testGetByIdWithInclude() throws Exception {
        Configuration config = new Configuration();
        config.addAliasPackage("test.billy.jee.tinymybatis.entity");
        config.addMapperDirectory("mapper");

        DemoDAO demoDAO = config.getMapper(DemoDAO.class);
        List<DemoEntity> byIdWithInclude = demoDAO.getByIdWithInclude(1);
        if (byIdWithInclude != null) {
            for (DemoEntity entity : byIdWithInclude) {
                System.out.println(entity);
            }
        }
    }


    /**
     * 使用 `Thread.currentThread().getContextClassLoader();`来替代this.getClass().getClassLoader();
     *
     * @throws Exception
     */
    @Test
    public void testClassloader() throws Exception {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        String path = contextClassLoader.getResource("mapper").getPath();
        System.out.println(path);
        //DBPool.getInstance(); //

        ClassLoader classLoader = this.getClass().getClassLoader();
        String mapper = classLoader.getResource("mapper").getPath();

        System.out.println(mapper);
    }
}
