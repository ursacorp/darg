(ns darg.db.entities
  (:require [korma.core :refer :all]))

(declare
 user
 team
 task
 repo
 github-user
 github-token
 github-issue
 github-pushe
 github-pull-request)

(defentity user
  (table :darg.user :user)
  (has-many task)
  (belongs-to github-user)
  (many-to-many team :darg.team_user))

(defentity team
  (table :darg.team :team)
  (has-many task)
  (has-many repo :team_repo)
  (many-to-many user :darg.team_user))

(defentity team-user
  (table :darg.team_user :team_user)
  (has-many team {:fk :team_id})
  (has-many user {:fk :user_id}))

(defentity task
  (table :darg.task :task)
  (belongs-to user {:fk :user_id})
  (belongs-to team {:fk :team_id}))

(defentity password-reset-token
  (table :darg.password_reset_token :password_reset_token)
  (belongs-to user {:fk :user_id}))

(defentity github-repo
  (table :github.repo :team_repo)
  (many-to-many team :darg.team_repo)
  (has-many github-issue)
  (has-many github-pushe)
  (has-many github-pull-request))

(defentity github-user
  (table :github.user :github_user)
  (has-one user)
  (belongs-to github-token)
  (has-many github-issue)
  (has-many github-pushe)
  (has-many github-pull-request))

(defentity github-token
  (table :github.token :github_token)
  (has-one github-user))

(defentity team-repo
  (table :darg.team_repo :team_repo)
  (has-many team {:fk :team_id}))

(defentity github-issue
  (table :github.issue :github_issue)
  (belongs-to github-user)
  (belongs-to github-repo {:fk :gh_repo_id}))

(defentity github-push
  (table :github.push :github_push)
  (belongs-to github-user)
  (belongs-to github-repo {:fk :github_repo_id}))

(defentity github-pull-request
  (table :github.pull_request :github_pull_request)
  (belongs-to github-user)
  (belongs-to github-repo {:fk :github_repo_id}))