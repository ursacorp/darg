(ns darg.api.v1-test
  (:use [darg.fixtures]
        [korma.core])
  (:require [clojure.test :refer :all]
            [darg.api.v1 :as api]
            [darg.core :as core]
            [darg.db :as db]
            [darg.services.stormpath-test :as stormpath-test]
            [ring.mock.request :as mock-request]
            [darg.fixtures.email :as f-email]
            [darg.model.tasks :as tasks]
            [darg.model.users :as users]
            [darg.model.teams :as teams]
            [darg.model :as table]
            [darg.services.stormpath :as stormpath]))

(with-db-fixtures)

;; /api/v1/login

(deftest i-can-login-and-it-set-my-cookies
  (let [auth-response (core/app (mock-request/request
                                  :post "/api/v1/login"
                                  {:email (:email stormpath-test/user-2)
                                   :password (:password stormpath-test/user-2)}))]
    (is (= (:body auth-response) "Successfully authenticated"))
    (is (= (:status auth-response) 200))
    (is (some #{"logged-in=true;Path=/"}
              (get (:headers auth-response) "Set-Cookie")))))

(deftest i-can't-login-and-it-don't-set-no-cookies
  (let [auth-response (core/app (mock-request/request
                                  :post "/api/v1/login"
                                  {:email (:email stormpath-test/user-1)
                                   :password (:password stormpath-test/user-1)}))]
    (is (= (:body auth-response) "Failed to authenticate"))
    (is (= (:status auth-response 401)))
    (is (not (some #{"logged-in=true;Path=/"}
                   (get (:headers auth-response) "Set-Cookie"))))))

(deftest i-can-register-and-it-wrote-to-the-database-and-cookies
  (let [auth-response (core/app (mock-request/request 
    :post "/api/v1/signup"
    stormpath-test/user-1))]
  (is (= (:body auth-response) "Account successfully created"))
  (is (= (:status auth-response 200)))
  (is (not (empty? (users/get-user-by-field {:email "test-user@darg.io"}))))
  (is (some #{"logged-in=true;Path=/"}
    (get (:headers auth-response) "Set-Cookie")))
  (stormpath/delete-account-by-email (:email stormpath-test/user-1))))

(deftest i-cant-write-the-same-thing-twice
  (let [auth-response (core/app (mock-request/request 
    :post "/api/v1/signup"
    stormpath-test/user-2))]
  (is (= (:body auth-response) "Account already exists"))
  (is (= (:status auth-response 409)))))

(deftest signup-failure-does-not-write-to-database-and-sets-no-cookies
  (let [auth-response (core/app (mock-request/request 
    :post "/api/v1/signup"
    stormpath-test/quasi-user))]
  (is (= (:body auth-response) "Failed to create account"))
  (is (= (:status auth-response 400)))
  (is (empty? (users/get-user-by-field {:email "quasi-user@darg.io"})))
  (is (not (some #{"logged-in=true;Path=/"}
                   (get (:headers auth-response) "Set-Cookie"))))))
  ; (stormpath/delete-account-by-email (:email stormpath-test/quasi-user))))

;GET v1/user/darg

(deftest authenticated-user-can-view-their-darg
  (let [sample-request {:session {:authenticated true :email "test-user2@darg.io"}}
         auth-response (api/get-user-darg-list sample-request)]
    (is (= (:status auth-response) 200))
    (is (-> auth-response
            :body
            (contains? :tasks)))))

(deftest unauthenticated-user-cant-view-a-darg
  (let [sample-request {:session {:authenticated false :email "test-user2@darg.io"}}
         auth-response (api/get-user-darg-list sample-request)]
    (is (= (:status auth-response) 403))
    (is (= (:body auth-response) "User not authenticated"))))

(deftest user-cant-view-a-darg-without-an-email
  (let [sample-request {:session {:authenticated true}}
         auth-response (api/get-user-darg-list sample-request)]
    (is (= (:status auth-response) 403))
    (is (= (:body auth-response) "User not authenticated"))))


;; /api/v1/logout

; TODO - TEST HERE WITH A HEADLESS BROWSER

;; api/v1/email

(deftest parsed-email-is-written-to-db
  (api/parse-email f-email/test-email-2)
  (is (not (empty? (tasks/get-task-by-params {:task "Dancing tiem!!"}))))
  (is (not (empty? (tasks/get-task-by-params {:task "Aint it a thing?"})))))

(deftest we-can-get-a-users-task-list
  (api/parse-email f-email/test-email-2)
  (is (= (count (tasks/get-all-tasks-for-user-by-email "domo@darg.io")) 5)))

(deftest we-can-get-a-teams-task-list
  (api/parse-email f-email/test-email-2)
  (def test-team-id (teams/get-teamid {:email "test.api@darg.io"}))
  (is (= (count (tasks/get-all-tasks-for-team test-team-id)) 5)))
