package purl
package unsafe

import scala.scalanative.unsafe._
import scala.scalanative.unsigned._
import scala.scalanative.posix.sys.select

import libcurl_const._

private[purl] object libcurl_const {
  final val CURLMSG_DONE: UInt = 1.toUInt

  final val CURLOPTTYPE_LONG = 0
  final val CURLOPTTYPE_OBJECTPOINT = 10000
  final val CURLOPTTYPE_FUNCTIONPOINT = 20000
  final val CURLOPTTYPE_STRINGPOINT = CURLOPTTYPE_OBJECTPOINT
  final val CURLOPTTYPE_SLISTPOINT = CURLOPTTYPE_OBJECTPOINT
  final val CURLOPT_CUSTOMREQUEST = CURLOPTTYPE_OBJECTPOINT + 36
  final val CURLOPT_URL = CURLOPTTYPE_STRINGPOINT + 2
  final val CURLOPT_HTTPHEADER = CURLOPTTYPE_STRINGPOINT + 23
  final val CURLOPT_HTTP_VERSION = CURLOPTTYPE_LONG + 84
  final val CURLOPT_HEADERFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 79
  final val CURLOPT_HEADERDATA = CURLOPTTYPE_OBJECTPOINT + 29
  final val CURLOPT_WRITEFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 11
  final val CURLOPT_WRITEDATA = CURLOPTTYPE_OBJECTPOINT + 1
  final val CURLOPT_READFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 12
  final val CURLOPT_READDATA = CURLOPTTYPE_OBJECTPOINT + 9
  final val CURLOPT_XFERINFOFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 219
  final val CURLOPT_XFERINFODATA = CURLOPTTYPE_OBJECTPOINT + 57
  final val CURLOPT_ERRORBUFFER = CURLOPTTYPE_OBJECTPOINT + 10
  final val CURLOPT_VERBOSE = CURLOPTTYPE_LONG + 41
  final val CURLOPT_UPLOAD = CURLOPTTYPE_LONG + 46
  final val CURLOPT_WS_OPTIONS = CURLOPTTYPE_LONG + 320
  final val CURLOPT_NOSIGNAL = CURLOPTTYPE_LONG + 99
  final val CURLOPT_NOPROGRESS = CURLOPTTYPE_LONG + 43

  final val CURLMOPT_SOCKETFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 1
  final val CURLMOPT_SOCKETDATA = CURLOPTTYPE_OBJECTPOINT + 2
  final val CURLMOPT_TIMERFUNCTION = CURLOPTTYPE_FUNCTIONPOINT + 4
  final val CURLMOPT_TIMERDATA = CURLOPTTYPE_OBJECTPOINT + 5

  final val CURL_HTTP_VERSION_NONE = 0L
  final val CURL_HTTP_VERSION_1_0 = 1L
  final val CURL_HTTP_VERSION_1_1 = 2L
  final val CURL_HTTP_VERSION_2 = 3L
  final val CURL_HTTP_VERSION_3 = 30L

  final val CURLPAUSE_RECV = 1 << 0
  final val CURLPAUSE_RECV_CONT = 0

  final val CURLPAUSE_SEND = 1 << 2
  final val CURLPAUSE_SEND_CONT = 0

  final val CURLPAUSE_ALL = CURLPAUSE_RECV | CURLPAUSE_SEND
  final val CURLPAUSE_CONT = CURLPAUSE_RECV_CONT | CURLPAUSE_SEND_CONT

  final val CURL_WRITEFUNC_PAUSE = 0x10000001L
  final val CURL_READFUNC_ABORT = 0x10000000L
  final val CURL_READFUNC_PAUSE = 0x10000001L

  // constant flags from websocket.h
  final val CURLWS_TEXT = 1 << 0
  final val CURLWS_BINARY = 1 << 1
  final val CURLWS_CONT = 1 << 2
  final val CURLWS_CLOSE = 1 << 3
  final val CURLWS_PING = 1 << 4
  final val CURLWS_OFFSET = 1 << 5
  final val CURLWS_PONG = 1 << 6

  // websocket options flags
  final val CURLWS_RAW_MODE = 1 << 0

  // select constants
  final val CURL_CSELECT_IN = 0x01
  final val CURL_CSELECT_OUT = 0x02
  final val CURL_CSELECT_ERR = 0x04

  // socket function callback events
  final val CURL_POLL_IN = 1
  final val CURL_POLL_OUT = 2
  final val CURL_POLL_INOUT = 3
  final val CURL_POLL_REMOVE = 4

  final val CURL_SOCKET_BAD = -1
  final val CURL_SOCKET_TIMEOUT = CURL_SOCKET_BAD
}

