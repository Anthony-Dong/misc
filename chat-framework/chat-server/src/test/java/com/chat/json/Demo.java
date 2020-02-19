package com.chat.json;

/**
 * TODO
 *
 * @date:2020/2/18 20:48
 * @author: <a href='mailto:fanhaodong516@qq.com'>Anthony</a>
 */
public class Demo {

    private String _demo;

    public String get_demo() {
        return _demo;
    }

    public void set_demo(String _demo) {
        this._demo = _demo;
    }

    @Override
    public String toString() {
        return "Demo{" +
                "_demo='" + _demo + '\'' +
                '}';
    }

    public static class Chat implements Inter{
        private Chat() {
        }

        private String _demo;

        public String get_demo() {
            return _demo;
        }

        public void set_demo(String _demo) {
            this._demo = _demo;
        }

        @Override
        public String toString() {
            return "Chat{" +
                    "_demo='" + _demo + '\'' +
                    '}';
        }
    }

    public static Inter instance(){
        Chat chat = new Chat();
        chat.set_demo("1111111");
        return chat;
    }
}
