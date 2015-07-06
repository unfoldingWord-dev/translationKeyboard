class CreateKeyboardTypeDefaultKeyPositions < ActiveRecord::Migration
  def change
    create_table :keyboard_type_default_key_positions do |t|
      t.integer :row_index
      t.integer :col_count
      t.integer :keyboard_type_id

      t.timestamps
    end
  end
end
