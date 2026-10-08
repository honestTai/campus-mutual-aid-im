package com.app.server.api;

import com.app.server.base.Page;
import com.app.server.base.Result;
import com.app.server.entity.PhotoInfo;
import com.app.server.service.PostsListService;
import com.app.server.vto.PostListVto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 最新内容API
 */
@RestController
@CrossOrigin
public class PostsListController {

    @Autowired
    private PostsListService postsListService;


    /**
     * 最新内容分页查询
     * @param postListVto 查询参数继承Page
     * @return
     */
    @PostMapping("list")
    public Result postList(@RequestBody PostListVto postListVto) {
        return postsListService.POSTS_LIST_PAGE_INFO(postListVto);
    }

    /**
     * 后台接口，内容列表查看
     */
    @PostMapping("photoPage")
    public Result page(@RequestBody Page page){
        return postsListService.selectByTapAndLikeTitle(page);
    }

    /**
     * 后台接口
     * 批量删除内容
     */
    @PostMapping("/deletePhoto")
    public Result deletePhoto(@RequestBody Page page){
        return postsListService.deletePhotoByIdList(page.getIdList());
    }

    /**
     * 后台接口更新内容
     */
    @PostMapping("/updatePhoto")
    public Result updatePhoto(@RequestBody PhotoInfo photoUploadByTag){
        return postsListService.updatePhotoInfoById(photoUploadByTag);
    }
}
