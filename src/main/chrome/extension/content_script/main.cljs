(ns chrome.extension.content-script.main
  (:require [goog.object :as gobj]
            [cljs.core.async :as async :refer [go go-loop chan <! put!]]))

(println ::loaded)


(def port (js/chrome.runtime.connect #js {:name "hello-console-remote"}))

(.postMessage port #js {:hello-console-panel-message "test"})

(.addListener (gobj/get port "onMessage")
                (fn [msg]
                  (println "MSG" msg)
                  (.postMessage js/window "pinging ya back" "*")))