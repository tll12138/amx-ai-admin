package org.dromara.ai.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则 + Apache Commons Lang 实现文本解析（标题/正文/标签）
 * 适配带前缀、非连续行、含额外内容的真实文本结构，保留正文原始换行
 */
public class RegexWithCommonsLangParser {

    // 正则：精准捕获标题/正文/标签，保留换行
    private static final Pattern TEXT_PARSE_PATTERN = Pattern.compile(
        ".*?标题：\\s*([\\s\\S]+?)\\s*\\r?\\n.*?正文：\\s*([\\s\\S]+?)\\s*(?:标签：\\s*)?(#.+?)\\s*(?=【|---|###|☆|★|$)",
        Pattern.DOTALL
    );

    /**
     * 解析文本，返回封装结果的实体类
     * @param originalText 待解析的原始文本
     * @return 解析结果（标题/正文/标签）
     */
    public static ParseResult parseText(String originalText) {
        String text = StringUtils.trimToEmpty(originalText);
        if (StringUtils.isEmpty(text)) {
            return new ParseResult("", "", "");
        }

        Matcher matcher = TEXT_PARSE_PATTERN.matcher(text);
        if (!matcher.find()) {
            return new ParseResult("", "", "");
        }

        // 标题：仅修剪首尾空白，保留特殊符号/换行外的格式
        String title = StringUtils.trimToEmpty(matcher.group(1));
        // 正文：仅统一换行符（\r\n→\n），保留原始换行和空格，仅修剪首尾
        String content = StringUtils.trimToEmpty(matcher.group(2))
            .replaceAll("\\r\\n", "\n"); // 仅统一换行符，不合并任何空格
        // 标签：移除#号，替换空格/换行为顿号
        String tagStr = StringUtils.trimToEmpty(matcher.group(3))
            .replaceAll("\\s+", " ");   // 仅把连续换行/空格转为单个空格

        return new ParseResult(title, content, tagStr);
    }

    /**
     * 解析结果封装类
     */
    public static class ParseResult {
        private final String title;    // 标题
        private final String content;  // 正文
        private final String tags;     // 标签

        public ParseResult(String title, String content, String tags) {
            this.title = title;
            this.content = content;
            this.tags = tags;
        }

        // Getter方法
        public String getTitle() { return title; }
        public String getContent() { return content; }
        public String getTags() { return tags; }

        // 重写toString，精准输出期望格式
        @Override
        public String toString() {
            return "===== 解析结果 =====\n" +
                "标题：" + title + "\n\n" +
                "正文：\n" + content + "\n\n" +
                "标签：" + tags;
        }
    }

    // 测试主方法
    public static void main(String[] args) {
        // 待解析的原始文本
        String originalText = """
            ### 标题分析与仿写：
            **原参考标题结构**： \s
            - 使用emoji和疑问句式引发好奇（“球球🥺快交出你们的圣诞装饰🎄”） \s
            - 标题包含核心关键词（圣诞装饰、氛围感）和互动感（“快交出”“围观”） \s

            **仿写标题**： \s
            **「油皮天菜！洗面奶对比测评谁懂啊😭 OC直接封神！」** \s
            （注：20字内，痛点词“油皮”+情绪词“天菜/封神”+对比测评关键词，符合爆款标题公式）

            ---

            ### 正文仿写： \s
            标题：油皮天菜！洗面奶对比测评谁懂啊😭 OC直接封神！ \s
            正文： \s
            姐妹们谁懂啊！！用了半年洗面奶都没改善的油痘肌，居然被这个小众宝藏救了😭 \s
            今天直接掏出我对比了3款洗面奶的实测结果，油皮闭眼冲的那款终于找到了！！ \s

            👉🏻 **OC祛痘洗面奶（Oxecure）** \s
            ✅ **洗感封神**：泡沫绵密到像云朵！1.5%水杨酸+金盏花电解水，洗完不紧绷反而润润的，连T区油光都哑光了！ \s
            ✅ **闭口救星**：用完一周毛孔里的脏东西都排出来了！对比之前用的红色小象儿童洗面奶，OC的卡卡度果梅萃取直接把暗沉肌提亮了两度！ \s
            ✅ **控油王者**：对比兔头妈妈的蚕丝氨基酸，OC的透明质酸组合居然做到“控油+补水”双修！现在素颜都敢怼脸拍！ \s

            👉🏻 **竞品实测吐槽**： \s
            - **mamakids洗发水**：虽然绿茶成分温和，但主打儿童用，油皮用完反而闷痘…（别问我怎么知道的😭） \s
            - **红色小象**：橄榄油成分保湿可以，但控油完全没效果，T区2小时就油到反光… \s
            - **兔头妈妈**：氨基酸温和是真，但对油痘肌的疏通力太弱了，闭口还是在原地踏步😭 \s

            👉🏻 **实测对比图**： \s
            （插入前后对比图/成分对比表格） \s
            洗完OC的毛孔对比红色小象，真的像开了美颜滤镜！！现在办公室姐妹都在问我用的什么，偷偷说：小红书搜“Oxecure OC洗面奶”就能找到～ \s

            评论区告诉我，你们的洗面奶都是什么牌子呀？油皮姐妹速来抄作业！！ \s

            标签：#祛痘洗面奶推荐 #控油洗面奶测评 #洗面奶对比 #油皮护肤 #Oxecure #小众洗面奶 #护肤干货 \s

            ---

            ### 核心策略解析： \s
            1. **痛点暴击+情绪共鸣**：以“油皮痛苦”切入，用“谁懂啊”“救命”等口语化情绪词引发共情。 \s
            2. **对比强化优势**：将竞品定位为“儿童适用”或“功能单一”，突出OC的“成人油痘肌针对性+成分科技感”。 \s
            3. **场景化代入**：通过“办公室姐妹追问”“素颜怼脸拍”等场景，增强真实体验感。 \s
            4. **数据化对比**：用“一周疏通闭口”“T区哑光”等具体效果，替代模糊描述。 \s
            5. **标签精准覆盖**：涵盖“测评/对比/功效/品牌”关键词，适配小红书算法推荐逻辑。 \s

            （注：正文需补充产品实拍图、对比图，增强视觉冲击力）
                """;

        // 解析文本并打印结果
        ParseResult result = RegexWithCommonsLangParser.parseText(originalText);
        System.out.println(result);
    }
}
