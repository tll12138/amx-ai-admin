package org.dromara.ai.service;

import org.dromara.ai.base.bo.AiBaseBo;
import org.dromara.ai.domain.vo.AiCommentTaskVo;
import org.dromara.ai.domain.bo.AiCommentTaskBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;

/**
 * AI评论生成Service接口
 *
 * @author ZRL
 * @date 2025-05-07
 */
public interface IAiCommentTaskService {

    /**
     * 查询AI评论生成
     *
     * @param id 主键
     * @return AI评论生成
     */
    AiCommentTaskVo queryById(String id);

    /**
     * 分页查询AI评论生成列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return AI评论生成分页列表
     */
    TableDataInfo<AiCommentTaskVo> queryPageList(AiCommentTaskBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的AI评论生成列表
     *
     * @param bo 查询条件
     * @return AI评论生成列表
     */
    List<AiCommentTaskVo> queryList(AiCommentTaskBo bo);

    /**
     * 新增AI评论生成
     *
     * @param bo AI评论生成
     * @return 是否新增成功
     */
    String insertAiCommentTask(AiCommentTaskBo bo);

    /**
     * 修改AI评论生成
     *
     * @param bo AI评论生成
     * @return 是否修改成功
     */
    Boolean updateAiCommentTask(AiCommentTaskBo bo);

    /**
     * 校验并批量删除AI评论生成信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

//    Flux<ServerSentEvent<String>> chat(AiCommentTaskBo aiCommentTaskBo);

}
