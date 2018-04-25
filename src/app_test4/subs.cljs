(ns app-test4.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :get-greeting
  (fn [db]
    (:greeting db)))

(reg-sub :get-cards
 (fn [db]
   (:cards db)))

(reg-sub :get-style-v1
         (fn [db]
           (:style-v1 db)))

(reg-sub :get-coordinate
         (fn [db]
           (:cord db)))
