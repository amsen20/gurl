package purl
package internal

import purl.unsafe.libcurl._
import purl.internal.Utils.toPtr

import scala.scalanative.unsafe._
import scala.util.Failure
import scala.util.Success
import scala.scalanative.runtime.RawPtr
import collection.mutable

final private[purl] class CurlSList(private[purl] var list: Ptr[curl_slist]) {
  private val string_list_shadow: mutable.ArrayBuffer[Array[Byte]] = mutable.ArrayBuffer.empty

  @inline def append(str: Array[Byte]): Unit =
    // Store a copy of each for the lifetime of the CurlSList,
    // This results in not being cleaned up by the GC.
    string_list_shadow.addOne(str)
    list = curl_slist_append(list, str.at(0))
  @inline def toPtr = list
}

/** Create a new curl_slist with each string
  * in the given zone and runs the body with
  * the curl_slist.
  */
private[purl] object CurlSList {
  def getSList(): (CurlSList, () => Unit) =
    val slist: CurlSList = CurlSList(list = null)

    (slist, () => if slist != null && slist.list != null then curl_slist_free_all(slist.list))
}
