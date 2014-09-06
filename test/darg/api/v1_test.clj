(ns darg.api.v1-test
  (:require [clojure.test :refer :all]))

(def test-received-params
  ;; this is an example of what we actually get forwarded to us from Mailgun
  {:stripped-html "<p>Dancing tiem!!</p><p>Aint it a thing?</p>"
   :From "butts@darg.io"
   :message-headers [["Received", "by luna.mailgun.net with HTTP; Tue, 02 Sep 2014 01:51:29 +0000",]
                     ["Mime-Version", "1.0"],
                     ["Content-Type", "text/plain; charset=\"ascii\""],
                     ["Subject", "Let's dance mofo"],
                     ["From", "butts@darg.io"],
                     ["To", "test.api@darg.io"],
                     ["Message-Id", "<20140902015129.23125.83955@darg.io>"],
                     ["Content-Transfer-Encoding", "7bit"]],
   :stripped-signature ""
   :signature "ad94075a8d99b540f4cb4aa0847eb49328bde219a67fb5639448b559a1b92102"
   :recipient "test.api@darg.io"
   :stripped-text "Dancing tiem!!
   Aint it a thing?"
   :Subject "Let's dance mofo"
   :Mime-Version 1.0
   :token "ee55af9ce04725e2b93ca5844b14621ac96de7e9144b21222f"
   :from "butts@darg.io"
   :Received "by luna.mailgun.net with HTTP; Tue, 02 Sep 2014 01:51:29 +0000"
   :sender "butts@darg.io"
   :Message-Id "<20140902015129.23125.83955@darg.io>"
   :To "test.api@darg.io"
   :Content-Transfer-Encoding "7bit"
   :timestamp 1409622691
   :Content-Type "text/plain; charset=\"ascii\""
   :subject "Let's dance mofo"
   :body-plain "Dancing tiem!!
   Aint it a thing?"})

(deftest email-sent-to-us-is-parseable
  (is (= 1 1))
  )
