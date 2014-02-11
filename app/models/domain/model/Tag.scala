package models.domain.model

import models.domain.model.Tag.TagId
import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal
import models.JugjaneException

/**
 * Route tagging.
 */
trait Tag {
  def id: TagId
}

/**
 * Categorization tag. Users cannot do anything about it.
 * @param name
 */
case class CategoryTag(name: String) extends Tag {
  val id = name.toLowerCase
}

/**
 * General flagging tag. Users can (un)flag it.
 */
case class FlagTag(id: String, counter: Int = 0) extends Tag {
  def setCounter(counter: Int) = FlagTag(id, counter)
}

object Tag {
  type TagId = String

  val flags: List[FlagTag] = {
    List(FlagTag("awesome"), FlagTag("boring"), FlagTag("tooeasy"),
    FlagTag("toohard"), FlagTag("scary"), FlagTag("whathow"))
  }

  val categories: List[CategoryTag] = {
    List(CategoryTag("Slab"), CategoryTag("Overhang"),
    CategoryTag("Traverse"), CategoryTag("Corner"), CategoryTag("Edge"), CategoryTag("Crack"),
    CategoryTag("Jugs"), CategoryTag("Crimps"), CategoryTag("Slopers"), CategoryTag("Pinches"),
    CategoryTag("Volumes"))
  }

  def getFlagsByIdsCounts(idsCounts: Map[String, Int]): Try[List[FlagTag]] = {
    Try {
      flags.map { f =>
        idsCounts.get(f.id) match {
          case Some(c) => f.setCounter(c)
          case _ => f
        }
      }
    }
  }

  def getFlagsByIds(ids: Iterable[String]): Try[List[FlagTag]] = getByIds(ids, flags)

  def getCategoriesByIds(ids: Iterable[String], extra: Iterable[CategoryTag]): Try[List[CategoryTag]] =
    getByIds(ids, categories ++ extra)

  private def getByIds[T <: Tag](ids: Iterable[String], tags: Seq[T]): Try[List[T]] = {
    try {
      val result = ids.map { id =>
        tags.find(_.id == id) match {
          case Some(c) => c
          case None => throw new JugjaneException("Tag doesn't exist!")
        }
      }
      Success(result.toList)
    }
    catch {
      case e: JugjaneException => Failure(e)
    }
  }
}