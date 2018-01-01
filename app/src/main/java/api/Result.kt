package api

import okhttp3.Response
import retrofit2.HttpException

//credit goes to https://github.com/gildor/kotlin-coroutines-retrofit/blob/master/src/main/kotlin/ru/gildor/coroutines/retrofit/Result.kt

/**
 * Sealed class of HTTP result
 */
@Suppress("unused")
public sealed class Result<out T : Any> {
  /**
   * Successful result of request without errors
   */
  public class Ok<out T : Any>(
      public val value: T,
      override val response: Response
  ) : Result<T>(), ResponseResult {
    override fun toString() = "Result.Ok{value=$value, response=$response}"
  }

  /**
   * HTTP error
   */
  public class Error(
      override val exception: HttpException,
      override val response: Response
  ) : Result<Nothing>(), ErrorResult, ResponseResult {
    override fun toString() = "Result.Error{exception=$exception}"
  }

  /**
   * Network exception occurred talking to the server or when an unexpected
   * exception occurred creating the request or processing the response
   */
  public class Exception(
      override val exception: Throwable
  ) : Result<Nothing>(), ErrorResult {
    override fun toString() = "Result.Exception{$exception}"
  }

}

/**
 * Interface for [Result] classes with [okhttp3.Response]: [Result.Ok] and [Result.Error]
 */
public interface ResponseResult {
  val response: Response
}

/**
 * Interface for [Result] classes that contains [Throwable]: [Result.Error] and [Result.Exception]
 */
public interface ErrorResult {
  val exception: Throwable
}

/**
 * Returns [Result.Ok.value] or `null`
 */
public fun <T : Any> Result<T>.getOrNull(): T? =
    if (this is Result.Ok) this.value else null

/**
 * Returns [Result.Ok.value] or [default]
 */
public fun <T : Any> Result<T>.getOrDefault(default: T): T =
    getOrNull() ?: default

/**
 * Returns [Result.Ok.value] or throw [throwable] or [ErrorResult.exception]
 */
public fun <T : Any> Result<T>.getOrThrow(throwable: Throwable? = null): T {
  return when (this) {
    is Result.Ok -> value
    is Result.Error -> throw throwable ?: exception
    is Result.Exception -> throw throwable ?: exception
  }
}