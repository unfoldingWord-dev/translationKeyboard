class CreateKeyboardLanguages < ActiveRecord::Migration
  def change
    create_table :keyboard_languages do |t|
      t.string :ln
      t.string :lc
      t.string :lr

      t.timestamps
    end
  end
end
