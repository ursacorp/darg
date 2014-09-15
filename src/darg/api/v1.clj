(ns darg.api.v1
  (:require [clojure.tools.logging :as logging]
            [clojure.string :as str :only [split trim]]
            [darg.model.tasks :as tasks]
            [darg.model.users :as users]
            [darg.model.teams :as teams]
            [darg.db-util :as dbutil]
            [darg.services.stormpath :as stormpath]
            [korma.core :refer :all]
            [ring.middleware.session.cookie :as cookie-store]
            [ring.middleware.session.store :as session-store]
            [slingshot.slingshot :refer [try+]]))

;; Authentication

(defn login
  "/v1/api/login

  Authentication endpoint. Routes input parameters to Stormpath for
  authentication; if successful, we set auth in their session and
  update the cookie to indicate that they're now logged in."
  [request-map]
  (let [email (-> request-map :params :email)
        password (-> request-map :params :password)]
    (try+
      ;;(stormpath/authenticate email password)
      (logging/info "Successfully authenticated with email" email)
      {:body "Successfully authenticated"
       :cookies {"logged-in" {:value true :path "/"}}
       :session {:authenticated true :email (get request-map :email)}
       :status 200}
      ;; Stormpath will return a 400 status code on failed auth
      (catch [:status 400] response
        (logging/info "Failed to authenticate with email " email)
        {:body "Failed to authenticate"
         :session {:authenticated false}
         :status 401}))))

(defn logout
  "/api/v1/logout

  The other half of the authentication endpoint pair. This one clears
  your session cookie and your plaintext cookie, logging you out both
  in practice and appearance"
  [request-map]
  {:body (str request-map)
   :status 200
   :session nil
   :cookies {"logged-in" {:value false :max-age 0 :path "/"}}})

(defn signup
  "/api/v1/signup

  TODO"
  [request-map]
  (let [request (-> request-map :params)]
  (try+
    (stormpath/create-account request)
    (logging/info "Successfully created account" (:email request))
    ;On success, write the request map to the database
    (users/create-stormpath-account-as-user request)
    ;Create a response
    {:body "Account successfully created"
     :cookies {"logged-in" {:value true :path "/"}}
     :session {:authenticated true :email (get request-map :email)}
     :status 200}
    (catch [:status 400] response
      (logging/info "Failed to create account")
      {:body "Failed to create account"
       :status 401})
    (catch [:status 409] response
      (logging/info "Account already exists")
      {:body "Account already exists"
       :status 409}))))

;; our logging problem is very similar to https://github.com/iphoting/heroku-buildpack-php-tyler/issues/17
(defn parse-forwarded-email
  "Parse an e-mail that has been forwarded by Mailgun"
  [body]
  (let [params (:params body)
        {:keys [recipient sender From subject
                body-plain stripped-text stripped-signature
                body-html stripped-html attachment-count
                attachment-x timestamp token signature
                message-headers content-id-map]} params]
    (logging/info "Mailgun Params: " params)
    (logging/info "Full Mailgun POST: " body)
    (str params)))

(defn parse-email
"/api/v1/parse-email

Recieves a darg email from a user, parses tasklist, and inserts the tasks into the database.
email mapping to task metadata is:
From -> uses email address to lookup :users_id
Recipient -> uses email address to lookup :teams_id
Subject -> parses out date in format 'MMM dd YYYY' and converts to sqldate for :date
Body -> Each newline in the body is parsed as a separate :task"
  [email]
  (let [task-list (-> email
                    (get :body-plain)
                    (str/split #"\n")
                    (->> (map str/trim)))
        email-metadata {:users_id (users/get-userid {:email (:from email)})
                        :teams_id (teams/get-teamid {:email (:recipient email)})
                        :date (dbutil/sql-date-from-subject (:subject email))}]
    (tasks/create-task-list task-list email-metadata)))
