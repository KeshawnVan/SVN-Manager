package com.star.svn.bean;

import java.util.Date;
import java.util.Map;

/**
* @Description:    java类作用描述:某个版本变动信息
* @Author:         yc
* @CreateDate:     2018/5/10 14:32
* @UpdateUser:     yc
* @UpdateDate:     2018/5/10 14:32
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
public class VersionChangePojo {
    /**
     * 唯一标识
     */
    private String UUID;
    /**
     * 作者
     */
    private String author;
    /**
     * 提交日期
     */
    private Date date;
    /**
     * 提交说明
     */
    private String message;
    /**
     * 老版本号
     */
    private long oldRevision;
    /**
     * 新版本号
     */
    private long revision;
    /**
     * 所有变动文件添加代码行数新版本相比较老版本而言
     */
    private int addSumCode;
    /**
     * 所有变动文件修改代码行数新版本相比较老版本而言
     */
    private int modifySumCode;
    /**
     * 所有变动文件删除代码行数新版本相比较老版本而言
     */
    private int deleteSumCode;
    /**
     * 变动文件
     */
    private Map<String,FileChangePojo> fileChangePojoMap;

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getOldRevision() {
        return oldRevision;
    }

    public void setOldRevision(long oldRevision) {
        this.oldRevision = oldRevision;
    }

    public long getRevision() {
        return revision;
    }

    public void setRevision(long revision) {
        this.revision = revision;
    }

    public int getAddSumCode() {
        return addSumCode;
    }

    public void setAddSumCode(int addSumCode) {
        this.addSumCode = addSumCode;
    }

    public int getModifySumCode() {
        return modifySumCode;
    }

    public void setModifySumCode(int modifySumCode) {
        this.modifySumCode = modifySumCode;
    }

    public int getDeleteSumCode() {
        return deleteSumCode;
    }

    public void setDeleteSumCode(int deleteSumCode) {
        this.deleteSumCode = deleteSumCode;
    }

    public Map<String, FileChangePojo> getFileChangePojoMap() {
        return fileChangePojoMap;
    }

    public void setFileChangePojoMap(Map<String, FileChangePojo> fileChangePojoMap) {
        this.fileChangePojoMap = fileChangePojoMap;
    }

    @Override
    public String toString() {
        return "VersionChangePojo{" +
                "author='" + author + '\'' +
                ", date=" + date +
                ", message='" + message + '\'' +
                ", oldRevision=" + oldRevision +
                ", revision=" + revision +
                ", addSumCode=" + addSumCode +
                ", modifySumCode=" + modifySumCode +
                ", deleteSumCode=" + deleteSumCode +
                ", fileChangePojoMap=" + fileChangePojoMap +
                '}';
    }
}
