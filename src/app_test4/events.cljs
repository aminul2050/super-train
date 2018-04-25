(ns app-test4.events
  (:require
   [re-frame.core :refer [reg-event-fx after reg-event-db reg-fx dispatch]]
   [clojure.spec.alpha :as s]
   [app-test4.db :as db :refer [app-db]]))

(def ReactNative (js/require "react-native"))
;; -- Interceptors ------------------------------------------------------------
;;
;; See https://github.com/Day8/re-frame/blob/master/docs/Interceptors.md
;;
(defn check-and-throw
  "Throw an exception if db doesn't have a valid spec."
  [spec db [event]]
  (when-not (s/valid? spec db)
    (let [explain-data (s/explain-data spec db)]
      (throw (ex-info (str "Spec check after " event " failed: " explain-data) explain-data)))))

(def validate-spec
  (if goog.DEBUG
    (after (partial check-and-throw ::db/app-db))
    []))

;; -- Handlers --------------------------------------------------------------
;;

(reg-event-fx
 :initialize-db
 [validate-spec]
 (fn [_ _]
   {:db app-db}))

(reg-event-fx
 :set-greeting
 [validate-spec]
 (fn [{:keys [db]} [_ value] ]
   {:db (assoc db :greeting value)}))

(reg-event-fx
 :set-style-v1
 [validate-spec]
 (fn [{:keys [db]} [_ ob]]
   {:db (assoc db :style-v1 ob)}))

(reg-fx :animate-spring (fn [{:keys [start stop layout]}]
                          (let [[x1 y1] start
                                [x2 y2] stop
                                animated (.-Animated ReactNative)
                                Value (.-ValueXY animated)
                                position (Value. x1 y1)
                                springcall  (fn []
                                              (.start (.spring animated position #js
                                                               {:toValue #js
                                                                {:x x2 :y y2}})))]
                            (do
                              (springcall)
                              (dispatch [layout (.getLayout position)])))))

(reg-event-fx :animate (fn [{:keys [db]} [_ [x1 y1] [x2 y2]]]
                         {:db (assoc db :cord [[x1 y1] [x2 y2]])
                          :animate-spring {:start [x1 y1]
                                           :stop [x2 y2]
                                           :layout :set-style-v1}}))

(reg-event-fx :animate-last (fn [{:keys [db]} [_ [x2 y2]]]
                              {:db (assoc db :cord [(last (:cord db))  [x2 y2]])
                               :animate-spring {:start [0 (last (last (:cord db)))]
                                                :stop [x2 y2]
                                                :layout :set-style-v1}}
                              ))
