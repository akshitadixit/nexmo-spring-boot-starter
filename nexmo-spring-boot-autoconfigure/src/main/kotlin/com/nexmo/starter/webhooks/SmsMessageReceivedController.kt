/*
 * Copyright (c) 2011-2019 Nexmo Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.nexmo.starter.webhooks

import com.fasterxml.jackson.databind.ObjectMapper
import com.nexmo.client.incoming.MessageEvent
import com.nexmo.starter.events.SmsMessageReceivedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RequestMapping
@ResponseStatus(HttpStatus.OK)
class SmsMessageReceivedController(private val applicationEventPublisher: ApplicationEventPublisher) {
    /**
     * Handle incoming SMS webhooks where the data is sent via a get request.
     *
     * Nexmo HTTP Method: GET
     */
    @GetMapping("\${nexmo.webhooks.incoming-sms-endpoint:$DEFAULT_ENDPOINT}")
    fun get(@RequestParam parameters: Map<String, String>) {
        val messageEvent = MessageEvent.fromJson(ObjectMapper().writeValueAsString(parameters))
        applicationEventPublisher.publishEvent(SmsMessageReceivedEvent(this, messageEvent))
    }

    /**
     * Handle incoming SMS webhooks where the data is sent via a post request using form parameters.
     *
     * Nexmo HTTP Method: POST
     */
    @PostMapping(
        "\${nexmo.webhooks.incoming-sms-endpoint:$DEFAULT_ENDPOINT}",
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8"]
    )
    fun post(@RequestParam parameters: Map<String, String>) {
        val messageEvent = MessageEvent.fromJson(ObjectMapper().writeValueAsString(parameters))
        applicationEventPublisher.publishEvent(SmsMessageReceivedEvent(this, messageEvent))
    }

    /**
     * Handle incoming SMS webhooks where the data is sent via a post request and the body is JSON
     *
     * Nexmo HTTP Method: POST-JSON
     */
    @PostMapping(
        "\${nexmo.webhooks.incoming-sms-endpoint:$DEFAULT_ENDPOINT}",
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun post(@RequestBody json: String) {
        val messageEvent = MessageEvent.fromJson(json)
        applicationEventPublisher.publishEvent(SmsMessageReceivedEvent(this, messageEvent))
    }

    companion object {
        const val DEFAULT_ENDPOINT = "/webhooks/sms"
    }
}