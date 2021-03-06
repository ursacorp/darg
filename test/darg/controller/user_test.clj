(ns darg.controller.user-test
  (:require [bond.james :as bond]
            [cheshire.core :as json]
            [clojure.test :refer :all]
            [darg.controller.user :as api]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.fixtures.model :as model-fixtures]
            [darg.model.user :as user]
            [darg.process.server :as server]
            [ring.mock.request :as mock-request]))

(with-db-fixtures)

(deftest i-can-signup-and-it-wrote-to-the-database-and-cookies
  (with-redefs [darg.services.mailgun/send-message (constantly true)]
    (let [auth-response (server/app (mock-request/request
                                     :post "/api/v1/user"
                                     {:email "dummy@darg.io"
                                      :password "test"
                                      :name "Crash dummy"}))]
      (is (= (json/parse-string (:body auth-response) true)
             {:message "Account successfully created"}))
      (is (= (:status auth-response) 200))
      (is (not (empty? (user/fetch-user {:email "dummy@darg.io"}))))
      (is (some #{"logged-in=true;Path=/"}
                (get (:headers auth-response) "Set-Cookie"))))))

(deftest ^:integration signup-sends-confirmation-email
  (with-redefs [darg.services.mailgun/send-message (constantly true)]
    (bond/with-spy [darg.services.mailgun/send-message]
      (let [auth-response (server/app (mock-request/request
                                       :post "/api/v1/user"
                                       {:email "dummy@darg.io"
                                        :password "test"
                                        :name "Crash dummy"}))]
        (is (= 1 (-> darg.services.mailgun/send-message bond/calls count)))))))

(deftest i-cant-write-the-same-thing-twice
  (let [user (select-keys model-fixtures/test-user-4
                          [:email :name :password])
        auth-response (server/app (mock-request/request
                                   :post "/api/v1/user"
                                   user))]
    (is (= (json/parse-string (:body auth-response) true)
           {:message "A user with that e-mail already exists."}))
    (is (= (:status auth-response) 409))))

(deftest signup-failure-does-not-write-to-database-and-sets-no-cookies
  (let [auth-response (server/app (mock-request/request
                                   :post "/api/v1/user"
                                   {:email "quasi-user@darg.io"}))]
    (is (= (json/parse-string (:body auth-response) true)
           {:message "The signup form needs an e-mail, a name, and a password."}))
    (is (= (:status auth-response) 400))
    (is (empty? (user/fetch-user {:email "quasi-user@darg.io"})))
    (is (not (some #{"logged-in=true;Path=/"}
                   (get (:headers auth-response) "Set-Cookie"))))))

(deftest update-user-works
  (with-redefs [darg.services.mailgun/send-message (constantly true)]
    (let [params {:email "test-user5@darg.io"
                  :name "Fiona the Human"
                  :id "4"
                  :send_digest_email "true"
                  :send_daily_email "true"}
          sample-request {:user {:email "test-user2@darg.io" :id 4}
                          :request-method :post
                          :params params}
          response (api/update! sample-request)]
      (is (= (:status response) 200))
      (is (some? (user/fetch-one-user {:email "test-user5@darg.io"}))))))

(deftest changing-the-email-requires-a-new-confirmation
  #_(is (= 0 1)))

(deftest we-cant-update-a-user-to-have-an-email-of-an-existing-user
  (let [params {:email "david@ursacorp.io"
                :name "David Jarvis"
                :id "4"
                :send_digest_email "true"
                :send_daily_email "true"}
        sample-request {:user {:email "test-user2@darg.io" :id 4}
                        :request-method :post
                        :params params}
        response (api/update! sample-request)]
    (is (= (:status response) 409))
    (is (= 1 (count (user/fetch-user {:email "david@ursacorp.io"}))))))

(deftest user-can-get-teammates-profile
  (let [sample-request {:user {:email "test-user2@darg.io" :id 4}
                        :request-method :get
                        :params {:id "1"}}
        response (api/get sample-request)]
    (is (= (:status response) 200))
    (is (= (:body response)
           (user/profile {:id 1})))))

(deftest user-cant-see-profile-for-non-teammate
  (let [sample-request {:user {:authenticated true :email "test-user2@darg.io" :id 4}
                        :request-method :get
                        :params {:id "2" :resource "profile"}}
        response (api/get sample-request)]
    (is (= (:status response) 401))
    (is (= (:body response)
           {:message "Not authorized."}))))
