(ns app-test4.events
  (:require
   [oops.core :as oops]
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

(reg-event-fx
 :set-style-v1-extended
 [validate-spec]
 (fn [{:keys [db]} [_ ob]]
   (if (= (count (:events ob)) 0)
     {:db (assoc db :style-v1-extended ob)}
     {:db (assoc db :style-v1-extended ob)
      (first ob) :style-v1-extended})))



(reg-fx :animate-spring (fn [{:keys [start stop layout]}]
                          (let [[x1 y1] start
                                [x2 y2] stop
                                animated (.-Animated ReactNative)
                                Value (.-ValueXY animated)
                                position (Value. #js {:x x1 :y y1})
                                springcall  (fn []
                                              (.start (.spring animated position #js
                                                               {:toValue #js
                                                                {:x x2 :y y2}})))]
                            (do
                              (springcall)
                              (dispatch [layout (.getLayout position)])))))

(reg-event-fx
 :animate-spring2
 (fn [{:keys [db]} [_ [x2 y2] style]]
   (let [position (get-in db [:animate-db style :position])
         animated (get-in db [:animate-db :obj])
         springcall  (fn []
                       (.start (.spring animated position #js
                                        {:toValue #js
                                         {:x x2 :y y2}})))]
     (do
       (springcall)
       {:db (assoc-in db
                      [:animate-db style] {:postion position
                                           :layout (.getLayout position)})}))))


(reg-fx :animate-fx-move (fn [{:keys [stop layout]}]
                           (let [[x1 y1] stop
                                 animated (.-Animated ReactNative)
                                 Value (.-ValueXY animated)
                                 position (Value. #js {:x x1 :y y1})]
                             (do
                               (dispatch [layout (.getLayout position)])))))

#_(reg-event-fx :interpolate-rotate
              (fn [{:keys [db]} [_ style-event]]
                {:db (assoc-in db
                               [:animate-db
                                style-event
                                :rotate]
                               (get-in db [:animate-db style-event
                                           position]))}))

(reg-event-fx :animate-fx-move3
              (fn [{:keys [db]}  [_ [x1 y1] style-event]]
                {:db (assoc db :animate-db
                            (let [animated (.-Animated ReactNative)
                                  Value (.-ValueXY animated)
                                  position (Value. #js {:x x1 :y y1})]
                              {:obj animated
                               style-event
                               (let [pos (.getLayout position)
                                     rotate (oops/ocall
                                             position [:x :interpolate]
                                             #js {:inputRange #js [-500 0 500]
                                                  :outputRange #js ["-120deg" "0deg" "120deg"]} )
                                     o #js {:transform #js [ #js {:rotate rotate}]}
                                     e (goog.object/extend o pos)]
                                 {:layout o
                                  :position position
                                  :rotate rotate})}
                              ))}))

(reg-event-fx :animate-move
              (fn [_ [_ [x1 y1] ]]
                { :animate-fx-move
                 {:stop [x1 y1]
                  :layout :set-style-v1}}))


(reg-event-fx :animate (fn [{:keys [db]} [_ [x1 y1] [x2 y2]]]
                         {:db (assoc db :cord [[x1 y1] [x2 y2]])
                          :animate-spring {:start [x1 y1]
                                           :stop [x2 y2]
                                           :layout :set-style-v1}}))

(reg-event-fx :animate-last (fn [{:keys [db]} [_ [x2 y2]]]
                              {:db (assoc db :cord [(last (:cord db))  [x2 y2]])
                               :animate-spring {:start (last (:cord db))
                                                :stop [x2 y2]
                                                :layout :set-style-v1}}))

(reg-event-fx :animate-position (fn [{:keys [db]} [_ [x2 y2]]]
                                  {:db (assoc db :cord [(last (:cord db))  [x2 y2]])
                                   :animate-spring {:start (last (:cord db))
                                                :stop [x2 y2]
                                                :layout :set-style-v1}}))
#_(dispatch [:animate-fx-move3 [100 10] :style-x])
#_(oops/ocall @(rf/subscribe [:style-x-test1])  "x.interpolate"
              #js {:inputRange #js [-500 0 500]
                   :outputRange #js ["-120deg" "0deg" "120deg"]} )
