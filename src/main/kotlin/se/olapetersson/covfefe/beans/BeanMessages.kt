package se.olapetersson.covfefe.beans



open class BeansRequest(val type: BeansMessageType = BeansMessageType.NOT_IMPLEMENTED) {
}

data class AddBeanRequest(val name: String) : BeansRequest(BeansMessageType.ADD_BEAN_REQUEST) {
}

data class AddBeanResponse(val name: String) : BeansRequest(BeansMessageType.BEAN_ADDED_RESPONSE) {
}

data class GetAllBeansRequest(val name: String = "") : BeansRequest(BeansMessageType.GET_ALL_BEANS_REQEUST) {
}