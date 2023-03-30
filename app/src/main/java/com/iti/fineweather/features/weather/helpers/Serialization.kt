package com.iti.fineweather.features.weather.helpers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.iti.fineweather.features.weather.models.Temperature
import java.lang.reflect.Type

class TemperatureDeserializer : JsonDeserializer<Temperature> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Temperature? {
        return when {
            json.isJsonObject -> { context.deserialize(json.asJsonObject, Temperature.DaySummery::class.java) }
            json.isJsonPrimitive -> { Temperature.Average(json.asFloat) }
            else -> { null }
        }
    }
}

class RainDeserializer : JsonDeserializer<Float> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Float? {
        return when {
            json.isJsonObject -> { json.asJsonObject.get("1h").asFloat }
            json.isJsonPrimitive -> { json.asFloat }
            else -> { null }
        }
    }
}
