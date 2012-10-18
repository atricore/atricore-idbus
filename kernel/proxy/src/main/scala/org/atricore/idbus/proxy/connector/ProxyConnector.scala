/*
 * Atricore IDBus
 *
 * Copyright (c) 2009-2012, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.proxy.connector

import collection.JavaConversions._
import org.atricore.idbus.proxy._
import cc.spray.{RequestContext, RequestResponder}
import cc.spray.http.HttpHeaders.{`Content-Length`, `Content-Type`}
import cc.spray.http.MediaTypes._
import cc.spray.http._
import cc.spray.http.StatusCodes._
import akka.actor.ActorSystem
import cc.spray.{Timeout => SprayTimeout}

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import java.io.{IOException, InputStream}
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.{TimeUnit, CountDownLatch}
import rest.ProxySprayRequestContext

/**
 * Main entry point for Proxy/Spray routes.
 *
 * Builds a servlet-agnostic proxy-specific Spray request and submits it to the Spray Root Service which in turn
 * submits it to the configured Http Service registered at boot time. Works in sync mode, hence it requires the
 * client to wait until a response is available or a timeout is reached.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
trait ProxyConnector
{

  import ProxyConnectorSettings._
  import LogLevels._

  protected lazy val system = ActorSystem("proxy")
  private val EmptyByteArray = new Array[Byte](0)
  private var timeout: Int = RequestTimeout.toInt
  lazy val rootService = system.actorFor(RootActorPath)
  lazy val timeoutActor = if (TimeoutActorPath.isEmpty) rootService else system.actorFor(TimeoutActorPath)

  protected def log(level : LogLevel, msg : String, cause : Exception = null)

  def requestContext(req: HttpServletRequest, resp: HttpServletResponse,
                     responder: RequestResponder, env : Environment): Option[RequestContext] = {
    try {
      Some {
        ProxySprayRequestContext(
          request = httpRequest(req),
          remoteHost = req.getRemoteAddr,
          responder = responder,
          environment = env
        )
      }
    } catch {
      case HttpException(failure, reason) => respond(req, resp, HttpResponse(failure.value, reason)); None
      case e: Exception => respond(req, resp, HttpResponse(500, "Internal Server Error:\n" + e.toString)); None
    }
  }

  def httpRequest(req: HttpServletRequest) = {
    val (contentTypeHeader, contentLengthHeader, regularHeaders) = HttpHeaders.parseFromRaw {
      val headerNames: List[_] = req.getHeaderNames.toList

      headerNames.map {
        name =>
          name.asInstanceOf[String] -> req.getHeaders(name.asInstanceOf[String]).toList.mkString(", ")
      }
    }
    HttpRequest(
      method = HttpMethods.getForKey(req.getMethod).get,
      uri = rebuildUri(req),
      headers = regularHeaders,
      content = httpContent(req.getInputStream, contentTypeHeader, contentLengthHeader),
      protocol = HttpProtocols.getForKey(req.getProtocol).get
    )
  }

  def rebuildUri(req: HttpServletRequest) = {
    val uri = req.getRequestURI
    val queryString = req.getQueryString
    if (queryString != null && queryString.length > 1) uri + '?' + queryString else uri
  }

  def httpContent(inputStream: InputStream, contentTypeHeader: Option[`Content-Type`],
                  contentLengthHeader: Option[`Content-Length`]): Option[HttpContent] = {
    contentLengthHeader.flatMap {
      case `Content-Length`(0) => None
      case `Content-Length`(contentLength) => {
        val body = if (contentLength == 0) EmptyByteArray
        else try {
          val buf = new Array[Byte](contentLength)
          var bytesRead = 0
          while (bytesRead < contentLength) {
            val count = inputStream.read(buf, bytesRead, contentLength - bytesRead)
            if (count >= 0) bytesRead += count
            else throw new HttpException(BadRequest, "Illegal Servlet request entity, expected length " +
              contentLength + " but only has length " + bytesRead)
          }
          buf
        } catch {
          case e: IOException =>
            throw new HttpException(InternalServerError, "Could not read request entity due to " + e.toString)
        }
        val contentType = contentTypeHeader.map(_.contentType).getOrElse(ContentType(`application/octet-stream`))
        Some(HttpContent(contentType, body))
      }
    }
  }

  def respond(req: HttpServletRequest, servletResponse: HttpServletResponse, response: HttpResponse) {
    try {
      servletResponse.setStatus(response.status.value)
      response.headers.foreach(header => servletResponse.addHeader(header.name, header.value))
      response.content.foreach {
        content =>
          servletResponse.addHeader("Content-Type", content.contentType.value)
          servletResponse.addHeader("Content-Length", content.buffer.length.toString)
          servletResponse.getOutputStream.write(content.buffer)
      }
    } catch {
      case e: IOException => log(Error,  "Could not write response body of {}, probably the request has either timed out" +
        "or the client has disconnected :" + requestString(req), e)
      case e: Exception => log(Error, "Could not complete :" + requestString(req), e)
    }
  }

  def responderFor(req: HttpServletRequest)(f: HttpResponse => Unit): RequestResponder = {
    RequestResponder(
      complete = {
        response =>
          try {
            f(response)
          } catch {
            case e: IllegalStateException => log(Error, "Could not complete {}, it probably timed out and has therefore" +
              "already been completed (" + requestString(req) + ")", e)
            case e: Exception => log(Error, "Could not complete " + requestString(req) + " due to ", e)
          }
      },
      reject = _ => throw new IllegalStateException
    )
  }

  def responder(req: HttpServletRequest, resp: HttpServletResponse,
                requestProcessed: CountDownLatch, requestResponded: AtomicBoolean): RequestResponder = {
    responderFor(req) {
      response =>
        // if request is not handled by Spray - as signaled using 404 status code - do not
        // propagate this condition to the catalina response. This way, the valves
        // down in the pipeline get the chance to continue with the request processing
        if ( response.status != StatusCodes.NoContent && response.status != StatusCodes.NotFound) {
          respond(req, resp, response)
          requestResponded.compareAndSet(false, true)
        }

        requestProcessed.countDown()
    }
  }

  def handleTimeout(req: HttpServletRequest, resp: HttpServletResponse) {
    val latch = new CountDownLatch(1);
    val responder = responderFor(req) {
      response =>
        respond(req, resp, response)
        latch.countDown()
    }
    requestContext(req, resp, responder, emptyEnv).foreach {
      context =>
        log(Error, "Timeout of " + context.request)
        timeoutActor ! SprayTimeout(context)
        latch.await(timeout, TimeUnit.MILLISECONDS) // give the timeoutActor another `timeout` ms for completing
    }
  }

  def requestString(req: HttpServletRequest) = req.getMethod + " request to '" + rebuildUri(req) + "'"

}

object LogLevels {
  sealed class LogLevel

  object Debug extends LogLevel
  object Warn extends LogLevel
  object Info extends LogLevel
  object Error extends LogLevel
}


