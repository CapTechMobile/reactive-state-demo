package atownsend.reactivestate.di;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ApplicationModule.class) @Singleton public class MainApplicationModule {

  @Provides @Singleton static ApplicationModule.DebounceProvider provideDebounceProvider() {
    return new ApplicationModule.DebounceProvider() {
      @Override public Long provideDebounce() {
        return 250L;
      }
    };
  }

  @Provides @Singleton static Retrofit provideRetrofit(OkHttpClient okHttpClient) {
    return new Retrofit.Builder().client(okHttpClient)
        .baseUrl("https://api.github.com")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
        .build();
  }

  @Provides @Singleton static OkHttpClient provideOkHttpClient() {
    final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    return new OkHttpClient.Builder().addInterceptor(interceptor)
        .retryOnConnectionFailure(true).build();
  }
}
