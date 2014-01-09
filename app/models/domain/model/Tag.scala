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
  def name: String
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
 * @param name
 */
case class FlagTag(name: String, counter: Option[Int] = None) extends Tag {
  val id = name.toLowerCase.filter(c => (c != ' ') && (c != '?'))

  def setCounter(counter: Int) = FlagTag(name, Some(counter))
}

object Tag {
  type TagId = String

  val flags: List[FlagTag] = {
    List(FlagTag("Awesome"), FlagTag("Boring"), FlagTag("Too easy"),
    FlagTag("Too hard"), FlagTag("Scary"), FlagTag("What? How?"))
  }

  val categories: List[CategoryTag] = {
    List(CategoryTag("Slab"), CategoryTag("Overhang"),
    CategoryTag("Traverse"), CategoryTag("Corner"), CategoryTag("Edge"), CategoryTag("Crack"),
    CategoryTag("Jugs"), CategoryTag("Crimps"), CategoryTag("Slopers"), CategoryTag("Pinches"),
    CategoryTag("Volumes"))
  }

  def getFlagsByIdsCounts(idsCounts: Map[String, Int]): Try[List[FlagTag]] = {
    getByIds(idsCounts.keys, flags).map { fs =>
      fs.map { f =>
        f.setCounter(idsCounts(f.id))
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