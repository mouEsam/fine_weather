# fine_weather

This project is an evaluation project for ITI Android in kotlin course.

## Steps to run:
1. Clone the repo locally
2. In `local.properties` provide:
   1. Weather API key for 2.5 One call (obtainable from [OpenWeather](https://openweathermap.org/api))
   2. Google Cloud Platform (GCP) Maps API key (obtainable from [GCP](https://console.cloud.google.com))
3. Insert the keys as such:
```properties
MAPS_API_KEY=INSERT_YOUR_KEY_HERE
WEATHER_API_KEY=INSERT_YOUR_KEY_HERE
```
4. Sync gradle and run the `app` module.