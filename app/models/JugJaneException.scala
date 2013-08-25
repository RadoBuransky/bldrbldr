package models

case class JugJaneException(message: String, cause: Throwable = null)
	extends Exception(message, cause)