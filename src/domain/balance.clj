(ns domain.balance)

(defn- get-total-balance [balances]
  (->> balances
       (map #(:total-amount %))
       (reduce +)))

(defn- get-mcc-balance [mcc balances]
  (first (filter (fn [balance]
                   (some #(= mcc %) (:code balance)))
                 balances)))

(defn- cash-balance [balances]
  (first (filter #(= "cash" (:type %)) 
                 balances)))

(defn get-balances [mcc balances]
  (filter (fn [balance]
            (or (some #(= mcc %) (:code balance))
                #(= "cash" (:type balance))))
          balances))

(defn has-balance? [amount balances]
  (->> balances
       (get-total-balance)
       (<= amount)))

(defmulti decreased (fn [balance _]
                      (:type balance)))

(defmethod decreased "cash" [balance amount-to-decrease] 
  {:balance (update balance :total-amount - amount-to-decrease)})

(defmethod decreased :default [{:keys [total-amount] :as balance} amount-to-decrease] 
  (let [rest (- amount-to-decrease total-amount)]
    (if (> rest 0)
      {:balance (update balance :total-amount (fn [current-amount]
                                                (- current-amount current-amount)))
       :rest rest}
      {:balance (update balance :total-amount - amount-to-decrease) :rest 0})
    ))

(defn decrease [{:keys [mcc total-amount]} balances]
  (if (has-balance? total-amount balances) 
    (let [mcc-balance  (get-mcc-balance mcc balances)
          cash-balance (cash-balance balances)]
      (cond
        (nil? mcc-balance) (decreased cash-balance total-amount)
        (< (:total-amount mcc-balance) total-amount) (let [nmcc-value (decreased mcc-balance total-amount)
                                                           nc-value (decreased cash-balance (:rest nmcc-value))]
                                                       [nmcc-value nc-value])
        :else (decreased mcc-balance total-amount)))
    balances))
