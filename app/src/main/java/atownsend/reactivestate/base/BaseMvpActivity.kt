package atownsend.reactivestate.base

import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity

/**
 * Base class for implementing a View interface and retaining a Presenter
 */
abstract class BaseMvpActivity<V : MvpView, P : BasePresenter<V, *>> : DaggerAppCompatActivity() {

  lateinit var presenter: P

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (lastNonConfigurationInstance == null) {
      presenter = createPresenter()
    } else {
      presenter = lastCustomNonConfigurationInstance as P
    }
  }

  override fun onStart() {
    super.onStart()
    presenter.attachView(getMvpView())
  }

  override fun onStop() {
    presenter.detachView(isChangingConfigurations || !isFinishing)
    super.onStop()
  }

  override fun onRetainCustomNonConfigurationInstance(): Any? {
    return presenter
  }

  fun getMvpView(): V = this as V

  abstract fun createPresenter(): P
}