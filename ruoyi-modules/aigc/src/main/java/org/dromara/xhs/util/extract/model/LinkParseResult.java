package org.dromara.xhs.util.extract.model;

import lombok.Getter;


/**
 * 解析结果对象（建造者模式）
 */
@Getter
public class LinkParseResult {
    // Getter methods
    private final PlatformEnum platform;
    private final String contentId;
    private final LinkType linkType;
    private final String url;

    private LinkParseResult(Builder builder) {
        this.platform = builder.platform;
        this.contentId = builder.contentId;
        this.linkType = builder.linkType;
        this.url =  builder.url;
    }

    public static Builder builder() {
        return new Builder();
    }

    public enum LinkType {
        DIRECT,
        SHARE
    }



    public static class Builder {
        private PlatformEnum platform;
        private String contentId;
        private LinkType linkType;

        private String url;

        public Builder platform(PlatformEnum platform) {
            this.platform = platform;
            return this;
        }

        public Builder contentId(String contentId) {
            this.contentId = contentId;
            return this;
        }

        public Builder linkType(LinkType linkType) {
            this.linkType = linkType;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public LinkParseResult build() {
            return new LinkParseResult(this);
        }
    }
}
