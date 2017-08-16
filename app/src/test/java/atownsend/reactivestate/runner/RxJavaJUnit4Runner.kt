package atownsend.reactivestate.runner

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.InitializationError

class RxJavaJUnit4Runner
/**
 * Creates a BlockJUnit4ClassRunner to run `klass`

 * @throws InitializationError if the test class is malformed.
 */
@Throws(InitializationError::class)
constructor(klass: Class<*>) : BlockJUnit4ClassRunner(klass) {
  init {
    RxAndroidPlugins.reset()
    RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
  }
}
