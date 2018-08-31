package test.billy.jee.tinymybatis.parsing;

import com.billy.jee.tinymybatis.parser.PropertyParser;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * by vt.zd
 * on 2017-03-21 09:42
 */
public class PropertyParserTest {

    @Test
    public void testParsing() {
        Map<String, Object> p = new HashMap<>();
        p.put("id", 3);
        System.out.println(p);

        String parse$ = PropertyParser.parse$("select * from t where id=${id}", p);
        System.out.println(parse$);
    }


}
