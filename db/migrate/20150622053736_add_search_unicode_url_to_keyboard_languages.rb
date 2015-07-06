class AddSearchUnicodeUrlToKeyboardLanguages < ActiveRecord::Migration
  def change
    add_column :keyboard_languages, :search_unicode_url, :string
  end
end
