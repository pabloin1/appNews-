package com.example.appnews.comments.data.model

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

// Clase para representar el objeto de usuario
data class UserIdObject(
    @SerializedName("_id") val id: String = "",
    val name: String = "",
    val email: String = ""
)

// Adaptador personalizado para el campo userId
class UserIdAdapter : JsonDeserializer<UserIdWrapper> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): UserIdWrapper {
        // Si es una cadena, crea un wrapper con la cadena como id
        return when {
            json.isJsonPrimitive -> {
                val id = json.asString
                UserIdWrapper(id = id)
            }
            // Si es un objeto, deserializa completamente
            json.isJsonObject -> {
                val obj = json.asJsonObject
                val id = obj.get("_id")?.asString ?: ""
                val name = obj.get("name")?.asString ?: ""
                val email = obj.get("email")?.asString ?: ""
                UserIdWrapper(id = id, name = name, email = email)
            }
            else -> UserIdWrapper()
        }
    }
}

// Wrapper para manejar ambos formatos
data class UserIdWrapper(
    val id: String = "",
    val name: String = "",
    val email: String = ""
)

data class CommentDTO(
    @SerializedName("_id") val id: String = "",
    @SerializedName("newsId") val newsId: String = "",
    @SerializedName("userId") val userId: UserIdWrapper = UserIdWrapper(),
    val comment: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    @SerializedName("__v") val version: Int = 0
) {
    // Propiedad derivada para obtener el nombre de usuario
    val userName: String
        get() = userId.name.ifEmpty { "Usuario" }
}

data class CreateCommentRequest(
    val newsId: String,
    val comment: String
)

data class CommentResponse(
    val comments: List<CommentDTO> = emptyList()
)