package com.app.server.entity;

import lombok.Data;

import java.util.Date;

/**
 * 用户收藏表
 */
@Data
public class UserCollection {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 内容id
     */
    private Integer photoId;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 收藏时间
     */
    private Date dateTime;

    /**
     * new方法
     * @param photoId 内容id
     * @param userId 用户id
     * @param dateTime 收藏时间
     */
    public UserCollection(Integer photoId,Integer userId,Date dateTime){
        this.photoId=photoId;
        this.userId=userId;
        this.dateTime=dateTime;
    }
}
