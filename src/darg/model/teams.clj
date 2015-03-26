(ns darg.model.teams
  (:require [darg.db.entities :as db]
            [korma.core :refer :all]))

; Create

(defn create-team
  "Insert a team into the database
  Takes a map of fields to insert into the db.
  Requires:
    :email - the unique email address for the team (string)
    :name - the name of the team (string)"
  [params]
  (insert db/team (values params)))

; Getters

(defn fetch-team
  "Returns a vector containing matching teams from the db
  Takes a map of fields to use in db lookup"
  [params]
  (select db/team (where params)))

(defn fetch-one-team
  "Returns the first team returned by fetch-team"
  [params]
  (first (fetch-team params)))

(defn fetch-team-id
  "Returns the id of the user based on submitted fields
  Takes a map of fields for use in db lookup"
  [params]
  (:id (first (select db/team (fields :id) (where params)))))

(defn fetch-team-by-id
  "Returns a team map from the db
  Takes a team id as an integer"
  [id]
  (first (select db/team (where {:id id}))))

; Update

(defn update-team
  "Updates the fields for a team.
  Takes a team-id as an integer and a map of fields + values to update."
  [id params]
  (update db/team (where {:id id}) (set-fields params)))

; Team Membership
(defn fetch-team-users
  "Gets the list of users for a given team"
  [id]
  (:darg.user (first (select db/team
                       (where {:id id})
                       (with db/user)))))

