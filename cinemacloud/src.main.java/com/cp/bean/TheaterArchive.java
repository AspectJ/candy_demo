package com.cp.bean;

/**
 * Created by john on 2017/3/10.
 */
public class TheaterArchive {
    private Integer id;
    private Integer theaterid;
    private Integer archiveid;

    private  Integer value;
    private  String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTheaterid() {
        return theaterid;
    }

    public void setTheaterid(Integer theaterid) {
        this.theaterid = theaterid;
    }

    public Integer getArchiveid() {
        return archiveid;
    }

    public void setArchiveid(Integer archiveid) {
        this.archiveid = archiveid;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TheaterArchive{" +
                "id=" + id +
                ", theaterid=" + theaterid +
                ", archiveid=" + archiveid +
                ", value=" + value +
                ", content='" + content + '\'' +
                '}';
    }
}
