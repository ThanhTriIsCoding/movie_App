package com.example.ojt_aada_mockproject1_trint28.di.module;

import com.example.ojt_aada_mockproject1_trint28.data.remote.api.ApiService;
import com.example.ojt_aada_mockproject1_trint28.data.remote.api.RetrofitClient;
import com.example.ojt_aada_mockproject1_trint28.data.repository.MovieRepository;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.GetMoviesUseCase;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    public ApiService provideApiService() {
        return RetrofitClient.getApiService();
    }

    @Provides
    public MovieRepository provideMovieRepository(ApiService apiService) {
        String apiKey = "e7631ffcb8e766993e5ec0c1f4245f93"; // Thay bằng cách lấy API key từ config
        return new MovieRepository(apiService, apiKey);
    }

    @Provides
    public GetMoviesUseCase provideGetMoviesUseCase(MovieRepository movieRepository) {
        return new GetMoviesUseCase(movieRepository);
    }

    @Provides
    public String provideApiKey() {
        return "e7631ffcb8e766993e5ec0c1f4245f93"; // Thay bằng cách lấy API key từ config
    }
}