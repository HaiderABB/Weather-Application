package Src.OpenWeatherAPI;

import com.google.gson.JsonObject;

interface InterfaceAPI {
  String APIkey = "cc7d0c84d9aca07ad0bc1494b2af27a0";
  String units = "metric";

  void APIcall(double latitude, double longitude); // to get data from longitude and latitude

  void APIcall(String cityName);

  public void parseJSON(JsonObject jsonObject);

}