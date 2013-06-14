package org.atricore.idbus.capabilities.idconfirmation.ui.util

import java.net.URL;
import java.util.Locale;
import org.apache.wicket.util.convert.converter.AbstractConverter
;

/**
 * Converts from and to URLs.
 */
case object URLConverter extends AbstractConverter[URL]
{
  override def getTargetType = classOf[URL]

  override def convertToString(value: URL, locale: Locale) = value match {
    case url : URL => url.toString
    case _ => "???"
  }

  def convertToObject(value: String, locale: Locale) = new URL(value)
}
