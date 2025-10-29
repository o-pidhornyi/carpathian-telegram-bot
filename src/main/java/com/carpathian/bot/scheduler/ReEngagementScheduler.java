package com.carpathian.bot.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled tasks to re‑engage users with seasonal promotions and
 * editorial picks.  In a real implementation this component would
 * iterate over users and push messages via the Telegram Bot API.  Here
 * we simply log when the task is executed to illustrate scheduling
 * mechanics and timezone awareness (Europe/Kyiv).
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ReEngagementScheduler {

    /**
     * Sends weekly top picks every Monday at noon Kyiv time.  Cron
     * expression fields: second, minute, hour, day of month, month,
     * day of week.
     */
    @Scheduled(cron = "0 0 12 * * MON", zone = "Europe/Kyiv")
    public void sendWeeklyPicks() {
        log.info("Executing weekly re‑engagement task: send top picks");
        // TODO: fetch editorial picks and send to users via Telegram
    }
}