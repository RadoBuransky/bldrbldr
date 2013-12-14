package models.domain.route

import models.Color

/**
 * Created with IntelliJ IDEA.
 * User: rado
 * Date: 11/11/13
 * Time: 9:28 AM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Route tagging.
 */
trait Tag {
  val id: String
  val name: String
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
case class FlagTag(name: String) extends Tag{
  val id = name.toLowerCase.filter(c => (c != ' ') && (c != '?'))
}

object Tag {
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
}