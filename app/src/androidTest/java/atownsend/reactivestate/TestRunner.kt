package atownsend.reactivestate

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner

class TestRunner : AndroidJUnitRunner() {

  override fun newApplication(cl: ClassLoader?, className: String?,
      context: Context?): Application {
    return super.newApplication(cl, TestReactiveStateApplication::class.java.name, context)
  }
}