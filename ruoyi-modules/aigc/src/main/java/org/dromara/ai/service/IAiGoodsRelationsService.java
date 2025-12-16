package org.dromara.ai.service;

import org.dromara.ai.domain.vo.AiGoodsRelationsVo;
import org.dromara.ai.domain.bo.AiGoodsRelationsBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 商品竞品关系Service接口
 *
 * @author ZRL
 * @date 2025-03-25
 */
public interface IAiGoodsRelationsService {

    /**
     * 根据商品id查询竞品id列表
     * @param productId 商品ID
     * @return 竞品ID列表
     */
    List<String> queryRelationsByProductId(String productId);

    /**
     * 根据竞品id查询商品id列表
     * @param competitorId 竞品ID
     * @return 商品ID列表
     */
    List<String> queryRelationsByCompetitorId(String competitorId);


    /**
     * 批量绑定商品竞品关系
     * @param competitorId 竞品ID
     * @param productIds 商品id列表
     * @return 是否批量绑定成功
     */
    Boolean batchBindAiGoodsRelations(String competitorId, List<String> productIds);

    /**
     * 校验并批量删除商品竞品关系信息
     *
     * @param competitorId  待删除的竞品ID
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByCompetitorId(String competitorId, Boolean isValid);

    /**
     * 批量删除商品竞品关系信息
     * @param competitorIds 竞品ID列表
     * @param isValid 是否校验
     * @return 是否删除成功
     */
    Boolean batchDeleteWithValidByCompetitorIds(List<String> competitorIds, Boolean isValid);




    /**
     * 更新商品竞品关系
     * @param competitorId 竞品ID
     * @param productIds 商品ID列表
     * @return 是否更新成功
     */
    Boolean updateAiGoodsRelations(String competitorId, List<String> productIds);

}
