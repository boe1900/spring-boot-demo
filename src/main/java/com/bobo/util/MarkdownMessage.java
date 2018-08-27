package com.bobo.util;



/**
 * <p>
 *
 * </p>
 *
 * @author yuys
 * @since 2018-08-21
 */
public class MarkdownMessage {
    private String msgtype = "markdown";
    private TextBean markdown;

    public static class TextBean{
        private String title;
        private String text;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public TextBean getMarkdown() {
        return markdown;
    }

    public void setMarkdown(TextBean markdown) {
        this.markdown = markdown;
    }
}
