package org.dromara.xhs.service;

import org.dromara.xhs.domain.bo.XhsShareNoteBo;
import org.dromara.xhs.domain.vo.common.TransformContentReqVO;
import org.dromara.xhs.domain.vo.common.TransformContentRespVO;
import org.dromara.xhs.domain.vo.common.TransformImageReqVO;

import java.util.List;

/**
 * @author zrl
 * @date 2025/7/30
 */
public interface CommonService {
    List<String> transformImages(TransformImageReqVO reqVO);

    TransformContentRespVO generateContent(TransformContentReqVO reqVO);

    Boolean addNote(XhsShareNoteBo shareNoteBo);
}
