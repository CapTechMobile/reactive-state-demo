package atownsend.reactivestate.di;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.Callable;
import javax.inject.Singleton;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ApplicationModule.class) @Singleton public abstract class TestApplicationModule {

  private static final String TAG = TestApplicationModule.class.getSimpleName();

  @Provides @Singleton static Retrofit provideRetrofit(MockWebServer server, OkHttpClient client) {
    return new Retrofit.Builder().client(client)
        .baseUrl(server.url("/"))
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
        .build();
  }

  @Provides @Singleton static OkHttpClient provideOkHttpClient() {
    return new OkHttpClient.Builder().addInterceptor(
        new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build();
  }

  @Provides @Singleton static ApplicationModule.DebounceProvider provideDebounceProvider() {
    return new ApplicationModule.DebounceProvider() {
      @Override public Long provideDebounce() {
        return 0L;
      }
    };
  }

  @Provides @Singleton static MockWebServer provideMockWebServer() {
    // need to throw this on a new thread and wait for server.start()
    Observable<MockWebServer> observable = Observable.fromCallable(new Callable<MockWebServer>() {
      @Override public MockWebServer call() throws Exception {
        MockWebServer server = new MockWebServer();
        server.start(0);
        return server;
      }
    }).subscribeOn(Schedulers.newThread());
    return observable.blockingSingle();
  }
}
