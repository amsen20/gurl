package ca.uwaterloo.plg.curl

import munit.FunSuite
import ca.uwaterloo.plg.curl.unsafe.CurlRuntime

class CurlRuntimeSuite extends FunSuite {

  test("curl version") {
    val prefixPattern = """^libcurl\/[78]\..+$""".r
    assert(prefixPattern.matches(MyCurlRuntime.curlVersion))
  }

  test("curl version number") {
    assert(MyCurlRuntime.curlVersionNumber > 0x070000)
    assert(MyCurlRuntime.curlVersionTriple._1 >= 7)
  }

  test("curl protocols") {
    val protocols = MyCurlRuntime.protocols
    assert(protocols.contains("http"))
    assert(protocols.contains("https"))
  }

}

object MyCurlRuntime extends CurlRuntime