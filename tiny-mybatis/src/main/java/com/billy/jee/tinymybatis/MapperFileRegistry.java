package com.billy.jee.tinymybatis;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;


public class MapperFileRegistry {
    private static Logger LOG = Logger.getLogger(MapperFileRegistry.class);
    private Configuration configuration;
    public static final String MAPPER_FILE_SQL_ID = "id";
    public static final String MAPPER_FILE_NAMESPACE = "namespace";
    public static final String MAPPER_FILE_RESULTTYPE = "resultType";

    private final Map<Class<?>, MapperFile> mapperFiles = new HashMap<>();

    public MapperFileRegistry(Configuration configuration) {
        this.configuration = configuration;
    }


    public void addMapperFile(String mapperFileName) throws Exception {
        String path = Thread.currentThread().getContextClassLoader().getResource(mapperFileName).getPath();

        File mapperFilePath = new File(path);
        parseMapperFile(mapperFilePath);
    }

    public boolean addMapperDirectory(String mapperPath) throws Exception {
        String path = Thread.currentThread().getContextClassLoader().getResource(mapperPath).getPath();
        File fileMapperRoot = new File(path);
        File[] files = fileMapperRoot.listFiles();
        for (File file : files) {
            parseMapperFile(file);
        }

        return true;
    }

    private void parseMapperFile(File mapperFilePath) throws Exception {
        SAXReader reader = new SAXReader();
        Document doc = reader.read(mapperFilePath);
//        System.out.println(mapperFilePath);
        MapperFile mapperFile = new MapperFile();
        parseNamesapce(doc, mapperFile);
        if (hasMapperFile(mapperFile.getMapperInterface())) {
            return ;
        }
        parseSQLSegment(doc, mapperFile);
        parseSQLCommand(doc, mapperFile);
        mapperFiles.put(mapperFile.getMapperInterface(), mapperFile);
        LOG.debug("mapper file `"+mapperFilePath.getPath()+"` was parsed.");
    }

    private void parseSQLSegment(Document doc, MapperFile mapperFile) {
        Element root = doc.getRootElement();
        List<?> elements = root.elements();
        List<SQLSegment> sqlSegments = new ArrayList<>();
        for (Object object : elements) {
            Element element = (Element) object;
            String name = element.getName();
            if ("sql".equals(name)) {
                String id = element.attributeValue(MAPPER_FILE_SQL_ID);
                List<Object> content = element.content();
                SQLCommand of = SQLCommand.of(mapperFile, configuration, id, content, null);
                sqlSegments.add(new SQLSegment(id, of));
            }
        }
        mapperFile.setSqlSegment(sqlSegments);
    }

    private boolean hasMapperFile(Class<?> mapperInterface) {
        return mapperFiles.containsKey(mapperInterface);
    }

    private void parseSQLCommand(Document doc, MapperFile mapperFile) {
        Element root = doc.getRootElement();
        List<?> elements = root.elements();
        List<SQLCommand> sqlCommands = new ArrayList<>();
        for (Object object : elements) {
            Element element = (Element) object;
//            String name = element.getName();
            String id = element.attributeValue(MAPPER_FILE_SQL_ID);
            if (id == null) {
                continue;
            }
            List<?> contentList = element.content(); // TODO select | update | delete | insert
//            System.out.println(name + " = " + id + " = " + text);
            String resultType = element.attributeValue(MAPPER_FILE_RESULTTYPE);
            sqlCommands.add(SQLCommand.of(mapperFile, configuration, id, contentList, resultType));
        }
        mapperFile.setSqlCommands(sqlCommands);
    }

    private void parseNamesapce(Document doc, MapperFile mapperFile) throws Exception {
        Element root = doc.getRootElement();
//        System.out.println("root=" + root.getName());
        String namespaceValue = root.attributeValue(MAPPER_FILE_NAMESPACE);
//        System.out.print(jee.Constants.MAPPER_FILE_NAMESPACE + "=" + namespaceValue);
        mapperFile.setMapperInterface(Class.forName(namespaceValue));
    }


    public Set<Class<?>> getMapperClasses() {
        return Collections.unmodifiableSet(mapperFiles.keySet());
    }

    public Map<Class<?>, MapperFile> getMapperFiles() {
        return Collections.unmodifiableMap(mapperFiles);
    }


}
