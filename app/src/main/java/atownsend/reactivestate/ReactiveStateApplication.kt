package atownsend.reactivestate

import android.app.Activity
import android.app.Application
import atownsend.reactivestate.di.DaggerAppComponent
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

// open for testing
open class ReactiveStateApplication : Application(), HasActivityInjector {

  @Inject lateinit var injector: DispatchingAndroidInjector<Activity>

  override fun activityInjector(): AndroidInjector<Activity> = injector

  override fun onCreate() {
    super.onCreate()
    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return
    }
    LeakCanary.install(this)

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
    injectComponent()
  }

  protected open fun injectComponent() {
    DaggerAppComponent.builder().application(this).build().inject(this)
  }
}
