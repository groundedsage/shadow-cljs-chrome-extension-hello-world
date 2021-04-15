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

(defn post-message [port type data]
  (.postMessage port #js {:hello-console-panel-message (pr-str {:type type :data data :timestamp (js/Date.)})
                          :tab-id        current-tab-id}))

(let [port (js/chrome.runtime.connect #js {:name "hello-console-panel"})]
  (.addListener (.-onMessage port)
                (fn [msg]
                  (js/console.log #js {:message msg
                                       :port port})))
  (js/console.log "the port:" port)
  (.postMessage (js/chrome.runtime.connect #js {:name "datalog-console-devtool"}) #js {:datalog-console-devtool-message "init" :tab-id js/chrome.devtools.inspectedWindow.tabId})
  (post-message port :hello-console/type {}))