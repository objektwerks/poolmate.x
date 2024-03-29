package poolmate

import com.raquo.laminar.api.L.*

trait View:
  protected[this] val errorBus = new EventBus[String]

  def call(command: Command, handler: Event => Unit): Unit = Proxy.call(command, handler)
  
  def route(page: Page): Unit = PageRouter.router.pushState(page)

  def log(message: String): Unit = log(message)

  def emitError(message: String): Unit =
    errorBus.emit(message)
    log(message)

  def clearErrors(): Unit = errorBus.emit("")

  def emit(eventBus: EventBus[String], message: String): Unit = eventBus.emit(message)

  def clear(eventBus: EventBus[String]): Unit = eventBus.emit("")