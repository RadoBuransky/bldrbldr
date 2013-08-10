package models;

import java.io.File

case class FileModel(
 id: String,
 foi: String // file objectid
)

object FileModel {
  def insertFile(file: File, contentType:String) = {
    
  }
}