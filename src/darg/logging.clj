(ns darg.logging
  (:use clojure.tools.logging
        clj-logging-config.log4j))

;; http://www.codejava.net/coding/common-conversion-patterns-for-log4js-patternlayout
(set-loggers! :root {:level :info
                     :out org.apache.log4j.ConsoleAppender
                     :pattern "[%p] %d{MM-dd-yyyy HH:mm:ss} | %m%n"})