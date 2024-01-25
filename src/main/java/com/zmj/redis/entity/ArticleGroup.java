package com.zmj.redis.entity;

/**
 * @author 14864
 * @apiNote 文章分组对象
 * @date 2024/1/23 20:59
 */
public class ArticleGroup {

    public static final String A = "";

    /**
     * 分组Id
     */
    private Long groupId;
    /**
     * 分组名称
     */
    private String groupName;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}