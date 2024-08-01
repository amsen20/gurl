package gurl
package http

import gurl.internal._
import gurl.unsafe.CurlRuntimeContext
import gurl.http.simple._
import gurl.unsafe.libcurl_const
import gurl.unsafe.CURLcode

import scala.scalanative.unsafe._
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import scala.collection.mutable.ArrayBuffer

object CurlRequest {
  private def setup(
      handle: CurlEasy,
      sendData: RequestSend,
      recvData: RequestRecv,
      progressData: RequestProgress,
      version: String,
      method: String,
      headers: CurlSList,
      uri: String,
  )(using cc: CurlRuntimeContext, zone: Zone): Unit = {
    // handle.setVerbose(true)

    handle.setCustomRequest(toCString(method))

    handle.setUpload(true)

    handle.setNoSignal(true)

    handle.setUrl(toCString(uri))

    val httpVersion = version match {
      case "1.0" => libcurl_const.CURL_HTTP_VERSION_1_0
      case "1.1" => libcurl_const.CURL_HTTP_VERSION_1_1
      case "2" => libcurl_const.CURL_HTTP_VERSION_2
      case "3" => libcurl_const.CURL_HTTP_VERSION_3
      case _ => libcurl_const.CURL_HTTP_VERSION_NONE
    }
    handle.setHttpVersion(toSize(httpVersion))

    handle.setHttpHeader(headers.list)

    handle.setReadData(Utils.toPtr(sendData))
    handle.setReadFunction(RequestSend.readCallback(_, _, _, _))

    handle.setHeaderData(Utils.toPtr(recvData))
    handle.setHeaderFunction(RequestRecv.headerCallback(_, _, _, _))

    handle.setWriteData(Utils.toPtr(recvData))
    handle.setWriteFunction(RequestRecv.writeCallback(_, _, _, _))

    handle.setNoProgress(false)
    handle.setProgressData(Utils.toPtr(progressData))
    handle.setProgressFunction(RequestProgress.progressCallback(_, _, _, _, _))

    cc.keepTrack(sendData)
    cc.keepTrack(recvData)
    cc.keepTrack(progressData)
    cc.addHandle(handle.curl, recvData.onTerminated)
  }

  def apply(req: SimpleRequest)(onResponse: Try[SimpleResponse] => Unit)(using
      cc: CurlRuntimeContext
  ): Unit =
    val cleanUps = ArrayBuffer.empty[() => Unit]

    try
      val (handle, handleCleanedUp) = CurlEasy.getEasy()
      cleanUps.addOne(handleCleanedUp)
      cleanUps.addOne(() => cc.removeHandle(handle.curl))

      val (headers, slistCleanedUp) = CurlSList.getSList()
      cleanUps.addOne(slistCleanedUp)

      req.headers.foreach(headers.append(_))

      val zone = Zone.open()
      cleanUps.addOne(() => zone.close())

      val sendData = RequestSend(req.body)
      val progressData = RequestProgress()
      val recvData = RequestRecv(res =>
        cleanUps.foreach(_())
        onResponse(res)
      )

      given Zone = zone
      setup(
        handle,
        sendData,
        recvData,
        progressData,
        req.httpVersion.toString,
        req.method.toString,
        headers,
        req.uri,
      )

    catch {
      case e: Throwable =>
        cleanUps.foreach(_())
        onResponse(Failure(e))
    }
}
