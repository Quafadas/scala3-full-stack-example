package example

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import example.shared.Todo

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.getquill.*
import scala.concurrent.{Future, ExecutionContext}

object DB:

  val conf: Config = ConfigFactory.load()
  val pgDataSource = new org.postgresql.ds.PGSimpleDataSource()
  pgDataSource.setUser(conf.getString("db.dbuser"))
  pgDataSource.setDatabaseName(conf.getString("db.dbname"))
  pgDataSource.setPassword(conf.getString("db.password"))
  pgDataSource.setServerNames(Array(conf.getString("db.servername")))
  pgDataSource.setPortNumbers(Array(conf.getInt("db.port")))

  val config = new HikariConfig()
  config.setDataSource(pgDataSource)

  val ctx = new PostgresJdbcContext(SnakeCase, new HikariDataSource(config))
  import ctx.*

  def allTodos(): Seq[Todo] = ctx.run(quote(query[Todo]))

  def aTodo(id: Int): Seq[Todo] = ctx.run {
    query[Todo].filter(_.todoId == lift(id))
  }

  def deleteTodo(id: Int) = ctx.run {
    quote {
      query[Todo].filter(_.todoId == lift(id)).delete
    }
  }

  // https://github.com/getquill/protoquill/issues/37
  def updateTodo(todo: Todo) = ctx.run {
    quote {
      
      query[Todo]
        .insert(todo)
        .onConflictUpdate(_.todoId)(
          (t, e) => t.description -> e.description,
          (t, e) => t.completed -> e.completed
        )
    }
  }

  def addTodo(todo: Todo) = ctx.run {
    quote {
      query[Todo]
        .insert(lift(todo))
        .onConflictIgnore(_.todoId)
        //.returningGenerated(_.todoId) // https://github.com/getquill/protoquill/issues/22
    }
  }
