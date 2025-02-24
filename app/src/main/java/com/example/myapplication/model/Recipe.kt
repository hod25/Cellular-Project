package com.example.myapplication.model

data class Recipe(
    val id: String = "",
    val title: String = "",
    val image: String = "",
    val ingredients: Array<String> = arrayOf(),
    val tags: Array<String> = arrayOf(),
    val owner: String = "",
    val likes: Int = 0
)
{
//    fun toMap(): Map<String, Any> {
//        return mapOf(
//            "title" to title,
//            "image" to image,
//            "ingredients" to ingredients,
//            "tags" to tags,
//            "owner" to owner,
//            "likes" to likes
//        )
//    }

//    companion object {
//        fun fromMap(map: Map<String, Any>): Recipe {
//            return Recipe(
//                id = map["id"] as? String ?: "",
//                title = map["title"] as? String ?: "",
//                image = map["image"] as? String ?: "",
//                ingredients = map["ingredients"] as? List<String> ?: listOf(),
//                tags = map["tags"] as? List<String> ?: listOf(),
//                owner = map["owner"] as? String ?: "",
//                likes = (map["likes"] as? Long)?.toInt() ?: 0
//            )
//        }
//    }
}
