package models

case class JugjaneException(message: String, cause: Throwable = null)
	extends Exception(message, cause)