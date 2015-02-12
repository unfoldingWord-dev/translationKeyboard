class CreateKeyboardCountries < ActiveRecord::Migration
  def change
    create_table :keyboard_countries do |t|
      t.string :cc

      t.timestamps
    end
  end
end
