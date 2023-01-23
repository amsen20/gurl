#if __has_include(<curl/curl.h>)

#include <curl/curl.h>

CURLMSG org_http4s_curl_CURLMsg_msg(CURLMsg *curlMsg) {
  return curlMsg->msg;
}

CURL *org_http4s_curl_CURLMsg_easy_handle(CURLMsg *curlMsg) {
  return curlMsg->easy_handle;
}

CURLcode org_http4s_curl_CURLMsg_data_result(CURLMsg *curlMsg) {
  return curlMsg->data.result;
}

const char * const * org_http4s_curl_get_protocols(){
  return curl_version_info(CURLVERSION_NOW) -> protocols;
}

#endif // has_include
