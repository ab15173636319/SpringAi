package com.aether.app.pojo.vto;

import java.time.Instant;

public record HealthView(String status, String service, Instant time) {
}

