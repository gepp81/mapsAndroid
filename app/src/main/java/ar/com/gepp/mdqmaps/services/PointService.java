package ar.com.gepp.mdqmaps.services;

import java.util.HashMap;
import java.util.List;

import ar.com.gepp.mdqmaps.services.dto.PointDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PointService {

    @POST("api/point/byAll")
    Call<List<PointDTO>> byAll(@Body HashMap<String, Object> body);

}
