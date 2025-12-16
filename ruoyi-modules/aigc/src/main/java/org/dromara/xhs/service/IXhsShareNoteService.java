package org.dromara.xhs.service;

import org.dromara.xhs.domain.vo.XhsShareNoteVo;
import org.dromara.xhs.domain.bo.XhsShareNoteBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.xhs.util.extract.exception.LinkParseException;

import java.util.Collection;
import java.util.List;

/**
 * 小红书笔记链接分享Service接口
 *
 * @author ZRL
 * @date 2025-08-15
 */
public interface IXhsShareNoteService {

    /**
     * 查询小红书笔记链接分享
     *
     * @param id 主键
     * @return 小红书笔记链接分享
     */
    XhsShareNoteVo queryById(Long id);

    /**
     * 分页查询小红书笔记链接分享列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 小红书笔记链接分享分页列表
     */
    TableDataInfo<XhsShareNoteVo> queryPageList(XhsShareNoteBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的小红书笔记链接分享列表
     *
     * @param bo 查询条件
     * @return 小红书笔记链接分享列表
     */
    List<XhsShareNoteVo> queryList(XhsShareNoteBo bo);

    /**
     * 新增小红书笔记链接分享
     *
     * @param bo 小红书笔记链接分享
     * @return 是否新增成功
     */
    Boolean insertXhsShareNote(XhsShareNoteBo bo);

    /**
     * 修改小红书笔记链接分享
     *
     * @param bo 小红书笔记链接分享
     * @return 是否修改成功
     */
    Boolean updateXhsShareNote(XhsShareNoteBo bo);

    /**
     * 校验并批量删除小红书笔记链接分享信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    String importXhsShareNote(List<XhsShareNoteBo> dataList, boolean updateSupport, String operatorName);

    void batchUpdate();

    String UpdateInteractionByNoteId(String noteId);
}
