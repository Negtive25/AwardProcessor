package org.com.code.certificateProcessor.util;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.web.client.RestTemplate;

/**
 * OSS 动态压缩 URL 工具类
 * 输入原始 URL，输出按比例压缩后的 URL
 */
public class OssImageCompressor {

    private static final RestTemplate restTemplate = new RestTemplate();

    /**
     * 利用阿里云 OSS 图片处理（按需压缩）
     *
     * 阿里云的 OSS 原生支持「访问时自动缩放」，
     * 只需要在原图 URL 后加上一个参数即可：
     *
     * 示例
     * https://your-bucket.oss-cn-hangzhou.aliyuncs.com/images/photo.jpg
     * ?x-oss-process=image/resize,w_1024/quality,q_90/format,png
     *
     * 解释：
     * resize,w_1024：宽度调整为 1024 像素，高度按比例缩放；
     * quality,q_90：JPEG压缩质量为 90（可调，80~90 通常肉眼无差别）；
     * format,png：格式调整为 PNG。
     *
     * OSS 自动在服务端完成压缩，无需你下载、再上传。
     * 模型拿到的这张图，就是压缩过的版本。
     */
    public static String getAdaptiveCompressedUrl(String originalUrl) {
        try {
            JSONObject imageInfo = getImageInfo(originalUrl);
            if (imageInfo == null) {
                // 获取失败，返回原图
                return originalUrl;
            }

            int width = imageInfo.getJSONObject("ImageWidth").getIntValue("value");
            int height = imageInfo.getJSONObject("ImageHeight").getIntValue("value");

            int maxSide = Math.max(width, height);
            long pixels = (long) width * height;

            String process;

            // 判断是否压缩
            if (maxSide <= 1000 || pixels <= 1_000_000) {
                // 小图，直接返回原图
                return originalUrl;
            }

            // 中等分辨率图片
            else if (maxSide <= 2000) {
                // 压缩到1200宽度，保持质量较高
                process = "image/resize,w_1200/quality,q_90";
            }

            // 高清大图
            else if (maxSide <= 4000) {
                // 压缩到1024，质量85
                process = "image/resize,w_1024/quality,q_85";
            }

            // 超大图（扫描件、单反类）
            else {
                // 强压缩
                process = "image/resize,w_800/quality,q_80";
            }

            // 拼接压缩参数
            if (originalUrl.contains("?")) {
                return originalUrl + "&x-oss-process=" + process;
            } else {
                return originalUrl + "?x-oss-process=" + process;
            }

        } catch (Exception e) {
            // 出错则回退原图
            return originalUrl;
        }
    }

    /**
     * 调用 OSS 的图片信息接口，返回 JSON
     * 不会下载整张图片，只获取元信息（宽高等）
     */
    private static JSONObject getImageInfo(String url) {
        try {
            /**
             * 情况 1：URL 没有参数
             * https://your-bucket.oss-cn-hangzhou.aliyuncs.com/photo.jpg
             *
             * 直接加 ?x-oss-process=image/info
             * 最终：
             * https://your-bucket.oss-cn-hangzhou.aliyuncs.com/photo.jpg?x-oss-process=image/info
             *
             * 情况 2：URL 已经有参数
             * https://your-bucket.oss-cn-hangzhou.aliyuncs.com/photo.jpg?token=abc123
             *
             * 再加参数时要用 &
             * 最终：
             *
             * https://your-bucket.oss-cn-hangzhou.aliyuncs.com/photo.jpg?token=abc123&x-oss-process=image/info
             */
            String infoUrl = url.contains("?")
                    ? url + "&x-oss-process=image/info"
                    : url + "?x-oss-process=image/info";

            String response = restTemplate.getForObject(infoUrl, String.class);
            return JSONObject.parseObject(response);
        } catch (Exception e) {
            return null;
        }
    }
}
