package com.instagram.proxy.integration

import com.instagram.proxy.integration.dto.DownloadRequest
import com.instagram.proxy.integration.dto.DownloadResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "instagramContentClient", url = "\${flask.api.base-url}")
interface InstagramContentClient {

    @PostMapping("/download")
    fun downloadInstagramContent(@RequestBody request: DownloadRequest): DownloadResponse
}