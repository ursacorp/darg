(ns darg.fixtures.model
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c :refer [to-sql-date]]
            [darg.db.entities :refer :all]
            [darg.model.user :refer [encrypt-password]]
            [korma.core :refer :all]))

(def test-user-1
  {:email "savelago@darg.io"
   :password (encrypt-password "butts")
   :name "John Lago"
   :timezone "America/Los_Angeles"
   :email_hour "1pm"
   :admin true
   :bot false
   :active true})

(def test-user-2
  {:email "domo@darg.io"
   :password (encrypt-password "cigarettes")
   :name "Domo the Robot"
   :timezone "UTC"
   :email_hour "5pm"
   :admin false
   :bot false
   :active true})

(def test-user-3
  {:email "davidst@darg.io"
   :password (encrypt-password "nihon")
   :name "The Couch"
   :timezone "UTC"
   :email_hour "2pm"
   :admin false
   :bot false
   :active true})

(def test-user-4
  {:email "test-user2@darg.io"
   :password (encrypt-password "samurai")
   :name "Finn the Human"
   :timezone "America/Los_Angeles"
   :email_hour "7pm"
   :admin false
   :bot false
   :active true})

(def test-user-5
  {:email "test@darg.io"
   :password (encrypt-password "ohmyglob")
   :name "LSP"
   :timezone "America/New_York"
   :email_hour "8pm"
   :admin false
   :bot false
   :active true})

(def test-user-6
  {:email "david@ursacorp.io"
   :password (encrypt-password "bloodthirst")
   :name "David Jarvis"
   :timezone "America/Los_Angeles"
   :email_hour "4pm"
   :admin true
   :bot false
   :active true})

(def test-user-7
  {:email "venantius@gmail.com"
   :password (encrypt-password "bongle5")
   :name "Dave"
   :timezone "America/Los_Angeles"
   :email_hour "7pm"
   :admin true
   :bot false
   :active true})

(def test-users
  [test-user-1
   test-user-2
   test-user-3
   test-user-4
   test-user-5
   test-user-6
   test-user-7])

(def test-github-user-1
  {:gh_login "dargtester1"
   :id 10094188
   :gh_avatar_url "https://avatars.githubusercontent.com/u/10094188?v=3"
   :github_token_id nil
   :darg_user_id 4})

(def test-github-users
  [test-github-user-1])

(def test-team-1
  {:name "Darg"
   :email "darg@mail.darg.io"})

(def test-team-2
  {:name "Standard Treasury"
   :email "st@mail.darg.io"})

(def test-team-3
  {:name "Jake n Cake"
   :email "jncake@mail.darg.io"})

(def test-teams
  [test-team-1
   test-team-2
   test-team-3])

(def test-task-1
  {:date nil
   :timestamp (c/to-sql-time (t/now))
   :user_id 4
   :team_id 1
   :task "Destroyed the Enchiridion."
   :type "task"})

(def test-task-2
  {:date nil
   :timestamp (c/to-sql-time (t/now))
   :user_id 2
   :team_id 2
   :task "Destroy all humans"
   :type "task"})

(def test-task-3
  {:date (c/to-sql-date (t/today))
   :timestamp nil
   :user_id 4
   :team_id 1
   :task "Went out on a date with Flame Princess!"
   :type "email"})

(def test-task-4
  {:date nil
   :timestamp (c/to-sql-time (t/now))
   :user_id 2
   :team_id 3
   :task "Once more into the breach"
   :type "task"})

(def test-task-5
  {:date nil
   :timestamp (c/to-sql-time (t/minus (t/now) (t/days 1)))
   :user_id 4
   :team_id 1
   :task "Some folks call it a kaiser blade, me I call it a sling blade"
   :type "task"})

(def test-task-6
  {:date (c/to-sql-date (t/today))
   :timestamp nil
   :user_id 4
   :team_id 2
   :task "Defeated the ice king."
   :type "email"})

(def test-task-7
  {:date nil
   :timestamp (c/to-sql-time (t/now))
   :user_id 6
   :team_id 2
   :task "Got a banking charter!"
   :type "task"})

(def test-task-8
  {:date nil
   :timestamp (c/to-sql-time (t/now))
   :user_id 4
   :team_id 2
   :task "Rescued Princess Bubblegum and saved the candy kingdom from disaster once again. It's a wonder they get anything done over there."
   :type "task"})

(def test-tasks
  [test-task-1
   test-task-2
   test-task-3
   test-task-4
   test-task-5
   test-task-6
   test-task-7
   test-task-8])

(def test-role-pair-1
  {:user_id 1
   :team_id 1
   :admin true})

(def test-role-pair-2
  {:user_id 3
   :team_id 1})

(def test-role-pair-3
  {:user_id 1
   :team_id 2})

(def test-role-pair-4
  {:user_id 4
   :team_id 2
   :admin true})

(def test-role-pair-5
  {:user_id 4
   :team_id 1
   :admin true})

(def test-role-pair-6
  {:user_id 2
   :team_id 3
   :admin false})

(def test-role-pair-7
  {:user_id 3
   :team_id 3
   :admin false})

(def test-role-pair-8
  {:user_id 6
   :team_id 1
   :admin true})

(def test-role-pair-9
  {:user_id 6
   :team_id 2
   :admin true})

(def test-role-pair-10
  {:user_id 7
   :team_id 1})

(def test-role-pairs
  [test-role-pair-1
   test-role-pair-2
   test-role-pair-3
   test-role-pair-4
   test-role-pair-5
   test-role-pair-6
   test-role-pair-7
   test-role-pair-8
   test-role-pair-9
   test-role-pair-10])

(def test-password-reset-token-1
  {:token "XBT6XI7WAHPX4NQDHBWGXPP2YCJSXS7Q"
   :user_id 1
   :expires_at (c/to-sql-time (t/plus (t/now) (t/days 1)))})

(def test-password-reset-token-2
  ;; already expired
  {:token "T3HLQG5QEPDF6K26Y2OQTFJGNOD2WYI7"
   :user_id 2
   :expires_at (c/to-sql-time (t/now))})

(def test-password-reset-tokens
  [test-password-reset-token-1
   test-password-reset-token-2])

(def test-user-email-confirmation-1
  {:user_id 4
   :token "KJGWF37QJ3A7FRTMVGFGHC7Y3X4CCLOANY6QCJYVVDIWDF4TXF65LL52"})

(def test-user-email-confirmations
  [test-user-email-confirmation-1])
