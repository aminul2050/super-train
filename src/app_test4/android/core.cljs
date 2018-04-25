(ns app-test4.android.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [app-test4.events]
            [app-test4.subs]))

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))

(def logo-img (js/require "./images/cljs.png"))

(def style-sheet (.-StyleSheet ReactNative))
(def animated-view (r/adapt-react-class (.. ReactNative -Animated -View)))

(def pan-responder (.-PanResponder ReactNative))
(def pan-handler (.create
                  pan-responder
                  (clj->js {:onStartShouldSetPanResponder #(do (js/console.log "onStartShouldSetPanResponder called") true)
                            :onMoveShouldSetPanResponder #(do (js/console.log "onMoveShouldSetPanResponder called") true)
                            :onPanResponderGrant #(js/console.log "onPanResponderGrant called..")
                            :onPanResponderMove (fn [event g]
                                                  (js/console.log "onPanResponderMove called.. event: " g))
                            :onPanResponderRelease #(js/console.log "onPanResponderRelease called..")
                            :onPanResponderTerminate #(js/console.log "onPanResponderTerminate called..")})))

(defn alert [title]
      (.alert (.-Alert ReactNative) title))

(defn app-root []
  (let [greeting (subscribe [:get-greeting])
        layout (subscribe [:get-style-v1])
        cards (subscribe [:get-cards])]
    (fn []
      [view
       [animated-view  {:style @layout}
        [view (merge (js->clj (.-panHandlers pan-handler))
                     { :style {:flex-direction "column"
                               :margin 10
                               :border-width 1
                               :padding-bottom 20
                               :border-color "#00000044"
                               :align-items "center"}})
         [text {:style {:align-self "stretch"
                        :elevation 1
                        :font-size 30
                        :font-weight "500"
                        :margin-bottom 20
                        :text-align "center"
                        :font-family "HelveticaNeue-CondenseBold"
                        :background-color "#34333644"
                        :color "#fff"
                        :position :relative}} @greeting]
         [image {:source logo-img
                 :style  {:width 80 :height 80 :margin-bottom 30}}]
         [touchable-highlight {:style {:margin 10
                                       :border-style "solid"
                                       :border-color "#355f27"
                                       :background-color "#60b044"
                                       :padding 10
                                       :border-radius 3
                                       :border-width (.-hairlineWidth style-sheet)
                                       :padding-left 20
                                       :padding-right 20}
                               :hit-slop #js {:top 100
                                              :left 5
                                              :right 5
                                              :bottom 10}
                               :underlay-color "#60b044cc"
                               :active-opacity .9
                               :on-press #(dispatch [:set-greeting "hello world2"])}
          [text {:style {:color "#e5dbda"
                         :font-size 19
                         :font-weight "500"
                         :font-family "Helvetica Neue"}} "Default HairLine"]]

         [view {:style {:padding-left 5
                        :padding-right 5
                        :flex-direction "row"
                        :justify-content :space-between} }
          [text {:style { :font-size 11
                         :color "#838786"}} "M Wang"]
          [text {:style {:font-size 11
                         :color "#838786"}} " , "]
          [text {:style {:font-size 11
                         :color "#838786"}} "Bdesh"]
          [text {:style { :font-size 11
                         :color "#838786"}} " , "]
          [text {:style {:font-size 11
                         :color "#838786"}} "4th April, 2342"]]]
        ]
       (map-indexed (fn [i card]
              [view {:key (str "hello-" i)
                     :style {:flex-direction "column"
                             :margin 10
                             :border-width 3
                             :height 200
                             :border-color "#FFFFFFCC"
                             :align-items :center
                             :justify-content :flex-end}}
               [text {:style {:align-self "stretch"
                              :elevation .2
                              :font-size 22
                              :font-weight "200"
                              :text-align :right
                              :padding-right 20
                              :font-family "HelveticaNeue-CondenseBold"
                              :background-color "#34333655"
                              :color "#FFFFFFAA"
                              }} (:text card)]
               [image {:source {:uri (:url card)}
                       :style  {:position :absolute
                                :width "100%" :height "100%"}}]])
            @cards)])))

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "AppTest4" #(r/reactify-component app-root)))
