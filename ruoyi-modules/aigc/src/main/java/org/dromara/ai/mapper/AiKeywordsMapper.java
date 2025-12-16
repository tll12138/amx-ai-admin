package org.dromara.ai.mapper;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import org.dromara.ai.domain.AiKeywords;
import org.dromara.ai.domain.vo.AiKeywordsVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.common.mybatis.core.mapper.SuperMapper;

import java.util.List;

/**
 * 关键词Mapper接口
 *
 * @author ZRL
 * @date 2025-04-24
 */
public interface AiKeywordsMapper extends SuperMapper<AiKeywords, AiKeywordsVo> {

}
