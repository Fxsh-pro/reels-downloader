package com.instagram.proxy.config

import com.instagram.proxy.service.TelegramBot
import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Configuration
class ApplicationConfig {

    @Bean
    @Throws(TelegramApiException::class)
    fun telegramBotsApi(telegramNotificationBot: TelegramBot): TelegramBotsApi {
        val api = TelegramBotsApi(DefaultBotSession::class.java)
        api.registerBot(telegramNotificationBot)
        return api
    }

    @Bean
    fun minioClient(
        @Value("\${minio.url}")
        url: String,
        @Value("\${minio.access-key}")
        accessKey: String,
        @Value("\${minio.secret-key}")
        secretKey: String
    ): MinioClient {
        return MinioClient.builder()
            .endpoint(url)
            .credentials(accessKey, secretKey)
            .build()
    }
}