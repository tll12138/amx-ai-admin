package org.dromara.ai.utils.qwen;

import cn.hutool.core.util.StrUtil;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * QwenChatUtil
 * {@code @description} 通义千问对话工具类
 * @author zrl
 * {@code @date} 2025/2/27
 */
@Component
@Slf4j
public class QwenChatUtil {

    public String callWithMessage(String content, String effect) {
        Generation gen = new Generation();
        try {
            Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content(
                    "角色定位：您是电商行业认证的商品功效检测专家，熟悉《广告法》《反不正当竞争法》及行业标准。\n" +
                        "\n" +
                        "检测流程：\n" +
                        "输入接收：\n" +
                        "商品描述文案：[用户提供的具体文本]\n" +
                        "备案功效说明：[用户提供的核准功效清单]\n" +
                        "核心检测：\n" +
                        "1. 功效匹配性分析\n" +
                        "逐条对比宣称功效与备案功效的对应关系\n" +
                        "标注\"超范围宣称\"(如美白产品宣传生发)\n" +
                        "识别暗示性功效词汇(如\"类肉毒杆菌效果\")\n" +
                        "2.广告法合规审查\n" +
                        "检测绝对化用语：\"最/第一/顶级\"等\n" +
                        "识别虚假承诺：\"100%有效/永不复发\"\n" +
                        "排查医疗宣称：\"治疗/治愈/消炎\"\n" +
                        "审查数据验证：28天见效需提供人体试验报告\n" +
                        "3. 风险等级评估\n" +
                        "■ 高危违规：涉及疾病治疗/绝对化用语\n" +
                        "■ 一般违规：夸大功效但未达医疗宣称\n" +
                        "■ 合规：宣称与备案一致且有据可循\n" +
                        "专家建议输出：\n" +
                        "违规点标注（具体条款对应）\n" +
                        "修改方案建议（替代词汇/补充材料）\n" +
                        "注意：如果合规请你在结果中将code写为0，不合规则写为1；content部分写总结内容；整个JSON必须有且只有这两个字段\n" +
                        "输出格式如下(严格确保输出为以下格式, 不需要使用markdown格式输出；不需要回复其他内容；字数尽量控制在150字内)：\n" +
                        "{  'code' : 1 , 'content': '简要说明文案是否合规; 例如“此商品宣传使用“4周*淡纹紧致、滚走细纹、28天淡纹紧致、”等词语及内容，未能提供传:该广告宣传内容的相关依据证实其真实性，涉及虚假宣传”'}"
                )
                .build();
            Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content("请分析以下文案：[" + content + "] \n" +"功效为：[" + effect + "]")
                .build();
            GenerationParam param = GenerationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey("sk-bd5121ec56134e05ab993f5ac965c8cc")
                // 此处以qwen-plus为例，可按需更换模型名称。模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                .model("qwen-plus")
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.TEXT)
                .build();
            GenerationResult call = gen.call(param);
            return extractJson(call.getOutput().getText());
        } catch (Exception e){
            log.error("调用通义千问失败", e);
            return "";
        }
    }

    /**
     * 从字符串中截取从第一个 '{' 到最后一个 '}' 之间的内容
     *
     * @param input 输入的字符串
     * @return 截取到的 JSON 字符串，如果没有找到则返回 null
     */
    public static String extractJson(String input) {
        if (StrUtil.isEmpty(input)){
            throw new RuntimeException("阿里云返回结果异常");
        }
        int startIndex = input.indexOf('{');
        int endIndex = input.lastIndexOf('}');

        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return input.substring(startIndex, endIndex + 1);
        }

        throw new RuntimeException("阿里云返回结果异常");
    }
}
