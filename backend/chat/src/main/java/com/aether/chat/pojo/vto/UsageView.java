package com.aether.chat.pojo.vto;

public record UsageView(int used, int limit, String day) {
    public int remaining() {
        return Math.max(0, limit - used);
    }
}



