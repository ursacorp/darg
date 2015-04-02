(ns darg.controller.team-test
  (:require [clojure.test :refer :all]
            [darg.controller.team :as api]
            [darg.model.role :as role]
            [darg.model.team :as team]
            [darg.fixtures :refer [with-db-fixtures]]))

(with-db-fixtures)

(deftest team-creation-endpoint-works
  (let [request {:request-method :post 
                 :params {:name "Google, Inc."
                          :email "google@mail.darg.io"}
                 :user {:id 4 :email "test-user2@darg.io"}}
        {:keys [body status]} (api/create! request)
        team-id (:id body)
        created-role (role/fetch-one-role {:user_id 4 :team_id team-id})]
    (is (= status 200))
    (is (some? created-role))
    (is (true? (:admin created-role)))
    (is (some? (team/fetch-one-team {:id (:id body)})))))

(deftest we-cant-create-a-team-with-an-existing-email
  (let [request {:request-method :post 
                 :params {:name "Standard Treasury 2"
                          :email "st@mail.darg.io"}
                 :user {:id 4 :email "test-user2@darg.io"}}
        response (api/create! request)]
    (is (= (:status response) 409))))

(deftest we-can-update-a-team
  (let [request {:request-method :post 
                 :params {:name "Darg sucks as a name"
                          :email "test.api@mail.darg.io"
                          :id "1"}
                 :user {:id 4 :email "test-user2@darg.io"}}
        response (api/update! request)
        team (team/fetch-one-team {:id 1})]
    (is (= (:status response) 200))
    (is (= (:name team)
           "Darg sucks as a name"))))

(deftest we-cant-update-a-team-if-were-not-the-admin
  (let [request {:request-method :post 
                 :params {:name "Darg sucks as a name"
                          :email "test.api@mail.darg.io"
                          :id "1"}
                 :user {:id 3 :email "arrigato@darg.io"}}
        response (api/update! request)
        team (team/fetch-one-team {:id 1})]
    (is (= (:status response) 401))))

(deftest we-cant-update-a-team-with-an-email-that-another-team-has
  (let [request {:request-method :post 
                 :params {:name "Darg sucks as a name"
                          :email "st@mail.darg.io"
                          :id "1"}
                 :user {:id 4 :email "test-user2@darg.io"}}
        response (api/update! request)
        team (team/fetch-one-team {:id 1})]
    (is (= (:status response) 409))
    (is (= (:name team)
           "Darg"))))