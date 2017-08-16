package atownsend.reactivestate.di;

import atownsend.reactivestate.TestReactiveStateApplication;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;
import javax.inject.Singleton;

@Component(modules = {
    AndroidSupportInjectionModule.class, TestApplicationModule.class, AndroidBindingsModule.class
}) @Singleton public interface TestAppComponent extends AppComponent {

  void inject(TestReactiveStateApplication application);

  @Component.Builder interface Builder {
    @BindsInstance Builder application(TestReactiveStateApplication application);

    TestAppComponent build();
  }
}
