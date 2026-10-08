package com.app.server.service;

import com.app.server.base.BaseService;
import com.app.server.base.Result;
import com.app.server.entity.PhotoInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 内容接口
 */
public interface PhotoService extends BaseService {

    /**
     * 通用上传内容接口
     * @param uploadFile 内容参数
     * @return
     * @throws Exception
     */
    Result upload(MultipartFile uploadFile) throws Exception;

    /**
     * 通过地址查询内容信息
     * @param paths 文件地址
     * @return
     */
    PhotoInfo selectPhotoByPath(String paths);
}
