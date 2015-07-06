class AddColumnsToKeyboardType < ActiveRecord::Migration
  def change
    add_column :keyboard_types, :os, :string
    add_column :keyboard_types, :os_variant, :string
  end
end
