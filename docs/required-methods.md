* getHouseId(userId)
* getStorageId(houseId)
  * (or get StorageId(userId))

### Endpoints
* Gateway - common path “application”:
  * Request forwarded to authentication - path starts with “/application/authentication/{func}” -> URL becomes “/authentication/{func}”
  * Requests forwarded to food management - path starts with “/application/foodmanagement/{func}” -> URL becomes “/foodmanagement/{func}”
  * Requests forwarded to food management - path starts with “/application/housemanagement/{func}” -> URL becomes “/housemanagement/{func}”
* Authentication - common path “/authentication”
  * “/sign-up”
  * “/login”
  * “/unregister”
  * “clear”
  * “user/get_ids”
* House Management - common path “/housemanagement”
  * “/house/create”
  * “/house/delete”
  * “/house/join”
  * “/house/leave”
  * “/house/getid”
  * “/house/reset”
  * “/validate/housemates”
* Food Management - common path “/foodmanagement”