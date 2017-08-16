package atownsend.reactivestate

import atownsend.reactivestate.di.DaggerTestAppComponent
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import javax.inject.Inject

class TestReactiveStateApplication : ReactiveStateApplication() {

  @Inject lateinit var mockWebServer: MockWebServer
  @Inject lateinit var okHttpClient: OkHttpClient

  override fun injectComponent() {
    DaggerTestAppComponent.builder().application(this).build().inject(this)
  }
}

