package com.example.xinsun.wifitest;

import java.util.List;

/**
 * Created by zhu on 15/7/20.
 */
public class Image {
    private String id;
    private Media media;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public class Media {
        private String d;
        private List<Fs> fs;

        public String getD() {
            return d;
        }

        public void setD(String d) {
            this.d = d;
        }

        public List<Fs> getFs() {
            return fs;
        }

        public void setFs(List<Fs> fs) {
            this.fs = fs;
        }

        public class Fs {
            private String n;
            private String mod;
            private String s;

            public String getN() {
                return n;
            }

            public void setN(String n) {
                this.n = n;
            }

            public String getMod() {
                return mod;
            }

            public void setMod(String mod) {
                this.mod = mod;
            }

            public String getS() {
                return s;
            }

            public void setS(String s) {
                this.s = s;
            }
        }
    }

    @Override
    public String toString() {
        return "Image{" +
                "id='" + id + '\'' +
                ", media=" + media +
                '}';
    }
}
