package org.dromara.ai.service;

import org.dromara.ai.domain.AiProductKeywords;
import org.dromara.ai.domain.vo.AiProductKeywordsVo;
import org.dromara.ai.domain.bo.AiProductKeywordsBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 产品-关键词关系Service接口
 *
 * @author ZRL
 * @date 2025-03-26
 */
public interface IAiProductKeywordsService {

    /**
     * 查询产品-关键词关系
     *
     * @param productId 主键
     * @return 产品-关键词关系
     */
    List<AiProductKeywords> queryByProductId(String productId);


    /**
     * 新增产品-关键词关系
     *
     * @param bo 产品-关键词关系
     * @return 是否新增成功
     */
    Boolean insertAiProductKeywords(AiProductKeywordsBo bo);

    /**
     * 修改产品-关键词关系
     *
     * @param bo 产品-关键词关系
     * @return 是否修改成功
     */
    Boolean updateAiProductKeywords(AiProductKeywordsBo bo);

    /**
     * 校验并批量删除产品-关键词关系信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

    /**
     * 删除某个产品所有的关键词关系信息
     *
     * @param productId  待删除的主键
     * @return 是否删除成功
     */
    Boolean deleteByProductId(String productId);

    Boolean deleteKeyword(AiProductKeywordsBo bo);

}
