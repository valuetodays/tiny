package test.billy.jee.tinymybatis.dao;

import com.billy.jee.tinymybatis.Param;
import test.billy.jee.tinymybatis.entity.DemoEntity;

import java.util.List;

/**
 * by vt.zd
 * on 2017-03-20 09:39
 */
public interface DemoDAO {
    String hello();
    String getById(@Param("id") Integer id);

    List<DemoEntity> getByEntity(DemoEntity demoEntity);
    List<DemoEntity> getByEntityWithIf(DemoEntity demoEntity);

    List<DemoEntity> getByIdWithInclude(@Param("id") Integer id);
}
