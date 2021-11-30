package example

import akka.actor.ActorSystem
import akka.http.scaladsl.*
import com.typesafe.config.ConfigFactory

import java.nio.file.Paths

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import akka.http.scaladsl.server.{Directives, Route}

import example.shared.{Todo, CreateToDo}

object WebServer extends server.Directives with CirceSupport:
  @main def start =
    given system: ActorSystem = ActorSystem("webserver")
    given ExecutionContext = system.dispatcher

    val config = ConfigFactory.load()
    val interface = config.getString("http.interface")
    val port = config.getInt("http.port")
    val directory = Paths.get(config.getString("example.directory"))

    val repository = Repository(directory)
    Http()
      .newServerAt(interface, port)
      .bindFlow(base ~ assets ~ api(repository) ~ todoApi)
    println(s"Server online at http://$interface:$port/")

  private val base: server.Route =
    pathSingleSlash(
      complete("hi wellt hat's something")
    )

  private val assets: server.Route =
    path("assets" / Remaining) { file =>
      getFromResource("assets/" + file)
    }

  private def hi(): server.Route =
    path("hi")(
      get(
        complete("h")
      )
    )

  private def todoApi(using ExecutionContext): Route =
    pathPrefix("api" / "todo") {
      pathEnd {
        concat(
          put {
            entity(as[CreateToDo]) { todo =>
              complete(Future(DB.addTodo(example.shared.Todo(0, todo.description, todo.completed ))))
            }
          },
          post {
            entity(as[Todo]) { todo =>
              complete(DB.updateTodo(todo))              
            }
          },
          get(complete(Future(DB.allTodos())))
        )
      }
        ~ path(IntNumber) { id =>
          concat(
            get(complete(Future(DB.aTodo(id)))),
            delete(complete(Future(DB.deleteTodo(id))))
          )
        }
    }

  private def api(repository: Repository): server.Route =
    path("api" / "notes")(
      get(
        complete(repository.getAllNotes())
      ) ~
        post(
          entity(as[CreateNote]) { request =>
            complete(repository.createNote(request.title, request.content))
          }
        )
    )
