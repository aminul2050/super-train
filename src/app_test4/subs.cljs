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

(reg-sub :style-x-test2
         (fn [db] (get-in db [:animate-db :style-x :layout])))

(reg-sub :style-x-test1
           (fn [db] (get-in db [:animate-db :style-x :position] )))
