package purl.http
package simple

case class SimpleRequest(
    httpVersion: HttpVersion,
    method: HttpMethod,
    headers: List[Array[Byte]],
    uri: String,
    body: Array[Byte],
)
