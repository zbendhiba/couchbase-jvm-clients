/*
 * Copyright (c) 2018 Couchbase, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.couchbase.client.scala.document

import scala.language.dynamics
import java.util.Objects

import com.couchbase.client.java._
import com.fasterxml.jackson.databind.ObjectMapper

import scala.annotation.tailrec


case class JsonObject(private val content: Map[String, Any]) extends Convertable with Dynamic {
  // For Jackson
  private def this() {
    this(Map.empty)
  }

  // Don't make this Dynamic, it makes it easy to misuse
  // TODO going back and forwards on this
  //  def dyn(): GetSelecter = GetSelecter(this, "")

  def selectDynamic(name: String): GetSelecter = GetSelecter(this, PathElements(List(PathObjectOrField(name))))

  def applyDynamic(name: String)(index: Int): GetSelecter = GetSelecter(this, PathElements(List(PathArray(name, index))))

  def put(name: String, value: Any): JsonObject = {
    Objects.requireNonNull(name)
    if (!checkType(value)) throw new IllegalArgumentException("Unsupported type for JsonObject: " + value.getClass)
    copy(content + (name -> value))
  }

  def putNull(name: String): JsonObject = {
    put(name, null)
  }

  def get(name: String): Option[Any] = {
    content.get(name)
  }

  def getString(name: String): Option[String] = {
    content.get(name).map(_.asInstanceOf[String])
  }

  def getInt(name: String): Option[Int] = {
    content.get(name).map(_.asInstanceOf[Int])
  }

  def getLong(name: String): Option[Long] = {
    content.get(name).map(_.asInstanceOf[Long])
  }

  def getDouble(name: String): Option[Double] = {
    content.get(name).map(_.asInstanceOf[Double])
  }

  def getBoolean(name: String): Option[Boolean] = {
    content.get(name).map(_.asInstanceOf[Boolean])
  }

  def getObject(name: String): Option[JsonObject] = {
    content.get(name).map(_.asInstanceOf[JsonObject])
  }

  def getArray(name: String): Option[JsonArray] = {
    content.get(name).map(_.asInstanceOf[JsonArray])
  }

  def getBigInt(name: String): Option[BigInt] = {
    content.get(name).map(_.asInstanceOf[BigInt])
  }

  def getBigDecimal(name: String): Option[BigDecimal] = {
    content.get(name).map(_.asInstanceOf[BigDecimal])
  }

  def removeKey(name: String): JsonObject = {
    copy(content - name)
  }

  def names: Set[String] = {
    content.keySet
  }

  def isEmpty: Boolean = {
    content.isEmpty
  }

  //  override def contentAs[T](path: String): T = contentAs(path, null)

  private type ContentType = Map[String, Any]

  @tailrec
  private def contentAsRecurse[T](cur: ContentType, paths: List[PathElement]): T = {
    paths match {
      case Nil =>
        throw new PathNotFound()
      case x :: Nil =>
        x match {
          case x: PathArray =>
            cur.get(x.name).map(_.asInstanceOf[JsonArray]) match {
              case Some(y: JsonArray) => y.get(x.index).asInstanceOf[T]
              case _ => throw new PathNotFound()
            }
          case x: PathObjectOrField =>
            cur.get(x.toString) match {
              case Some(y) => y.asInstanceOf[T]
              case _ => throw new PathNotFound()
            }
        }
      case x :: rest =>
        x match {
          case x: PathArray =>
            cur.get(x.name) match {
              case None => throw new PathNotFound()
              case Some(y: JsonArray) =>
                val arr = y.get(x.index).asInstanceOf[JsonObject]
                contentAsRecurse(arr.content, rest)

            }

          case x: PathObjectOrField =>
            val y = cur.get(x.toString)
            if (y.isEmpty) throw new PathNotFound()
            else {
              val z = y.get
              //          if (z.isInstanceOf[T]) {
              //            z.asInstanceOf[T]
              //          }
              //          else {
              val next = if (z.isInstanceOf[JsonObject]) z.asInstanceOf[JsonObject].content
              else z.asInstanceOf[ContentType]
              contentAsRecurse(next, rest)
              //          }
            }
        }
    }
  }


  override def contentAs[T](path: PathElements): T = {

    //    val paths = path.split("\\.").toList
    contentAsRecurse[T](content, path.paths)
  }

  override def exists(path: PathElements): Boolean = {
    //    type ContentType = Map[String,Any]
    //
    //    @tailrec
    //    def go(cur: ContentType, paths: PathElements): Boolean = {
    //      paths match {
    //        case x => true
    //        case x :: rest =>
    //          val y = cur.get(x)
    //          if (y.isEmpty) false
    //          else {
    //            go(y.get.asInstanceOf[ContentType], rest)
    //          }
    //      }
    //    }
    //
    //    val paths = path.split(".").toList
    //    go(content, paths)
    // TODO
    true
  }


  // TODO toMap
  // TODO toString

  def containsKey(name: String): Boolean = {
    content.contains(name)
  }

  def size: Int = {
    content.size
  }

  private def checkType(value: Any): Boolean = {
    value match {
      case x: String => true
      case x: Int => true
      case x: Long => true
      case x: Double => true
      case x: Boolean => true
      case x: BigInt => true
      case x: BigDecimal => true
      case x: JsonObject => true
      case x: JsonArray => true
      case _ => false
    }
  }
}


object JsonObject {
  private val mapper = new ObjectMapper()

  def fromJson(json: String): JsonObject = {
    mapper.readValue(json, classOf[JsonObject])
  }

  private val EMPTY = JsonObject.create

  def empty: JsonObject = EMPTY

  def create: JsonObject = new JsonObject(Map.empty)

  // TODO from(Map)
}