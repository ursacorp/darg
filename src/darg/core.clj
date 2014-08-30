(ns darg.core
  (:gen-class) ;; need this for the main method
  (:require [compojure.core :refer [defroutes GET]]
            [ring.adapter.jetty :as ring]))

(defroutes routes
  (GET "/" [] "<h2>Hello World</h2>"))

(defn -main []
  (ring/run-jetty #'routes {:port (Integer. (or (System/getenv "PORT") "8080"))
                            :join? false}))
