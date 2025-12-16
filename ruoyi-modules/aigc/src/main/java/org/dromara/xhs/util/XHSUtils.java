package org.dromara.xhs.util;

import cn.hutool.core.bean.BeanDesc;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.dromara.ai.domain.XhsCookie;
import org.dromara.ai.service.IXhsCookieService;
import org.dromara.common.core.exception.ServiceException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.yulichang.toolkit.SpringContentUtils.getBean;

/**
 * @author zrl
 * @date 2025/3/18
 */
@Slf4j
public class XHSUtils {

    /**
     * 根据链接获取笔记信息
     *
     * @param originalUrl 原始链接 可以是 移动端或PC端的分享链接
     * @return 返回笔记的文章信息
     */
    public static JSONObject getNoteInfoByUrl(String originalUrl) {
        Opt<String[]> noteIdAndXsecToken = extractNoteIdAndXsecToken(originalUrl);
        if (noteIdAndXsecToken.isEmpty()) {
            throw new ServiceException("链接格式校验失败；请输入正确的链接");
        }

        // 创建OkHttpClient实例和Request对象
        String noteId = noteIdAndXsecToken.get()[0];
        String url = noteIdAndXsecToken.get()[2];
        OkHttpClient client = new OkHttpClient();

        try {

            Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36")
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("请求失败，状态码: {}", response.code());
                }

                String resData = Objects.requireNonNull(response.body()).string();
                String resJson = resData.split("__INITIAL_STATE__=")[1].split("</script>")[0].replace("undefined", "null");

                JSONObject judgeData = JSONUtil.parseObj(resJson);
                Opt<JSONObject> jsonObjectOpt = Opt.ofNullable(judgeData
                    .getJSONObject("note")
                    .getJSONObject("noteDetailMap")
                    .getJSONObject(noteId)
                    .getJSONObject("note"));
                if (jsonObjectOpt.isPresent()) {
                    return processNoteData(jsonObjectOpt.get(), noteId);
                }
            } catch (IOException e) {
                log.error("请求执行失败: {}", e.getMessage());
            }
        } catch (Exception e) {
            log.error("解析失败");
        }
        throw new ServiceException("所有Cookie尝试失败，无法获取笔记数据");
    }

    /**
     * 处理笔记数据
     */
    /**
 * 处理笔记数据
 */
    private static JSONObject processNoteData(JSONObject jsonObject, String noteId) {
        log.info("开始处理笔记数据:{}", JSONUtil.toJsonStr(jsonObject));
        JSONObject entries = new JSONObject();
        entries.set("noteId", noteId);

        String title = Opt.ofNullable(jsonObject.getStr("title")).orElse("");
        String desc = Opt.ofNullable(jsonObject.getStr("desc")).orElse("");
        String content = desc.replaceAll("\\[话题]#", "").trim();
        entries.set("title", title);
        entries.set("desc", content);

        List<String> tags = new ArrayList<>();
        Opt.ofNullable(jsonObject.getJSONArray("tagList")).ifPresent(arr -> {
            for (Object item : arr) {
                if (item instanceof JSONObject obj) {
                    String name = Opt.ofNullable(obj.getStr("name")).orElse(Opt.ofNullable(obj.getStr("tagName")).orElse(""));
                    if (StrUtil.isNotBlank(name)) {
                        tags.add(name);
                    }
                } else if (item instanceof CharSequence cs) {
                    String name = cs.toString();
                    if (StrUtil.isNotBlank(name)) {
                        tags.add(name);
                    }
                }
            }
        });
        entries.set("tags", tags);

        List<String> images = new ArrayList<>();
        Opt.ofNullable(jsonObject.getJSONArray("imageList")).ifPresent(list -> {
            for (int i = 0; i < list.size(); i++) {
                JSONObject imageObj = list.getJSONObject(i);
                String url = Opt.ofNullable(imageObj.getStr("urlDefault")).orElse("");
                if (StrUtil.isBlank(url)) {
                    url = Opt.ofNullable(imageObj.getStr("url")).orElse("");
                }
                if (StrUtil.isBlank(url)) {
                    JSONArray infoList = Opt.ofNullable(imageObj.getJSONArray("infoList")).orElse(new JSONArray());
                    String dft = "";
                    String prv = "";
                    for (int j = 0; j < infoList.size(); j++) {
                        JSONObject info = infoList.getJSONObject(j);
                        String scene = Opt.ofNullable(info.getStr("imageScene")).orElse("");
                        String u = Opt.ofNullable(info.getStr("url")).orElse("");
                        if ("WB_DFT".equalsIgnoreCase(scene)) {
                            dft = u;
                        } else if ("WB_PRV".equalsIgnoreCase(scene)) {
                            prv = u;
                        }
                    }
                    url = StrUtil.isNotBlank(dft) ? dft : prv;
                }
                if (StrUtil.isNotBlank(url)) {
                    images.add(url.replace("`", "").trim());
                }
            }
        });
        if (!images.isEmpty()) {
            entries.set("cover", images.get(0));
        }
        entries.set("imageList", images);

        String videoUrl = "";
        JSONObject video = Opt.ofNullable(jsonObject.getJSONObject("video")).orElse(null);
        if (video != null) {
            JSONObject media = Opt.ofNullable(video.getJSONObject("media")).orElse(null);
            if (media != null) {
                JSONObject stream = Opt.ofNullable(media.getJSONObject("stream")).orElse(null);
                if (stream != null) {
                    JSONArray h264 = Opt.ofNullable(stream.getJSONArray("h264")).orElse(new JSONArray());
                    JSONObject pick = null;
                    for (int i = 0; i < h264.size(); i++) {
                        JSONObject it = h264.getJSONObject(i);
                        Integer def = Opt.ofNullable(it.getInt("defaultStream")).orElse(0);
                        if (def != null && def == 1) {
                            pick = it;
                            break;
                        }
                    }
                    if (pick == null && !h264.isEmpty()) {
                        pick = h264.getJSONObject(0);
                    }
                    if (pick != null) {
                        videoUrl = Opt.ofNullable(pick.getStr("masterUrl")).orElse("");
                        if (StrUtil.isBlank(videoUrl)) {
                            JSONArray backups = Opt.ofNullable(pick.getJSONArray("backupUrls")).orElse(new JSONArray());
                            if (!backups.isEmpty()) {
                                videoUrl = backups.getStr(0);
                            }
                        }
                    }
                }
            }
        }
        if (StrUtil.isNotBlank(videoUrl)) {
            entries.set("videoUrl", videoUrl.replace("`", "").trim());
        }

        Long time = Opt.ofNullable(jsonObject.getLong("time")).orElse(0L);
        entries.set("publishTime", new Date(time));

        JSONObject user = Opt.ofNullable(jsonObject.getJSONObject("user")).orElse(new JSONObject());
        entries.set("nickname", Opt.ofNullable(user.getStr("nickname")).orElse(""));
        entries.set("avatar", Opt.ofNullable(user.getStr("avatar")).orElse(""));
        entries.set("userId", Opt.ofNullable(user.getStr("userId")).orElse(""));
        entries.set("xsecToken", Opt.ofNullable(user.getStr("xsecToken")).orElse(""));

        return entries;
    }



    /**
     * 从分享链接中提取noteId和xsec_token
     *
     * @param link 分享链接
     * @return 返回noteId和xsec_token
     */
    public static Opt<String[]> extractNoteIdAndXsecToken(String link) {
        // 正则表达式用于匹配URL
        String urlPattern = "https?://[\\w\\-./?%&=]*";

        // 编译正则表达式
        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(link);

        // 查找第一个匹配的URL
        if (matcher.find()) {
            String url = matcher.group();

            // 处理短链接情况
            url = extractActualUrl(url);

            // 解析noteId和xsec_token
            String noteId = null;
            String xsecToken = null;

            // 解析noteId
            Pattern noteIdPattern = Pattern.compile("/(?:discovery/item|explore)/([0-9a-f]+)");
            Matcher noteIdMatcher = noteIdPattern.matcher(url);
            if (noteIdMatcher.find()) {
                noteId = noteIdMatcher.group(1);
            }

            // 解析xsec_token
            Pattern xsecTokenPattern = Pattern.compile("xsec_token=([^&]+)");
            Matcher xsecTokenMatcher = xsecTokenPattern.matcher(url);
            if (xsecTokenMatcher.find()) {
                xsecToken = xsecTokenMatcher.group(1);
            }

            if (noteId != null && xsecToken != null) {
                String noteUrl = StrUtil.format(
                        "https://www.xiaohongshu.com/explore/{}?xsec_token={}&xsec_source=pc_feed&m_source=mengfanwetab",
                        noteId,
                        xsecToken);
                return Opt.of(new String[]{noteId, xsecToken, noteUrl});
            }
        }
        return Opt.empty();
    }

    /**
     * 提取短链接对应的实际URL
     *
     * @param shareLink 短链接
     * @return 返回实际URL
     */
    private static String extractActualUrl(String shareLink) {
        // 如果已经是标准的HTTP/HTTPS链接，直接返回
        if (!shareLink.contains("xhslink.com")) {
            return shareLink;
        }
        // 如果无法匹配，尝试通过网络请求解析短链接
        try {
            HttpRequest request = HttpUtil.createGet(shareLink);
            request.header(Header.USER_AGENT, "Mozilla/5.0");
            request.setFollowRedirects(true); // 启用自动重定向以解析短链接

            try (HttpResponse response = request.execute()) {
                if (response.isOk()) {
                    return request.getUrl(); // 返回最终跳转的目标URL
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("无法解析分享链接: " + e.getMessage());
        }
        throw new RuntimeException("无法从分享链接中提取有效URL");
    }
}
