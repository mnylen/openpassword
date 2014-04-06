(ns openpassword.ui
  (:require [openpassword.ui.unlock-vault]
            [openpassword.ui.entry-list]
            [openpassword.ui.eventbus]))

(defn ^:export init []
  (openpassword.ui.unlock-vault/init)
  (openpassword.ui.entry-list/init)
  (openpassword.ui.eventbus/trigger :unlock-vault)
  (openpassword.ui.eventbus/listen :vault-unlocked #(.log js/console "Successfully unlocked vault")))