final private[purl] case class CURLcode(value: CInt) extends AnyVal {
  @inline def isOk: Boolean = value == 0
  @inline def isError: Boolean = value != 0
}
final private[purl] case class CURLMcode(value: CInt) extends AnyVal {
  @inline def isOk: Boolean = value == 0
  @inline def isError: Boolean = value != 0
}

@link("curl")
@extern
private[purl] object libcurl {

  type CURL
  type CURLcode = purl.unsafe.CURLcode

  type CURLM
  type CURLMcode = purl.unsafe.CURLMcode

  type CURLMSG = CUnsignedInt
  type CURLMsg

  type CURLoption = CUnsignedInt

  type CURLversion = CUnsignedInt

  type curl_socket_t = CInt

  type curl_slist

  type curl_version_info_data

  type header_callback = CFuncPtr4[Ptr[CChar], CSize, CSize, Ptr[Byte], CSize]

  type write_callback = CFuncPtr4[Ptr[CChar], CSize, CSize, Ptr[Byte], CSize]

  type read_callback = CFuncPtr4[Ptr[CChar], CSize, CSize, Ptr[Byte], CSize]

  type progress_callback = CFuncPtr5[Ptr[Byte], CLongLong, CLongLong, CLongLong, CLongLong, CInt]

  type socket_callback = CFuncPtr5[Ptr[CURL], CInt, CInt, Ptr[Byte], Ptr[Byte], CInt]

  type timer_callback = CFuncPtr3[Ptr[CURLM], CLong, Ptr[Byte], CInt]

  type curl_ws_frame = CStruct4[CInt, CInt, Long, Long] // age, flags, offset, bytesleft

  def curl_version(): Ptr[CChar] = extern

  def curl_version_info(age: CURLversion): Ptr[curl_version_info_data] = extern

  def curl_global_init(flags: CLongInt): CURLcode = extern

  def curl_global_cleanup(): Unit = extern

  def curl_multi_init(): Ptr[CURLM] = extern

  def curl_multi_cleanup(multi_handle: Ptr[CURLM]): CURLMcode = extern

  def curl_multi_poll(
      multi_handle: Ptr[CURLM],
      extra_fds: Ptr[Byte],
      extra_nfds: CUnsignedInt,
      timeout_ms: CInt,
      numfds: Ptr[CInt],
  ): CURLMcode = extern

  def curl_multi_wakeup(multi_handle: Ptr[CURLM]): CURLMcode = extern

  def curl_multi_fdset(
      multi_handle: Ptr[CURLM],
      read_fd_set: Ptr[select.fd_set],
      write_fd_set: Ptr[select.fd_set],
      exc_fd_set: Ptr[select.fd_set],
      max_fd: Ptr[CInt],
  ): CURLMcode = extern

  def curl_multi_perform(multi_handle: Ptr[CURLM], running_handles: Ptr[CInt]): CURLMcode = extern

  def curl_multi_socket_action(
      multi_handle: Ptr[CURLM],
      sockfd: curl_socket_t,
      ev_bitmask: CInt,
      running_handles: Ptr[CInt],
  ): CURLMcode = extern

  def curl_multi_info_read(multi_handle: Ptr[CURLM], msgs_in_queue: Ptr[CInt]): Ptr[CURLMsg] =
    extern

  @name("curl_multi_setopt")
  def curl_multi_setopt_socket_function(
      multi_handle: Ptr[CURLM],
      option: CURLMOPT_SOCKETFUNCTION.type,
      socket_callback: socket_callback,
  ): CURLMcode = extern

  @name("curl_multi_setopt")
  def curl_multi_setopt_socket_data(
      multi_handle: Ptr[CURLM],
      option: CURLMOPT_SOCKETDATA.type,
      pointer: Ptr[Byte],
  ): CURLMcode = extern

  @name("curl_multi_setopt")
  def curl_multi_setopt_timer_function(
      multi_handle: Ptr[CURLM],
      option: CURLMOPT_TIMERFUNCTION.type,
      timer_callback: timer_callback,
  ): CURLMcode = extern

  @name("curl_multi_setopt")
  def curl_multi_setopt_timer_data(
      multi_handle: Ptr[CURLM],
      option: CURLMOPT_TIMERDATA.type,
      pointer: Ptr[Byte],
  ): CURLMcode = extern

  @name("org_http4s_curl_CURLMsg_msg")
  def curl_CURLMsg_msg(curlMsg: Ptr[CURLMsg]): CURLMSG = extern

  @name("org_http4s_curl_CURLMsg_easy_handle")
  def curl_CURLMsg_easy_handle(curlMsg: Ptr[CURLMsg]): Ptr[CURL] = extern

  @name("org_http4s_curl_CURLMsg_data_result")
  def curl_CURLMsg_data_result(curlMsg: Ptr[CURLMsg]): CURLcode = extern

  @name("org_http4s_curl_get_protocols")
  def curl_protocols_info(data: Ptr[curl_version_info_data]): Ptr[CString] = extern

  @name("org_http4s_curl_get_version_num")
  def curl_version_number(data: Ptr[curl_version_info_data]): CInt = extern

  @name("org_http4s_curl_version_now")
  def CURLVERSION_NOW(): CURLversion = extern

  def curl_multi_add_handle(multi_handle: Ptr[CURLM], curl_handle: Ptr[CURL]): CURLMcode = extern

  def curl_multi_remove_handle(multi_handle: Ptr[CURLM], curl_handle: Ptr[CURL]): CURLMcode = extern

  def curl_easy_init(): Ptr[CURL] = extern

  def curl_easy_cleanup(curl: Ptr[CURL]): Unit = extern

  def curl_easy_pause(handle: Ptr[CURL], bitmask: CInt): CURLcode = extern

  @name("curl_easy_setopt")
  def curl_easy_setopt_url(curl: Ptr[CURL], option: CURLOPT_URL.type, URL: Ptr[CChar]): CURLcode =
    extern

  @name("curl_easy_setopt")
  def curl_easy_setopt_customrequest(
      curl: Ptr[CURL],
      option: CURLOPT_CUSTOMREQUEST.type,
      request: Ptr[CChar],
  ): CURLcode =
    extern

  @name("curl_easy_setopt")
  def curl_easy_setopt_httpheader(
      curl: Ptr[CURL],
      option: CURLOPT_HTTPHEADER.type,
      headers: Ptr[curl_slist],
  ): CURLcode =
    extern

  @name("curl_easy_setopt")
  def curl_easy_setopt_http_version(
      curl: Ptr[CURL],
      option: CURLOPT_HTTP_VERSION.type,
      version: CLong,
  ): CURLcode =
    extern

  @name("curl_easy_setopt")
  def curl_easy_setopt_headerfunction(
      curl: Ptr[CURL],
      option: CURLOPT_HEADERFUNCTION.type,
      header_callback: header_callback,
  ): CURLcode =
    extern

  @name("curl_easy_setopt")
  def curl_easy_setopt_headerdata(
      curl: Ptr[CURL],
      option: CURLOPT_HEADERDATA.type,
      pointer: Ptr[Byte],
  ): CURLcode =
    extern

  @name("curl_easy_setopt")
  def curl_easy_setopt_writefunction(
      curl: Ptr[CURL],
      option: CURLOPT_WRITEFUNCTION.type,
      write_callback: write_callback,
  ): CURLcode =
    extern

  @name("curl_easy_setopt")
  def curl_easy_setopt_writedata(
      curl: Ptr[CURL],
      option: CURLOPT_WRITEDATA.type,
      pointer: Ptr[Byte],
  ): CURLcode =
    extern

  @name("curl_easy_setopt")
  def curl_easy_setopt_readfunction(
      curl: Ptr[CURL],
      option: CURLOPT_READFUNCTION.type,
      read_callback: read_callback,
  ): CURLcode =
    extern

  @name("curl_easy_setopt")
  def curl_easy_setopt_readdata(
      curl: Ptr[CURL],
      option: CURLOPT_READDATA.type,
      pointer: Ptr[Byte],
  ): CURLcode =
    extern

  @name("curl_easy_setopt")
  def curl_easy_setopt_xferinfofunction(
      curl: Ptr[CURL],
      option: CURLOPT_XFERINFOFUNCTION.type,
      progress_callback: progress_callback,
  ): CURLcode =
    extern

  @name("curl_easy_setopt")
  def curl_easy_setopt_xferinfodata(
      curl: Ptr[CURL],
      option: CURLOPT_XFERINFODATA.type,
      pointer: Ptr[Byte],
  ): CURLcode =
    extern

  @name("curl_easy_setopt")
  def curl_easy_setopt_upload(
      curl: Ptr[CURL],
      option: CURLOPT_UPLOAD.type,
      upload: CLong,
  ): CURLcode =
    extern

  @name("curl_easy_setopt")
  def curl_easy_setopt_verbose(
      curl: Ptr[CURL],
      option: CURLOPT_VERBOSE.type,
      value: CLong,
  ): CURLcode =
    extern

  @name("curl_easy_setopt")
  def curl_easy_setopt_errorbuffer(
      curl: Ptr[CURL],
      option: CURLOPT_ERRORBUFFER.type,
      buffer: Ptr[CChar],
  ): CURLcode =
    extern

  @name("curl_easy_setopt")
  def curl_easy_setopt_noprogress(
      curl: Ptr[CURL],
      option: CURLOPT_NOPROGRESS.type,
      value: CLong,
  ): CURLcode =
    extern

  @name("curl_easy_strerror")
  def curl_easy_strerror(code: CURLcode): Ptr[CChar] = extern

  @name("curl_multi_strerror")
  def curl_multi_strerror(code: CURLMcode): Ptr[CChar] = extern

  @name("curl_easy_setopt")
  def curl_easy_setopt_websocket(
      curl: Ptr[CURL],
      option: CURLOPT_WS_OPTIONS.type,
      flags: CLong,
  ): CURLcode =
    extern

  @name("curl_easy_setopt")
  def curl_easy_setopt_no_signal(
      curl: Ptr[CURL],
      option: CURLOPT_NOSIGNAL.type,
      value: CLong,
  ): CURLcode =
    extern

  @name("curl_ws_send")
  def curl_easy_ws_send(
      curl: Ptr[CURL],
      buffer: Ptr[Byte],
      bufLen: CSize,
      send: Ptr[CSize],
      fragsize: CSize,
      flags: UInt,
  ): CURLcode = extern

  @name("curl_ws_meta")
  def curl_easy_ws_meta(
      curl: Ptr[CURL]
  ): Ptr[curl_ws_frame] = extern

  def curl_easy_reset(curl: Ptr[CURL]): Unit = extern

  def curl_slist_append(list: Ptr[curl_slist], string: Ptr[CChar]): Ptr[curl_slist] = extern

  def curl_slist_free_all(list: Ptr[curl_slist]): Unit = extern

}
