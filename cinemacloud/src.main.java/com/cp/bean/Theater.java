package com.cp.bean;

import java.util.Date;

/**
 * Created by john on 2017/3/10.
 */
public class Theater {

    private Integer theaterid;
    private String theaternum;
    private String theatername;
    private String theaterphone;

    private String theateraddress;
    private String description;
    private Date createtime;
    private Date modifytime;
    private Integer status;

    public Integer getTheaterid() {
        return theaterid;
    }

    public void setTheaterid(Integer theaterid) {
        this.theaterid = theaterid;
    }

    public String getTheaternum() {
        return theaternum;
    }

    public void setTheaternum(String theaternum) {
        this.theaternum = theaternum;
    }

    public String getTheatername() {
        return theatername;
    }

    public void setTheatername(String theatername) {
        this.theatername = theatername;
    }

    public String getTheaterphone() {
        return theaterphone;
    }

    public void setTheaterphone(String theaterphone) {
        this.theaterphone = theaterphone;
    }

    public String getTheateraddress() {
        return theateraddress;
    }

    public void setTheateraddress(String theateraddress) {
        this.theateraddress = theateraddress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getModifytime() {
        return modifytime;
    }

    public void setModifytime(Date modifytime) {
        this.modifytime = modifytime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Theater{" +
                "theaterid=" + theaterid +
                ", theaternum='" + theaternum + '\'' +
                ", theatername='" + theatername + '\'' +
                ", theaterphone='" + theaterphone + '\'' +
                ", theateraddress='" + theateraddress + '\'' +
                ", description='" + description + '\'' +
                ", createtime=" + createtime +
                ", modifytime=" + modifytime +
                ", status=" + status +
                '}';
    }
}
