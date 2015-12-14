(ns om-next.core
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(defonce app-state (atom {:count 0}))

(defui Counter
  static om/IQuery
  (query [this]
    [:count])
  Object
  (render [this]
          (let [count (:count (om/props this))]
            (dom/div nil
                     (dom/div nil (str "Count: " count))
                     (dom/button
                      #js {:onClick (fn [e]
                                      (om/transact! this '[(increment)]))}
                      "Increase")))))

(defn read
  [{:keys [state] :as env} key params]
  (let [st @state] 
   (if-let [[_ v] (find st key)]
      {:value v}
      {:value :not-found})))

(defn mutate [{:keys [state] :as env} key params]
  (if (= 'increment key)
    {:value {:keys [:count]}
     :action #(swap! state update-in [:count] inc)}
    {:value :not-found}))

(def reconciler
  (om/reconciler {:state app-state
                  :parser (om/parser {:read read
                                      :mutate mutate})}))

(om/add-root! reconciler Counter (gdom/getElement "app"))
