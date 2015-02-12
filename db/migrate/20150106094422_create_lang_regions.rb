class CreateLangRegions < ActiveRecord::Migration
  def change
    create_table :lang_regions do |t|
      t.references :keyboardCountry, index: true
      t.references :keyboardLanguages, index: true

      t.timestamps
    end
  end
end
