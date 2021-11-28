package example

import org.scalajs.dom.html.Element
import org.scalajs.dom.document
import org.scalajs.dom.html.*
import scala.scalajs.js

import DomHelper.*

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

import annotation.unused
import scala.scalajs.js.annotation.JSImport

 @js.native
@JSImport("../../../../src/main/scala/resources/main.css", JSImport.Namespace)
object Css extends js.Object

object WebPage:
  given ExecutionContext = ExecutionContext.global
  val service = new HttpClient()
  @unused private val css = Css

  val titleInput = input()
  val contentTextArea = textarea()

  val saveButton = button("Create note")
  saveButton.onclick = _ =>
    service
      .createNote(titleInput.value, contentTextArea.value)
      .map(addNote)

  val form: Div = div(
    titleInput,
    contentTextArea,
    saveButton
  )
  form.className = "note-form"

  val appContainer: Div = div(
    h1("My Notepad"),
    form
  )
  appContainer.id = "appContainer"

  def addNote(note: Note): Unit =
    val elem = div(
      h2(note.title),
      p(note.content)
    )
    elem.className = "note"
    appContainer.appendChild(elem)

  @main def start: Unit =
    document.body.appendChild(appContainer)

    for notes <- service.getAllNotes(); note <- notes do addNote(note)
