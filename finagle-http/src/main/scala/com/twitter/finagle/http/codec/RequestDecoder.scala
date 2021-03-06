package com.twitter.finagle.http.codec

import com.twitter.logging.Logger
import com.twitter.finagle.http.Request
import org.jboss.netty.channel.{Channel, Channels, ChannelHandlerContext, MessageEvent,
  SimpleChannelHandler}
import org.jboss.netty.handler.codec.http.HttpRequest


/** Convert Netty Request to a Finagle-HTTP Request. */
class RequestDecoder extends SimpleChannelHandler {

  protected[this] val log = Logger("finagle-http")

  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) {
    e.getMessage match {
      case httpRequest: HttpRequest if !httpRequest.isChunked =>
        val request = Request(httpRequest, e.getChannel)
        Channels.fireMessageReceived(ctx, request)
      case unknown =>
        log.warning("RequestDecoder: illegal message type: %s", unknown)
        Channels.disconnect(ctx.getChannel)
    }
  }
}
