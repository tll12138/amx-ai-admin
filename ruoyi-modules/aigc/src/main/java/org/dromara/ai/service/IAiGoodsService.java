package org.dromara.ai.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.dromara.ai.domain.vo.AiGoodsVo;
import org.dromara.ai.domain.bo.AiGoodsBo;
import org.dromara.ai.domain.vo.AiProductKeywordsVo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 产品信息Service接口
 *
 * @author ZRL
 * @date 2025-03-24
 */
public interface IAiGoodsService {

    /**
     * 查询产品信息
     *
     * @param id 主键
     * @return 产品信息
     */
    AiGoodsVo queryById(String id);

    /**
     * 分页查询产品信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 产品信息分页列表
     */
    TableDataInfo<AiGoodsVo> queryPageList(AiGoodsBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的产品信息列表
     *
     * @param bo 查询条件
     * @return 产品信息列表
     */
    List<AiGoodsVo> queryList(AiGoodsBo bo);

    /**
     * 新增产品信息
     *
     * @param bo 产品信息
     * @return 是否新增成功
     */
    Boolean insertAiGoods(AiGoodsBo bo);

    /**
     * 修改产品信息
     *
     * @param bo 产品信息
     * @return 是否修改成功
     */
    Boolean updateAiGoods(AiGoodsBo bo);

    /**
     * 校验并批量删除产品信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);


    /**
     * 批量导入产品信息数据
     * @param aiGoodsList 产品信息列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    String importAiGoods(List<AiGoodsVo> aiGoodsList, boolean updateSupport, String operatorName);

    JSONArray getSelfGoods();

    String importAiGoodsKeywords(List<AiProductKeywordsVo> dataList, boolean updateSupport, String operatorName);

    JSONArray getCompetitorOptions(String id);
}
