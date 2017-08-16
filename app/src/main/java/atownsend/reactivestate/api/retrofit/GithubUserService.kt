package atownsend.reactivestate.api.retrofit

import atownsend.reactivestate.api.model.Repo
import atownsend.reactivestate.api.model.User
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

typealias RepoObservable = Observable<Response<List<Repo>>>
typealias UserObservable = Observable<Response<User>>

interface GithubUserService {

  @Headers("Accept: application/vnd.github.v3+json", "Connection: close")
  @GET("/users/{user}")
  fun checkUser(@Path("user") user: String): UserObservable

  @Headers("Accept: application/vnd.github.v3+json", "Connection: close")
  @GET("/users/{user}/repos")
  fun getRepos(@Path("user") user: String): RepoObservable
}