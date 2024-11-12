(ns application.repositories.merchant-repository)

(defprotocol MerchantRepository
 (get-by-name [this name]))