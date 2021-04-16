(ns chrome.extension.background.main
  (:require [goog.object :as gobj]
            [cljs.core.async :as async :refer [go-loop chan <! put!]]))


(println ::loaded)

(defonce tools-conns* (atom {}))


(defn handle-devtool-message [message _port]
  (println "handling devtool message:" (gobj/get message "name"))
  (cond
    (= "init" (gobj/get message "name"))
    (let [tab-id (gobj/get message "tab-id")]
      (js/console.log #js {:name "init" :tab-id tab-id :message message}))

    (gobj/getValueByKeys message "hello-console-panel-message")
    (let [tab-id      (gobj/get message "tab-id")]
      (js/console.log #js {:name "hello-console-panel-message" :tab-id tab-id :message message}))))


(defn handle-remote-message [message port]
  (js/console.log "handle-remote-message:" #_message #_port)
  (cond
    ; send message to devtool
    (gobj/getValueByKeys message "hello-console-remote")
    (let [tab-id (gobj/getValueByKeys port "sender" "tab" "id")]
      (js/console.log {:tab-id tab-id :message message})

      ; ack message received
      (when-let [id (gobj/getValueByKeys message "__hello-console-msg-id")]
        (.postMessage port #js {:ack "ok" "__hello-console-msg-id" id})))))

(js/chrome.runtime.onConnect.addListener 
 (fn [port]
   (case (gobj/get port "name")
     
     "hello-console-remote"
     (let [listener (partial handle-remote-message port)
           tab-id   (gobj/getValueByKeys port "sender" "tab" "id")]
        (js/console.log #js {:port port
                             :tab-id tab-id})
       (.addListener (gobj/get port "onMessage") listener)
       #_(.addListener (gobj/get port "onDisconnet")
                     (fn [port]
                       (.removeListener  (gobj/get port "onMessage") listener))))
     
     "hello-console-panel"
     (let [listener (partial handle-devtool-message port)]
       (js/console.log "devtool port: " port)
       (.addListener (gobj/get port "onMessage") listener)
       #_(.addListener (gobj/get port "onDisconnet")
                     (fn [port]
                       (.removeListener  (gobj/get port "onMessage") listener))))


     (js/console.log "Ignoring connection:" (gobj/get port "name")))))
