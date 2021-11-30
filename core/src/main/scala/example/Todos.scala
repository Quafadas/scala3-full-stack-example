package example.shared

import io.circe.generic.semiauto.*
import io.circe.Codec

case class Todo(todoId: Int, description: String, completed: Boolean)

object Todo:
  given Codec[Todo] = deriveCodec[Todo]

case class CreateToDo(description: String, completed: Boolean)

object CreateToDo:
  given Codec[CreateToDo] = deriveCodec[CreateToDo]
