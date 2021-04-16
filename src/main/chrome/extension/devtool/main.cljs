(ns chrome.extension.devtool.main
  (:require
   [clojure.pprint :refer [pprint]]
   [cljs.reader]
   [cljs.core.async :as async :refer [go <! put!]]
   [com.wsscode.common.async-cljs :refer [<?maybe]]
   [goog.object :as gobj]
   [taoensso.timbre :as log]))


(println ::loaded)


(def current-tab-id js/chrome.devtools.inspectedWindow.tabId)

(def create-port #(js/chrome.runtime.connect #js {:name %}))

(defn post-message [port type data]
  (.postMessage port #js {:hello-console-panel-message (pr-str {:type type :data data :timestamp (js/Date.)})
                          :tab-id        current-tab-id}))



(let [port (create-port "hello-console-panel")]
  (.addListener (.-onMessage port)
                (fn [msg]
                  (js/console.log #js {:message msg
                                       :port port})))
  (js/console.log #js {:port port
                       :tab-id js/chrome.devtools.inspectedWindow.tabId})
  (post-message port :hello-console/type {}))

