(ns darg.model.password-reset-tokens-test
  (:require [clojure.test :refer :all]
            [darg.fixtures :refer [with-db-fixtures]]
            [darg.model.password-reset-tokens :as password-reset-tokens]))

(with-db-fixtures)

(deftest create!-and-fetch-one-work
  (let [token (password-reset-tokens/create! {:users_id 4})]
    (let [fetched-token (password-reset-tokens/fetch-one {:users_id 4})]
      (is (= token fetched-token))
      (is (some? token))
      (is (not (empty? token)))
      (is (some? fetched-token)))))

(deftest fetch-one-valid-works
  (let [valid-token (password-reset-tokens/fetch-one-valid {:users_id 1})
        expired-token (password-reset-tokens/fetch-one-valid {:users_id 2})]
    (is (some? valid-token))
    (is (nil? expired-token))))

(deftest valid-token?-works
  (let [valid-token (password-reset-tokens/fetch-one {:users_id 1})
        expired-token (password-reset-tokens/fetch-one {:users_id 2})]
    (is (false? (password-reset-tokens/valid-token? expired-token)))
    (is (true? (password-reset-tokens/valid-token? valid-token)))))
