package gurl
package http

import gurl.internal.Utils
import gurl.unsafe.libcurl_const

import scalanative.unsafe._
import scalanative.libc.string._
import scalanative.unsigned._
import scala.collection.mutable.ArrayBuffer

final private[gurl] class RequestSend private (
    val content: Array[Byte],
    var offset: Int,
) {
  def onRead(
      buffer: Ptr[CChar],
      size: CSize,
      nitems: CSize,
  ): CSize =
    if offset >= content.length then return Size.intToSize(0).toUSize
    val contentPtr = content.at(offset)
    val copyAmount = Math.min(size.toInt * nitems.toInt, content.length - offset)
    val copyAmountUSize = Size.intToSize(copyAmount).toUSize
    memcpy(buffer, contentPtr, copyAmountUSize)
    offset += copyAmount
    copyAmountUSize
}

private[gurl] object RequestSend {
  def apply(content: Array[Byte]): RequestSend =
    new RequestSend(content, 0)

  private[gurl] def readCallback(
      buffer: Ptr[CChar],
      size: CSize,
      nitems: CSize,
      userdata: Ptr[Byte],
  ): CSize = Utils.fromPtr[RequestSend](userdata).onRead(buffer, size, nitems)
}
