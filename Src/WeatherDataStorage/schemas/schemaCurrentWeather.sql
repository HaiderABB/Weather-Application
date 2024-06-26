CREATE TABLE Current_Weather_Data (
    loc_id INT PRIMARY KEY,
    city_name VARCHAR(255),
    latitude Float,
    longitude Float,
	weather_main VARCHAR(255),
    weather_description VARCHAR(255),
	weather_icon VARCHAR(255),
	temperature Float,
    feels_like Float,
    temp_min Float,
    temp_max Float,
	pressure INT,
	humidity INT,
	visibility INT,
	wind_speed Float,
	rain_ Float,
	 clouds_all INT,
	 dt INT,
	 country VARCHAR(255),
    sunrise BIGINT,
    sunset BIGINT,
	 timezone INT,
);
