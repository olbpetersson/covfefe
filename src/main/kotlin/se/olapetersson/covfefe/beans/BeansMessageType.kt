package se.olapetersson.covfefe.beans

enum class BeansMessageType(val value: Class<out BeansRequest>) {
    ADD_BEAN_REQUEST(AddBeanRequest::class.java),
    BEAN_ADDED_RESPONSE(AddBeanResponse::class.java),
    GET_ALL_BEANS_REQEUST(GetAllBeansRequest::class.java),
    //TODO:
    NOT_IMPLEMENTED(AddBeanResponse::class.java)
}

enum class Color(val rgb: Int) {
    RED(0xFF0000),
    GREEN(0x00FF00),
    BLUE(0x0000FF)
}