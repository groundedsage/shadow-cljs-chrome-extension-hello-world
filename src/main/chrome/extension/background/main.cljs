(ns chrome.extension.background.main
  (:require [goog.object :as gobj]
            [cljs.core.async :as async :refer [go-loop chan <! put!]]))


(println ::loaded)


(defn handle-devtool-message [message _port]
  (println "handling devtool message:" (gobj/get message "name"))
  (cond
    ;; (= "init" (gobj/get message "name"))
    ;; (let [tab-id (gobj/get message "tab-id")]
    ;;   (js/console.log #js {:name "init" :tab-id tab-id :message message}))

    (gobj/getValueByKeys message "hello-console-panel-message")
    (let [tab-id      (gobj/get message "tab-id")]
      (js/console.log #js {:name "hello-console-panel-message" :tab-id tab-id :message message}))))


(defn handle-remote-message [message port]
  (js/console.log "handle-remote-message" message port)
  (cond
    ; send message to devtool
    (gobj/getValueByKeys message "hello-console-remote-message")
    (let [tab-id (gobj/getValueByKeys port "sender" "tab" "id")]
      (js/console.log {:tab-id tab-id :message message})

      ; ack message received
      (when-let [id (gobj/getValueByKeys message "__hello-console-msg-id")]
        (.postMessage port #js {:ack "ok" "__hello-console-msg-id" id})))))

(js/chrome.runtime.onConnect.addListener 
 (fn [port]
   (js/console.log "port: " port)
   (let [listener (partial handle-devtool-message port)]
     (case (gobj/get port "name")
    ;;  "hello-console-remote"
    ;;  (.addListener (gobj/get port "onMessage") handle-remote-message)
       "hello-console-panel"
       (.addListener (gobj/get port "onMessage") listener)


       (js/console.log "Ignoring connection" (gobj/get port "name"))))))
