package com.app.server.vto;

import com.app.server.base.Page;
import lombok.Data;

/**
 * 用户相关的参数,集成分页实体
 */
@Data
public class User extends Page {

    /**
     * 查询type(0上传的内容1收藏的内容)
     */
    private Integer viewType;

    /**
     * 内容id
     */
    private Integer photoId;

    /**
     * 内容path
     */
    private String filePath;
}
