(ns chrome.extension.content-script.main
  (:require [goog.object :as gobj]
            [cljs.core.async :as async :refer [go go-loop chan <! put!]]))


 (defn init []
   (println "Content script init"))


(let [port (js/chrome.runtime.connect #js {:name "hello-console-remote"})]
  (.addListener (gobj/get port "onMessage")
                (fn [msg]
                  (println "MSG" msg)
                  (.postMessage js/window "pinging ya back" "*"))))