package com.example.upsider_kadai.core.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class InstantUtils {

    public static Instant millisNow() {
        return Instant.now().truncatedTo(ChronoUnit.MILLIS);
    }

}
