package org.atricore.idbus.capabilities.idconfirmation.ui

import org.apache.wicket.request.component.IRequestablePage
import org.apache.wicket.markup.html.form.{SubmitLink, Form}

object WicketImplicits {

  type wicketSubmitAction = { def apply( e : Any ) : Either[Class[_ <: IRequestablePage],Class[_ <: IRequestablePage]]}

  implicit def pimpForm(form : Form[_]) = new PimpedForm(form)

  class PimpedForm(form : Form[_]) {

    def withSubmitLink[T <: wicketSubmitAction](func : T) = {
      val sl = new SubmitLink("doSave") {
        override def onSubmit {
          try {
            func.apply(form.getModelObject) match {
              case Right(successPage) => form.setResponsePage(successPage)
              case Left(errorPage) => error(getLocalizer.getString("app.error", this, "Operation failed"))
            }
          }
          catch {
            case _: Exception => {
              error(getLocalizer.getString("app.error", this, "Operation failed"))
            }
          }
        }
      }
      sl.setOutputMarkupId(true)
      form.add(sl)
      sl
    }
  }

}
