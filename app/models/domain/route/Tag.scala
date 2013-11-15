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
  val color: Color
}

/**
 * Categorization tag. Users cannot do anything about it.
 * @param name
 */
case class CategoryTag(name: String, color: Color) extends Tag {
  val id = name.toLowerCase
}

/**
 * General flagging tag. Users can (un)flag it.
 * @param name
 */
case class FlagTag(name: String, color: Color) extends Tag{
  val id = name.toLowerCase.filter(c => (c != ' ') && (c != '?'))
}

object Tag {
  def getFlags: List[FlagTag] = {
    FlagTag("Awesome", Color.Red) ::
    FlagTag("Boring", Color.Red) ::
    FlagTag("Too easy", Color.Red) ::
    FlagTag("Too hard", Color.Red) ::
    FlagTag("Scary", Color.Red) ::
    FlagTag("What? How?", Color.Red) :: Nil
  }

  def getCategories: List[CategoryTag] = {
    CategoryTag("Slab", Color.Blue) ::
    CategoryTag("Overhang", Color.Blue) ::
    CategoryTag("Traverse", Color.Blue) ::
    CategoryTag("Corner", Color.Blue) ::
    CategoryTag("Edge", Color.Blue) ::
    CategoryTag("Crack", Color.Blue) ::
    CategoryTag("Jugs", Color.Blue) ::
    CategoryTag("Crimps", Color.Blue) ::
    CategoryTag("Slopers", Color.Blue) ::
    CategoryTag("Pinches", Color.Blue) ::
    CategoryTag("Volumes", Color.Blue) ::  Nil
  }
}