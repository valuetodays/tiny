package test.billy.jee.tinymybatis.entity;

/**
 *
 *
 * @author liulei
 * @date 2017-03-30 15:25
 */
public class DemoEntity {
    private Integer id;
    private String name;
    private String title;
    private int type;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "DemoEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                '}';
    }
}
