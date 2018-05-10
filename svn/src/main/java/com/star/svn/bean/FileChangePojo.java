package com.star.svn.bean;
/**
* @Description:    java类作用描述:用来记录两个版本比较的文件变动信息
* @Author:         yc
* @CreateDate:     2018/5/10 13:50
* @UpdateUser:     yc
* @UpdateDate:     2018/5/10 13:50
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
public class FileChangePojo {
    /**
     * 唯一标志
     */
    private String UUID;
    /**
     * 变动文件地址
     */
    private String path;
    /**
     * 文件类型： TYPE_ADDED = 'A' 新增文件;TYPE_DELETED = 'D' 删除文件;TYPE_MODIFIED = 'M' 修改文件;TYPE_REPLACED = 'R' 替换文件;
     */
    private char type;
    /**
     * 添加代码行数新版本相比较老版本而言
     */
    private int addCode;
    /**
     * 修改代码行数新版本相比较老版本而言
     */
    private int modifyCode;
    /**
     * 删除代码行数新版本相比较老版本而言
     */
    private int deleteCode;

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public int getAddCode() {
        return addCode;
    }

    public void setAddCode(int addCode) {
        this.addCode = addCode;
    }

    public int getModifyCode() {
        return modifyCode;
    }

    public void setModifyCode(int modifyCode) {
        this.modifyCode = modifyCode;
    }

    public int getDeleteCode() {
        return deleteCode;
    }

    public void setDeleteCode(int deleteCode) {
        this.deleteCode = deleteCode;
    }

    @Override
    public String toString() {
        return "FileChangePojo{" +
                "  path='" + path + '\'' +
                ", type=" + type +
                ", addCode=" + addCode +
                ", modifyCode=" + modifyCode +
                ", deleteCode=" + deleteCode +
                '}';
    }
}
