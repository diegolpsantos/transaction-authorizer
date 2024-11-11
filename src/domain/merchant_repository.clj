(ns domain.merchant-repository)

(defprotocol MerchantRepository
 (get-by-name [this name]))