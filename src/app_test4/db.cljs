(ns app-test4.db
  (:require [clojure.spec.alpha :as s]))

;; spec of app-db
(s/def ::greeting string?)
(s/def ::app-db
  (s/keys :req-un [::greeting]))

;; initial state of app-db
(def app-db {:greeting "Hello Clojure in iOS and Android!"
             :cord [[0 0] [0 0]]
             :cards [{:id 1,
                      :text "card #1",
                      :url
                      "http://imgs.abduzeedo.com/files/paul0v2/unsplash/unsplash-04.jpg"}
                     {:id 2,
                      :text "card #2",
                      :url
                      "http://www.fluxdigital.co/wp-content/uploads/2015/04/Unsplash.jpg"}
                     {:id 3,
                      :text "card #3",
                      :url
                      "http://imgs.abduzeedo.com/files/paul0v2/unsplash/unsplash-09.jpg"}
                     {:id 4,
                      :text "card #4",
                      :url
                      "http://imgs.abduzeedo.com/files/paul0v2/unsplash/unsplash-01.jpg"}
                     {:id 5,
                      :text "card #5",
                      :url
                      "http://imgs.abduzeedo.com/files/paul0v2/unsplash/unsplash-04.jpg"}
                     {:id 6,
                      :text "card #6",
                      :url
                      "http://www.fluxdigital.co/wp-content/uploads/2015/04/Unsplash.jpg"}
                     {:id 7,
                      :text "card #7",
                      :url
                      "http://imgs.abduzeedo.com/files/paul0v2/unsplash/unsplash-09.jpg"}
                     {:id 8,
                      :text "card #8",
                      :url
                      "http://imgs.abduzeedo.com/files/paul0v2/unsplash/unsplash-01.jpg"}]})
