class AddIsoRegionAndLanguageToKeyboard < ActiveRecord::Migration
  def change
    add_column :keyboards, :iso_region, :string
    add_column :keyboards, :iso_language, :string
  end
end
